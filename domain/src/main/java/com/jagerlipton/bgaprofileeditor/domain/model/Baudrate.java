package com.jagerlipton.bgaprofileeditor.domain.model;

public class Baudrate {

    public Baudrate() {
    }

    private static Integer[] rates = {9600, 19200, 38400, 57600, 115200, 230400, 460800, 921600};

    public static Integer getBaudrateByIndex(Integer index) {
        return rates[index];
    }

}