package com.example.beerproject.calculators;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.example.beerproject.R;

import java.text.DecimalFormat;

public class AlcoholCalculator extends AppCompatActivity {

    EditText initialGravityInput;
    EditText finalGravityInput;
    TextView alcoholResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_calculator);

        initialGravityInput = findViewById(R.id.initial_gravity_input);
        initialGravityInput.addTextChangedListener(filterTextWatcher);
        finalGravityInput = findViewById(R.id.final_gravity_input);
        finalGravityInput.addTextChangedListener(filterTextWatcher);
        alcoholResult = findViewById(R.id.alcohol_result);


    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!initialGravityInput.getText().toString().equals("") && !finalGravityInput.getText().toString().equals("")) {
                Double result = (Double.parseDouble(initialGravityInput.getText().toString()) - Double.parseDouble(finalGravityInput.getText().toString())) / 1.938;
                DecimalFormat df = new DecimalFormat("####0.00");
                alcoholResult.setText(df.format(result) + "%");
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
