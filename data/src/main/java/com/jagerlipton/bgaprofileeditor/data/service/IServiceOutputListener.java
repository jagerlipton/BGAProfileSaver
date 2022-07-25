package com.jagerlipton.bgaprofileeditor.data.service;

import com.jagerlipton.bgaprofileeditor.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofileeditor.data.service.model.Connection;
import java.util.List;

public interface IServiceOutputListener {
    void getServiceArrayList(List<ArduinoProfileListData> list);
    void getServiceConnectionData(Connection connection);
}
