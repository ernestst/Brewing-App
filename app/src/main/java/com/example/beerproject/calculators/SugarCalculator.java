package com.example.beerproject.calculators;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.beerproject.R;
import android.widget.AdapterView.OnItemSelectedListener;

import java.text.DecimalFormat;

public class SugarCalculator extends AppCompatActivity {

    EditText batchValue;
    EditText temperature;

    TextView sugarResult;
    TextView glucoseResult;

    Spinner spinner;
    Float minValue;
    Float maxValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugar_calculator);

        batchValue = findViewById(R.id.sugar_batch_value);
        batchValue.addTextChangedListener(filterTextWatcher);
        temperature = findViewById(R.id.sugar_temperature);
        temperature.addTextChangedListener(filterTextWatcher);


        sugarResult = findViewById(R.id.sugar_amount);
        glucoseResult = findViewById(R.id.glucose_amount);

        spinner = findViewById(R.id.beer_sugar);
        spinner.setOnItemSelectedListener(new SpinnerItemListener());


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.beers_sugar, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            calculate();

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public class SpinnerItemListener extends Activity implements OnItemSelectedListener {


        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            String[] string = parent.getItemAtPosition(pos).toString().split(" ");
            minValue = Float.parseFloat(string[string.length-1].substring(0,3));
            maxValue = Float.parseFloat(string[string.length-1].substring(4,7));
            calculate();

        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void calculate(){
        if(!temperature.getText().toString().equals("") && !batchValue.getText().toString().equals("")){
            double residualVols = 1.57 * Math.pow( 0.97, Double.parseDouble(temperature.getText().toString()));
            double addedVols = minValue - residualVols;
            double co2_l = addedVols * Double.parseDouble(batchValue.getText().toString());
            double co2_mol = co2_l / 22.4;
            double glucose = co2_mol / 2 * 198;
            double sugar= co2_mol / 2 * 180;
            DecimalFormat df = new DecimalFormat("####0.00");
            sugarResult.setText(df.format(sugar));
            glucoseResult.setText(df.format(glucose));
            addedVols = maxValue - residualVols;
            co2_l = addedVols * Double.parseDouble(batchValue.getText().toString());
            co2_mol = co2_l / 22.4;
            glucose = co2_mol / 2 * 198;
            sugar = co2_mol / 2 * 180;
            sugarResult.append("-" + df.format(sugar) + " g");
            glucoseResult.append("-" + df.format(glucose) + " g");
        }
    }
}
