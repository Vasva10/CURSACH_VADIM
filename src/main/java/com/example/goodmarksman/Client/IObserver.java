package com.example.goodmarksman.Client;

import com.example.goodmarksman.models.GameModel;

public interface IObserver {
    void event(GameModel m);
}
