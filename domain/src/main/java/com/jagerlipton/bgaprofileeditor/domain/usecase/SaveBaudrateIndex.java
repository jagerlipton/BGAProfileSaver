package com.jagerlipton.bgaprofileeditor.domain.usecase;

import com.jagerlipton.bgaprofileeditor.domain.interfaces.IRepository;

public class SaveBaudrateIndex {

    private final IRepository repository;

    public SaveBaudrateIndex(IRepository repository) {
        this.repository = repository;
    }

    public void execute(Integer index) {
        repository.saveBaudrateIndex(index);
    }

}

