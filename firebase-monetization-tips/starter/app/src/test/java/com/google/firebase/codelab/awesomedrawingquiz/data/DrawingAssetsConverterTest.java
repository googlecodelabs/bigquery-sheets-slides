package com.google.firebase.codelab.awesomedrawingquiz.data;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DrawingAssetsConverterTest {

    @Mock
    private AssetManager am;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void loadItem() throws IOException {
        // Mock
        String[] pathNames = new String[]{
                getTestResourceFilePath("item.ndjson")
        };

        am = mock(AssetManager.class);
        when(am.list(anyString()))
                .thenReturn(pathNames);
        when(am.open(anyString()))
                .thenReturn(getTestResource("item.ndjson"));

        // Test
        TestObserver<Drawing> o = TestObserver.create();

        // When
        new DrawingAssetsConverter(am).subscribe(o);

        // Then
        o.assertComplete();
        o.assertNoErrors();
        o.assertValueCount(1);

        o.dispose();
    }

    @Test
    public void loadItems() throws IOException {
        // Mock
        String[] pathNames = new String[]{
                getTestResourceFilePath("items.ndjson")
        };

        am = mock(AssetManager.class);
        when(am.list(anyString()))
                .thenReturn(pathNames);
        when(am.open(anyString()))
                .thenReturn(getTestResource("items.ndjson"));

        // Test
        TestObserver<Drawing> o = TestObserver.create();

        // When
        new DrawingAssetsConverter(am).subscribe(o);

        // Then
        o.assertComplete();
        o.assertNoErrors();
        o.assertValueCount(3);

        o.dispose();
    }

    @NonNull
    private String getTestResourceFilePath(@NonNull String path) {
        String filename = this.getClass().getClassLoader().getResource(path).getFile();
        if (null == filename) {
            throw new IllegalStateException("Cannot find a file in path " + path);
        }
        return new File(filename).getAbsolutePath();
    }

    @NonNull
    private InputStream getTestResource(@NonNull String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

}
