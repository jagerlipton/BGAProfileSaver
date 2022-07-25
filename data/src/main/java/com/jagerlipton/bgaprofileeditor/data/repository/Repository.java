package com.jagerlipton.bgaprofileeditor.data.repository;

import com.jagerlipton.bgaprofileeditor.data.storage.IStorage;
import com.jagerlipton.bgaprofileeditor.domain.interfaces.IRepository;

public class Repository implements IRepository {

    private IStorage storage;
    public Repository(IStorage storage) {
        this.storage = storage;
    }

    @Override
    public Integer loadBaudrateIndex() {
        return storage.loadBaudrateIndex();
    }

    @Override
    public void saveBaudrateIndex(Integer index) {
        storage.saveBaudrateIndex(index);
    }

}
