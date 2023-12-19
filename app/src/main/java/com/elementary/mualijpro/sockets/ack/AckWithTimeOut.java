package com.elementary.mualijpro.sockets.ack;

import com.github.nkzawa.socketio.client.Ack;

import java.util.Timer;
import java.util.TimerTask;

public class AckWithTimeOut implements Ack {

    private Timer timer;
    private long timeOut = 0;
    private boolean called = false;


    public AckWithTimeOut(long timeoutAfter) {
        if (timeoutAfter <= 0)
            return;
        this.timeOut = timeoutAfter;
        startTimer();
    }


    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                callback("No Ack");
            }
        }, timeOut);
    }

    public void resetTimer() {
        if (timer != null) {
            timer.cancel();
            startTimer();
        }
    }

    public void cancelTimer() {
        if (timer != null)
            timer.cancel();
    }

    void callback(Object... args) {
        if (called) {
            return;
        }
        cancelTimer();
        call(args);
    }

    @Override
    public void call(Object... args) {
        called = true;
        cancelTimer();
    }
}