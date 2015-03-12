package com.fallen.ultra.callbacks;

import com.fallen.ultra.creators.StatusObject;

public interface Observerable {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(StatusObject sObject);
}
