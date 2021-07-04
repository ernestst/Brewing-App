package com.example.beerproject.beerDAO;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class BeerViewModel extends AndroidViewModel {
    private BeerRepository beerRepository;

    private LiveData<List<Beer>> beers;

    public BeerViewModel(@NonNull Application application) {
        super(application);
        beerRepository = new BeerRepository(application);
        beers = beerRepository.findAllBooks();
    }

    public LiveData<List<Beer>> findAll(){
        return beers;
    }

    public LiveData<Beer> findBeerWithId(Integer id){
        return beerRepository.findBeerWithId(id);
    }

    public void insert(Beer beer){
        beerRepository.insert(beer);
    }

    public void update(Beer beer){
        beerRepository.update(beer);
    }

    public void delete(Beer beer){
        beerRepository.delete(beer);
    }
}
