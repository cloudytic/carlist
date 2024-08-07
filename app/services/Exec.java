package services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Exec {
    public static ExecutorService Ex = Executors.newCachedThreadPool();

    public static ScheduledExecutorService ScEx = Executors.newScheduledThreadPool(3);

}