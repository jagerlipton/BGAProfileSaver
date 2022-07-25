package com.jagerlipton.bgaprofileeditor.domain.usecase;

import com.jagerlipton.bgaprofileeditor.domain.interfaces.IRepository;


public class LoadBaudrateIndex {

    private final IRepository repository;

    public LoadBaudrateIndex(IRepository repository) {
        this.repository = repository;
    }

    public Integer execute() {
        return repository.loadBaudrateIndex();
    }

}
