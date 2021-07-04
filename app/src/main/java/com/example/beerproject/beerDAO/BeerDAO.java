package com.example.beerproject.beerDAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BeerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Beer beer);

    @Update
    public void update(Beer beer);

    @Delete
    public void delete(Beer beer);

    @Query("DELETE FROM beer")
    public void deleteAll();

    @Query("Select * FROM beer ORDER BY name")
    public LiveData<List<Beer>> findAll();


    @Query("Select * FROM beer WHERE id = :id LIMIT 1")
    public LiveData<Beer> findBeerWithId(Integer id);
}
