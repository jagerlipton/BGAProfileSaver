package com.jagerlipton.bgaprofilesaver.data.service;

import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofilesaver.data.service.model.Connection;
import java.util.List;

public interface IServiceOutputListener {
    void getServiceArrayList(List<ArduinoProfileListData> list);
    void getServiceConnectionData(Connection connection);
}
