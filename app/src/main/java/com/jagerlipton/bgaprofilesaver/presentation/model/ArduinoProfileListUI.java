package com.jagerlipton.bgaprofilesaver.presentation.model;

import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofilesaver.domain.model.ArduinoProfileListDomain;

import java.util.ArrayList;
import java.util.List;

public class ArduinoProfileListUI {
    private Integer id;
    private String keyName;
    private String valueName;

    public ArduinoProfileListUI(Integer id, String keyName, String valueName) {
        this.id = id;
        this.keyName = keyName;
        this.valueName = valueName;
    }

    public ArduinoProfileListUI() {
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
        return "{" + keyName + ":" + valueName + "}";
    }

    public static List<ArduinoProfileListUI> mapDataToUI(List<ArduinoProfileListData> input) {
        List<ArduinoProfileListUI> output = new ArrayList<ArduinoProfileListUI>();
        for (int i = 0; i < input.size(); i++) {
            ArduinoProfileListUI item = new ArduinoProfileListUI(i + 1, input.get(i).getKeyName(), input.get(i).getValueName());
            output.add(item);
        }
        return output;
    }

    public static List<ArduinoProfileListDomain> mapUIToDomain(List<ArduinoProfileListUI> input) {
        List<ArduinoProfileListDomain> output = new ArrayList<ArduinoProfileListDomain>();
        for (int i = 0; i < input.size(); i++) {
            ArduinoProfileListDomain item = new ArduinoProfileListDomain(input.get(i).getKeyName(), input.get(i).getValueName());
            output.add(item);
        }
        return output;
    }
}
