package com.example.beerproject.beers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beerproject.R;
import com.example.beerproject.beerDAO.Ingredient;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Optional;

public class BeerAddActivity extends AppCompatActivity {
    static final String NEW_BEER_NAME="new beer name";
    static final String NEW_BEER_TIME="new beer time";
    static final String NEW_BEER_TEMPERATURE="new beer temperature";
    static final String NEW_BEER_STYLE="new beer style";
    static final String NEW_BEER_INGREDIENTS="new beer ingredients";
    static final String NEW_BEER_LIST_INGREDIENTS="new beer list ingredients";

    private EditText brewName;
    private EditText brewTime;
    private EditText brewTemperature;

    private Button addIngredient;
    private Button addBrew;
    private RecyclerView ingredientRecyclerView;
    private IngredientAdapter ingredientAdapter;
    private Spinner spinner;
    private String beerStyle;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_add_layout);
        ingredientAdapter = new IngredientAdapter();
        ingredientRecyclerView = findViewById(R.id.ingredient_recycler_view);
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientRecyclerView.setAdapter(ingredientAdapter);

        addBrew = findViewById(R.id.add_beer);
        brewName = findViewById(R.id.brew_name);
        brewTime = findViewById(R.id.brew_time);
        brewTemperature = findViewById(R.id.brew_temperature);
        addIngredient = findViewById(R.id.add_ingredient);
        addBrew = findViewById(R.id.add_beer);
        spinner = findViewById(R.id.beer_style_add_recipe);
        spinner.setOnItemSelectedListener(new SpinnerItemListener());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.beers_sugar, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAddIngredient();
            }
        });

        addBrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAddBrew();
            }

        });
    }


    void clickAddIngredient(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(BeerAddActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_ingredient_layout, null);
        EditText ingredientName = dialogView.findViewById(R.id.ingredient_name);
        EditText ingredientAmount = dialogView.findViewById(R.id.ingredient_amount);
        EditText ingredientTime = dialogView.findViewById(R.id.ingredient_time);
        Button addIngredientButton = dialogView.findViewById(R.id.add_ingredient_element);

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ingredientName.getText().toString().equals("") && !ingredientAmount.getText().toString().equals("") && !ingredientTime.getText().toString().equals("")) {
                    Ingredient newIngredient = new Ingredient(ingredientName.getText().toString(), Float.parseFloat(ingredientAmount.getText().toString()), Float.parseFloat(ingredientTime.getText().toString()));
                    ingredientAdapter.addIngredient(newIngredient);
                    dialogBuilder.dismiss();
                }
                else Snackbar.make(dialogView,R.string.add_error,Snackbar.LENGTH_LONG);
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    void clickAddBrew(){
        if(brewName.getText().toString().equals("") || brewTime.getText().toString().equals("") || brewTemperature.getText().toString().equals("")) {
            Snackbar.make(findViewById(R.id.beer_add_id),R.string.add_error,Snackbar.LENGTH_LONG);
        }
        else {
            Intent replyIntent = new Intent();
            replyIntent.putExtra(NEW_BEER_NAME, brewName.getText().toString());
            replyIntent.putExtra(NEW_BEER_TIME, Float.parseFloat(brewTime.getText().toString()));
            replyIntent.putExtra(NEW_BEER_TEMPERATURE, Float.parseFloat(brewTemperature.getText().toString()));
            replyIntent.putExtra(NEW_BEER_STYLE, beerStyle);

            Bundle extra = new Bundle();
            extra.putSerializable(NEW_BEER_INGREDIENTS, ingredientAdapter.getIngredients());
            replyIntent.putExtra(NEW_BEER_LIST_INGREDIENTS, extra);
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }

    public class SpinnerItemListener extends Activity implements AdapterView.OnItemSelectedListener {


        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            beerStyle = "";
            String[] string = parent.getItemAtPosition(pos).toString().split(" ");
            for(int i = 0; i < string.length-1; i++){
                beerStyle = beerStyle.concat(string[i]);
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class IngredientHolder extends RecyclerView.ViewHolder{

        private TextView ingredientNameHolder;
        private TextView ingredientAmountHolder;
        private TextView ingredientTimeHolder;
        private ImageView editIngredient;
        private ImageView deleteIngredient;
        Ingredient ingredient;

        public IngredientHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.ingredient_element,parent,false));
            ingredientNameHolder = itemView.findViewById(R.id.ingredient_name_element);
            ingredientAmountHolder = itemView.findViewById(R.id.ingredient_amount_element);
            ingredientTimeHolder = itemView.findViewById(R.id.ingredient_time_element);
            editIngredient = itemView.findViewById(R.id.edit_ingredient);
            deleteIngredient = itemView.findViewById(R.id.delete_ingredient);

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Ingredient ingredient)
        {
            this.ingredient = ingredient;
            ingredientNameHolder.setText(ingredient.getName());
            ingredientAmountHolder.setText(ingredient.getAmount().toString() + " g");
            ingredientTimeHolder.setText(ingredient.getMinute().toString() + " min");

            editIngredient.setOnClickListener( e -> {
                clickEditIngredient(ingredientNameHolder.getText().toString(), ingredientAmountHolder.getText().toString(), ingredientTimeHolder.getText().toString(),ingredient);
            });

            deleteIngredient.setOnClickListener(e -> {
                clickDeleteIngredient(ingredient);
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void clickEditIngredient(String ingredientNameHolder, String ingredientAmountHolder, String ingredientTimeHolder, Ingredient ingredient){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(BeerAddActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_ingredient_layout, null);
        EditText ingredientName = dialogView.findViewById(R.id.ingredient_name);
        EditText ingredientAmount = dialogView.findViewById(R.id.ingredient_amount);
        EditText ingredientTime = dialogView.findViewById(R.id.ingredient_time);
        Button addIngredientButton = dialogView.findViewById(R.id.add_ingredient_element);

        ingredientName.setText(ingredientNameHolder);
        String amountOptionalHolder = Optional.ofNullable(ingredientAmountHolder.toString()).filter(string -> string.length()>0).map(string -> string.substring(0, string.length()-2)).get();
        ingredientAmount.setText(amountOptionalHolder);
        String timeOptionalHolder = Optional.ofNullable(ingredientTimeHolder.toString()).filter(string -> string.length()>0).map(string -> string.substring(0, string.length()-4)).get();
        ingredientTime.setText(timeOptionalHolder);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientAdapter.deleteIngredient(ingredient);
                Ingredient newIngredient = new Ingredient(ingredientName.getText().toString(),Float.parseFloat(ingredientAmount.getText().toString()),Float.parseFloat(ingredientTime.getText().toString()));
                ingredientAdapter.addIngredient(newIngredient);
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    void clickDeleteIngredient(Ingredient ingredient){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
        builder1.setMessage("Are you sure you want to delete this ingredient?");
        builder1.setCancelable(true);
        Log.d("onLongClick","OnLongClick");

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ingredientAdapter.deleteIngredient(ingredient);
                        dialog.cancel();
                    }
                });



        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private class IngredientAdapter extends RecyclerView.Adapter<BeerAddActivity.IngredientHolder>
    {
        private ArrayList<Ingredient> ingredients;

        public void addIngredient(Ingredient ingredient)
        {
            ingredients.add(ingredient);
            notifyDataSetChanged();
        }

        public void deleteIngredient(Ingredient ingredient){
            if(ingredients.contains(ingredient)){
                ingredients.remove(ingredient);
                notifyDataSetChanged();
            }else{
                Log.d("Ingredient Error","Ingredient don't exists");
            }

        }

        public ArrayList<Ingredient> getIngredients(){
            return ingredients;
        }

        IngredientAdapter(){
            ingredients = new ArrayList<>();
        }

        @NonNull
        @Override
        public BeerAddActivity.IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BeerAddActivity.IngredientHolder(getLayoutInflater(), parent);

        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(@NonNull BeerAddActivity.IngredientHolder holder, int position) {

            if (ingredients != null) {
                Ingredient ingredient = ingredients.get(position);
                holder.bind(ingredient);
            } else {
                Log.d("MainActivity", "No ingredients");
            }
        }

        @Override
        public int getItemCount()
        {
            if (ingredients != null){
                return ingredients.size();
            } else {
                return 0;
            }
        }

    }
}
