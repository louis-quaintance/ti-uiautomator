package com.ti.uiautomator.utils;

public abstract class RetryCommand<T> {
    private final int maxRetries;

    public RetryCommand(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public abstract T command() throws Exception;

    public final T run() throws RuntimeException {
        try {
            return command();
        } catch (Throwable e) {
            return retry();
        }
    }

    private final T retry() throws RuntimeException {
        System.out.println("FAILED - Command failed, will be retried " + maxRetries + " times.");
        int retryCounter = 0;
        while (retryCounter < maxRetries) {
            try {
                return command();
            } catch (Throwable e) {
                retryCounter++;
                System.out.println("FAILED - Command failed on retry " + retryCounter + " of " + maxRetries
                        + " error: " + e);
                if (retryCounter >= maxRetries) {
                    System.out.println("Max retries exceeded.");
                    break;
                }
            }
        }
        throw new RuntimeException("Command failed on all of " + maxRetries + " retries");
    }
}
