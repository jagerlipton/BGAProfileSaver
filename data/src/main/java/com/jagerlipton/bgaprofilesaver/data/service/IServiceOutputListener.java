package com.jagerlipton.bgaprofilesaver.data.service;

import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofilesaver.data.service.model.Connection;

import java.util.ArrayList;

public interface IServiceOutputListener {
    void getServiceArrayList(ArrayList<ArduinoProfileListData> list);
    void getServiceConnectionData(Connection connection);
}
