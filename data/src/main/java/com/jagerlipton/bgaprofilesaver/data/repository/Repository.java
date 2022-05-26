package com.jagerlipton.bgaprofilesaver.data.repository;

import com.jagerlipton.bgaprofilesaver.data.storage.IStorage;
import com.jagerlipton.bgaprofilesaver.domain.interfaces.IRepository;

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
