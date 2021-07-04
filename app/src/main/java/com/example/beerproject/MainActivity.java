package com.example.beerproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.beerproject.beers.BeerListActivity;
import com.example.beerproject.calculators.AlcoholCalculator;
import com.example.beerproject.calculators.SugarCalculator;
import com.example.beerproject.calculators.IBUCalculator;


public class MainActivity extends AppCompatActivity {

    TextView beersList;
    TextView alcoholCalculator;
    TextView sugarCalculator;
    TextView IBUCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        beersList = findViewById(R.id.beers_list);
        beersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BeerListActivity.class);
                startActivity(intent);
            }
        });

        alcoholCalculator = findViewById(R.id.alcohol_calculator);
        alcoholCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlcoholCalculator.class);
                startActivity(intent);

            }
        }
        );

        sugarCalculator = findViewById(R.id.sugar_calculator);
        sugarCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SugarCalculator.class);
                startActivity(intent);
            }
        }
        );

        IBUCalculator = findViewById(R.id.IBU_calculator);
        IBUCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IBUCalculator.class);
                startActivity(intent);
            }
        }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);


        MenuItem info = menu.findItem(R.id.information);
        info.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(MainActivity.this).create();
                LayoutInflater inflater2 = getLayoutInflater();
                View dialogView = inflater2.inflate(R.layout.information, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}
