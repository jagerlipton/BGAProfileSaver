package com.jagerlipton.bgaprofileeditor.domain.model;

public class ArduinoProfileListDomain {
    private String keyName;
    private String valueName;

    public ArduinoProfileListDomain(String keyName, String valueName) {
        this.keyName = keyName;
        this.valueName = valueName;
    }

    public ArduinoProfileListDomain() {
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }



}
