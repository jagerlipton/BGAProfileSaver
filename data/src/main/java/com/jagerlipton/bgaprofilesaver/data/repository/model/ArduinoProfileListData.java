package com.jagerlipton.bgaprofilesaver.data.repository.model;

import com.jagerlipton.bgaprofilesaver.data.service.JSON.ArrayItem;
import com.jagerlipton.bgaprofilesaver.data.service.JSON.OutputItem;
import com.jagerlipton.bgaprofilesaver.domain.model.ArduinoProfileListDomain;

import java.util.ArrayList;
import java.util.List;

public class ArduinoProfileListData {
    private String keyName;
    private String valueName;

    public ArduinoProfileListData(String keyName, String valueName) {
        this.keyName = keyName;
        this.valueName = valueName;
    }

    public ArduinoProfileListData() {
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

    public static List<ArduinoProfileListData> mapDomainToData(List<ArduinoProfileListDomain> input) {
        List<ArduinoProfileListData> output = new ArrayList<ArduinoProfileListData>();
        for (int i = 0; i < input.size(); i++) {
            ArduinoProfileListData item = new ArduinoProfileListData(input.get(i).getKeyName(), input.get(i).getValueName());
            output.add(item);
        }
        return output;
    }

    public static List<ArrayItem> mapDataToArrayItem(List<ArduinoProfileListData> input) {
        List<ArrayItem> output = new ArrayList<ArrayItem>();
        for (int i = 0; i < input.size(); i++) {
            ArrayItem item = new ArrayItem();
            item.key = input.get(i).getKeyName();
            item.value = input.get(i).getValueName();
            output.add(item);
        }
        return output;
    }

}
