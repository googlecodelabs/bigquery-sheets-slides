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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.codelab.awesomedrawingquiz.AwesomeDrawingQuiz;
import com.google.firebase.codelab.awesomedrawingquiz.R;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameLevelClearEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameLevelClueUpdateEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameLevelEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameLevelSkipEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameStageClearEvent;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.event.GameWrongAnswerEvent;
import com.google.firebase.codelab.awesomedrawingquiz.view.QuickDrawView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static final String TAG = "GameActivity";

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9416413843";

    TextView tvLevelInfo;

    TextView tvHint;

    QuickDrawView qvDrawing;

    EditText etAnswer;

    Button btnHint;

    Button btnAnswer;

    Button btnSkip;

    FrameLayout flFullScreenDialog;

    TextView tvFullScreenDialogTitle;

    TextView tvFullScreenDialogMessage;

    Button btnFullScreenDialogAction;

    AlertDialog dlgAnswer;

    GameViewModel viewModel;

    RewardedVideoAd rewardedVideo;

    FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        viewModel = ViewModelProviders.of(this,
                ((AwesomeDrawingQuiz) getApplication()).provideViewModelFactory())
                .get(GameViewModel.class);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setupRewardedVideoAd();
        loadRewardedVideoAd();

        setupViews();
        setupAnswerInputDialog();
        setupViewModel();

        if (!viewModel.isStarted()) {
            showStageStartFullScreenDialog();
            QuizAnalytics.logStageStart(firebaseAnalytics);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        rewardedVideo.pause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rewardedVideo.resume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rewardedVideo.destroy(this);
    }

    private void setupViews() {
        tvLevelInfo = findViewById(R.id.tvActivityGameRound);
        tvHint = findViewById(R.id.tvActivityGameHint);
        btnHint = findViewById(R.id.btnActivityGameHint);
        btnAnswer = findViewById(R.id.btnActivityGameAnswer);
        btnSkip = findViewById(R.id.btnActivityGameSkip);
        qvDrawing = findViewById(R.id.qvActivityGame);

        btnHint.setOnClickListener(v ->
                showHintConfirmDialog()
        );

        btnAnswer.setOnClickListener(v ->
                showAnswerInputDialog()
        );

        btnSkip.setOnClickListener(v ->
                viewModel.skipLevel()
        );

        flFullScreenDialog = findViewById(R.id.flActivityGameFullScreenDialog);
        tvFullScreenDialogTitle = findViewById(R.id.tvActivityGameFullScreenDialogTitle);
        tvFullScreenDialogMessage = findViewById(R.id.tvActivityGameFullScreenDialogMessage);
        btnFullScreenDialogAction = findViewById(R.id.btnActivityGameFullScreenDialogAction);
    }

    private void setupAnswerInputDialog() {
        final View dialogView = LayoutInflater.from(this).inflate(
                R.layout.dialog_answer, null, false);
        etAnswer = dialogView.findViewById(R.id.et_dialog_answer);

        dlgAnswer = new AlertDialog.Builder(this)
                .setTitle(R.string.enter_your_answer)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, (d, which) ->
                        viewModel.checkAnswer(etAnswer.getText().toString())
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private void setupViewModel() {
        viewModel.registerGameEventListener(ev -> {
            if (ev instanceof GameLevelEvent) {
                GameLevelEvent round = (GameLevelEvent) ev;
                Log.d(TAG, "Round loaded: " + ev);

                QuizAnalytics.logLevelStart(firebaseAnalytics, round.drawing().getWord());

                tvLevelInfo.setText(getString(
                        R.string.level_indicator,
                        round.currentLevel(), GameSettings.MAX_LEVEL));

                tvHint.setText(round.clue());
                etAnswer.setHint(round.clue());

                qvDrawing.setDrawing(round.drawing());
                updateHintAvailability();
            } else if (ev instanceof GameLevelClueUpdateEvent) {
                GameLevelClueUpdateEvent event = (GameLevelClueUpdateEvent) ev;
                Log.d(TAG, "Clue updated:" + event.newClue());

                tvHint.setText(event.newClue());
                etAnswer.setHint(event.newClue());

                updateHintAvailability();
            } else if (ev instanceof GameWrongAnswerEvent) {
                GameWrongAnswerEvent event = (GameWrongAnswerEvent) ev;
                Log.d(TAG, "Wrong Answer received: " + event);

                QuizAnalytics.logLevelWrongAnswer(firebaseAnalytics, event.drawing().getWord());

                showWrongAnswerFullScreenDialog();
            } else if (ev instanceof GameLevelClearEvent) {
                GameLevelClearEvent event = (GameLevelClearEvent) ev;
                Log.d(TAG, "Round cleared: " + event);

                QuizAnalytics.logLevelSuccess(firebaseAnalytics, event.drawing().getWord(),
                        event.numAttempts(), event.elapsedTimeInSeconds(), event.isHintUsed());

                if (event.isFinalLevel()) {
                    viewModel.moveToNextLevel();
                } else {
                    showRoundClearFullScreenDialog();
                }
            } else if (ev instanceof GameLevelSkipEvent) {
                GameLevelSkipEvent event = (GameLevelSkipEvent) ev;
                Log.d(TAG, "Round skipped: " + event);

                QuizAnalytics.logLevelFail(firebaseAnalytics, event.drawing().getWord(),
                        event.numAttempts(), event.elapsedTimeInSeconds(), event.isHintUsed());
            } else if (ev instanceof GameStageClearEvent) {
                GameStageClearEvent event = (GameStageClearEvent) ev;
                Log.d(TAG, "Stage cleared: " + event);

                QuizAnalytics.logStageEnd(firebaseAnalytics, event.numCorrectAnswers());

                showStageClearFullScreenDialog(event.numCorrectAnswers());
            }
        });
    }

    private void showAnswerInputDialog() {
        etAnswer.setText("");
        dlgAnswer.show();
    }

    private void showStageStartFullScreenDialog() {
        tvFullScreenDialogTitle.setText(R.string.guess_the_name);
        tvFullScreenDialogMessage.setVisibility(View.GONE);
        btnFullScreenDialogAction.setText(R.string.get_started);
        btnFullScreenDialogAction.setOnClickListener(v -> {
            hideFullScreenDialog();
            viewModel.startLevel();
        });

        flFullScreenDialog.animate().alpha(1.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        flFullScreenDialog.setVisibility(View.VISIBLE);
                        super.onAnimationStart(animation);
                    }
                });
    }

    private void showWrongAnswerFullScreenDialog() {
        tvFullScreenDialogTitle.setText(R.string.wrong_answer);
        tvFullScreenDialogMessage.setVisibility(View.GONE);
        btnFullScreenDialogAction.setText(R.string.try_again);
        btnFullScreenDialogAction.setOnClickListener(v ->
                hideFullScreenDialog()
        );

        flFullScreenDialog.animate().alpha(1.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        flFullScreenDialog.setVisibility(View.VISIBLE);
                        super.onAnimationStart(animation);
                    }
                });
    }

    private void showRoundClearFullScreenDialog() {
        tvFullScreenDialogTitle.setText(R.string.good_job);
        tvFullScreenDialogMessage.setVisibility(View.GONE);
        btnFullScreenDialogAction.setText(R.string.next_level);
        btnFullScreenDialogAction.setOnClickListener(v -> {
            hideFullScreenDialog();
            viewModel.moveToNextLevel();
        });

        flFullScreenDialog.animate().alpha(1.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        flFullScreenDialog.setVisibility(View.VISIBLE);
                        super.onAnimationStart(animation);
                    }
                });
    }

    private void showStageClearFullScreenDialog(int numberOfCorrectAnswers) {
        tvFullScreenDialogTitle.setText(R.string.stage_finished);
        tvFullScreenDialogMessage.setVisibility(View.VISIBLE);
        tvFullScreenDialogMessage.setText(
                getString(R.string.correct_answers, numberOfCorrectAnswers));
        btnFullScreenDialogAction.setText(R.string.back_to_main_menu);
        btnFullScreenDialogAction.setOnClickListener(v ->
                finish()
        );

        flFullScreenDialog.animate().alpha(1.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        flFullScreenDialog.setVisibility(View.VISIBLE);
                        super.onAnimationStart(animation);
                    }
                });
    }

    private void hideFullScreenDialog() {
        flFullScreenDialog.animate().alpha(0.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        flFullScreenDialog.setVisibility(View.GONE);
                    }
                });
    }

    private void showHintConfirmDialog() {
        QuizAnalytics.logAdPrompt(firebaseAnalytics, AD_UNIT_ID);

        new AlertDialog.Builder(this)
                .setTitle(R.string.need_a_hint)
                .setMessage(R.string.need_a_hint_description)
                .setPositiveButton(android.R.string.ok, (dlg, which) -> {
                    showRewardedVideoAd();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void updateHintAvailability() {
        btnHint.setEnabled(isHintAvailable());
    }

    private boolean isHintAvailable() {
        return viewModel.isHintAvailable() && rewardedVideo.isLoaded();
    }

    private void setupRewardedVideoAd() {
        rewardedVideo = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideo.setRewardedVideoAdListener(this);
    }

    private void loadRewardedVideoAd() {
        Log.d(TAG, "Requesting rewarded video ad");
        rewardedVideo.loadAd(AD_UNIT_ID, new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build());
    }

    private void showRewardedVideoAd() {
        if (rewardedVideo.isLoaded()) {
            rewardedVideo.show();
        } else {
            Log.e(TAG, "Rewarded Video Ad was not loaded yet");
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded()");
        updateHintAvailability();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened()");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d(TAG, "onRewardedVideoStarted()");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d(TAG, "onRewardedVideoAdClosed()");
        btnHint.setEnabled(false);
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d(TAG, "onRewarded()");
        viewModel.useHint();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLeftApplication()");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Log.d(TAG, "onRewardedVideoAdFailedToLoad() : " + errorCode);
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.d(TAG, "onRewardedVideoCompleted()");
    }
}
