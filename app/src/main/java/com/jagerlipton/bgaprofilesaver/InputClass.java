package com.jagerlipton.bgaprofilesaver;

public class InputClass {
private Integer id;
private String keyName;
private String valueName;

    public InputClass(Integer id, String keyName, String valueName) {
        this.id = id;
        this.keyName = keyName;
        this.valueName = valueName;
    }

    public InputClass() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "{" + keyName + ":"+ valueName + "}";
    }


}
