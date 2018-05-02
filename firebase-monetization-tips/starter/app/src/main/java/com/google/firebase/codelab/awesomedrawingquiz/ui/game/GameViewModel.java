// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.codelab.awesomedrawingquiz.ui.game;

import com.google.firebase.codelab.awesomedrawingquiz.data.Drawing;
import com.google.firebase.codelab.awesomedrawingquiz.data.DrawingDao;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameLevelClearEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameLevelClueUpdateEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameLevelEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameLevelSkipEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameStageClearEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameWrongAnswerEvent;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public final class GameViewModel extends ViewModel {

    private Disposable drawingRequestDisposable;

    private Disposable gameEventDisposable;

    private PublishSubject<GameEvent> gameEvents = PublishSubject.create();

    private final DrawingDao drawingDao;

    // Level-scoped information

    private int currentLevel = 1;

    private int numAttempts = 0;

    private int disclosedLettersByDefault = 1;

    private int disclosedLetters;

    private long levelStartTimeInMillis;

    private String clue;

    private boolean isHintAvailable;

    private boolean isHintUsed;

    private Drawing drawing;

    // Stage-scope information

    private int disclosedLettersOnReward = 1;

    private int gameDifficulty;

    private int numCorrectAnswers = 0;

    private List<String> seenWords = new ArrayList<>();

    public GameViewModel(DrawingDao drawingDao) {
        this.drawingDao = drawingDao;
    }

    void registerGameEventListener(Consumer<GameEvent> listener) {
        disposeIfNeeded(gameEventDisposable);
        gameEventDisposable = gameEvents
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener);
    }

    boolean isStarted() {
        return null != drawing;
    }

    void startLevel() {
        numCorrectAnswers = 0;
        gameDifficulty = GameSettings.DIFFICULTY_NORMAL;
        seenWords.clear();

        startLevel(1);
    }

    void checkAnswer(@NonNull String userAnswer) {
        numAttempts++;

        boolean correct = drawing.getWord().equalsIgnoreCase(userAnswer);
        if (correct) {
            numCorrectAnswers++;
            int elapsedTimeInSeconds =
                    (int) (System.currentTimeMillis() - levelStartTimeInMillis) / 1000;
            gameEvents.onNext(GameLevelClearEvent.create(
                    numAttempts, elapsedTimeInSeconds,
                    currentLevel == GameSettings.MAX_LEVEL, isHintUsed, drawing));
        } else {
            gameEvents.onNext(GameWrongAnswerEvent.create(drawing));
        }
    }

    boolean isHintAvailable() {
        return isHintAvailable && !isHintUsed;
    }

    Drawing getCurrentDrawing() {
        if (null == drawing) {
            throw new IllegalStateException("Level is not loaded yet");
        }
        return drawing;
    }

    void skipLevel() {
        int elapsedTimeInSeconds =
                (int) (System.currentTimeMillis() - levelStartTimeInMillis) / 1000;
        gameEvents.onNext(GameLevelSkipEvent.create(
                numAttempts, elapsedTimeInSeconds, isHintUsed, drawing));
        moveToNextLevel();
    }

    void moveToNextLevel() {
        if (currentLevel < GameSettings.MAX_LEVEL) {
            startLevel(currentLevel + 1);
        } else {
            finishStage();
        }
    }

    void useHint() {
        if (isHintUsed) {
            Log.e("GameViewModel", "Hint already used");
            return;
        }

        isHintUsed = true;
        disclosedLetters += disclosedLettersOnReward;

        clue = ClueGenerator.generate(drawing.getWord(), disclosedLetters);
        gameEvents.onNext(GameLevelClueUpdateEvent.create(clue, drawing));
    }

    private void startLevel(int newLevel) {
        numAttempts = 0;
        isHintAvailable = true;
        isHintUsed = false;
        currentLevel = newLevel;
        levelStartTimeInMillis = System.currentTimeMillis();

        applyDifficulty();
        requestNewDrawing();
    }

    private void applyDifficulty() {
        switch (gameDifficulty) {
            case GameSettings.DIFFICULTY_EASY:
                disclosedLettersByDefault = 2;
                break;
            case GameSettings.DIFFICULTY_HARD:
                disclosedLettersByDefault = 0;
                break;
            default:
                disclosedLettersByDefault = 1;
                break;
        }
        disclosedLetters = disclosedLettersByDefault;
    }

    private void requestNewDrawing() {
        disposeIfNeeded(drawingRequestDisposable);

        drawingRequestDisposable = drawingDao.getRandomDrawings(seenWords)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d -> {
                    clue = ClueGenerator.generate(d.getWord(), disclosedLetters);
                    seenWords.add(d.getWord());
                    drawing = d;

                    // Do not allow using a hint if it reveals all letters of the word
                    // after receiving a reward
                    if (disclosedLettersByDefault + disclosedLettersOnReward
                            >= drawing.getWord().length()) {
                        isHintAvailable = false;
                    }

                    gameEvents.onNext(GameLevelEvent.create(currentLevel, clue, d));
                });
    }

    private void finishStage() {
        gameEvents.onNext(GameStageClearEvent.create(numCorrectAnswers));
    }

    private void disposeIfNeeded(@Nullable Disposable d) {
        if (null != d && !d.isDisposed()) {
            d.dispose();
        }
    }

    @Override
    protected void onCleared() {
        disposeIfNeeded(drawingRequestDisposable);
        disposeIfNeeded(gameEventDisposable);
    }
}
