package com.example.android.popularmovies.database;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors executorInstance;
    private final Executor diskIOExecutor;

    private AppExecutors(Executor diskIOExecutor) {
        this.diskIOExecutor = diskIOExecutor;
    }

    public static AppExecutors getExecutorInstance() {
        if (executorInstance == null) {
            synchronized (LOCK) {
                executorInstance = new AppExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return executorInstance;
    }

    public Executor diskIOExecutor() {
        return diskIOExecutor;
    }

}
