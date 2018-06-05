package com.uk.cmo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by usman on 31-05-2018.
 */

public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}