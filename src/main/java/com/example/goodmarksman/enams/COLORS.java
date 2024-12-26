package com.example.goodmarksman.enams;

import javafx.scene.paint.Color;

public enum COLORS {
    BLUE, RED, GREEN,
    DARK_BLUE, ORANGE, BLACK, PURPLE,
    NULL;

    public Color getValue() {
        return switch (this) {
            case BLUE -> Color.rgb(33, 212, 255);
            case RED -> Color.rgb(255, 33, 33);
            case GREEN -> Color.rgb(0, 128, 0);
            case DARK_BLUE -> Color.rgb(0, 0, 255);
            case ORANGE -> Color.rgb(255, 140, 0);
            case BLACK -> Color.rgb(0, 0, 0);
            case PURPLE -> Color.rgb(128, 0, 128);
            case NULL -> throw new Error("NULL color");
        };
    }
}

