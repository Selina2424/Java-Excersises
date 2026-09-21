package com.sg.flooringmastery.ui;

public interface UserIo {

    void print(String message);
    String readString(String prompt);
    int readInt(String prompt);
    int readInt(String prompt, int min, int max);
}
