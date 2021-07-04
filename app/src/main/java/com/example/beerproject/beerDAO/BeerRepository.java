package com.example.beerproject.beerDAO;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class BeerRepository {
    private BeerDAO beerDAO;
    private LiveData<List<Beer>> beers;

    BeerRepository(Application application){
        BeerDatabase database = BeerDatabase.getDatabase(application);
        beerDAO = database.beerDAO();
        beers = beerDAO.findAll();
    }

    LiveData<List<Beer>> findAllBooks(){
        return beers;
    }


    public LiveData<Beer> findBeerWithId(Integer id){
        return beerDAO.findBeerWithId(id);
    }

    void insert(Beer beer){
        BeerDatabase.databaseWriteExecutor.execute(()->{
            beerDAO.insert(beer);
        });
    }

    void update(Beer beer){
        BeerDatabase.databaseWriteExecutor.execute(()->{
            beerDAO.update(beer);
        });
    }

    void delete(Beer beer){
        BeerDatabase.databaseWriteExecutor.execute(()->{
            beerDAO.delete(beer);
        });
    }
}
