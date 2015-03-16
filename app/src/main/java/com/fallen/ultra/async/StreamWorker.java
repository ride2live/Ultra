package com.fallen.ultra.async;

import com.fallen.ultra.callbacks.Observer;

import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Nolwe on 13.03.2015.
 */
public class StreamWorker implements Runnable {

    boolean isBuffered = false;
    ArrayList<Observer> observers;
    OutputStream emulatingStream;



    @Override
    public void run() {

    }
}
