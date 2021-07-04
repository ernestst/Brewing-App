package com.example.beerproject.calculators;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beerproject.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class IBUCalculator extends AppCompatActivity {

    EditText gravity;
    EditText liters;
    EditText hopAmount;
    EditText alphacid;
    EditText minute;
    LinkedList<Hop> listHops;
    Button addHop;
    TextView IBUResult;
    RecyclerView recyclerViewHops;
    HopAdapter adapter;
    DecimalFormat df;
    DecimalFormatSymbols dfs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibu_calculator);

        dfs = new DecimalFormatSymbols(Locale.ENGLISH);
        dfs.setDecimalSeparator('.');
        df = new DecimalFormat("####0.00",dfs);

        liters = findViewById(R.id.ibu_liters);
        liters.addTextChangedListener(filterTextWatcher);
        gravity = findViewById(R.id.ibu_gravity);
        gravity.addTextChangedListener(filterTextWatcher);
        addHop = findViewById(R.id.add_hop);
        IBUResult = findViewById(R.id.ibu_result);
        listHops = new LinkedList<>();
        adapter = new HopAdapter();
        recyclerViewHops = findViewById(R.id.hops_recycler_view);
        recyclerViewHops.setAdapter(adapter);
        recyclerViewHops.setLayoutManager(new LinearLayoutManager(this));

        addHop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAddHop();
            }
        });
    }

    void clickAddHop(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(IBUCalculator.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_hop_layout, null);

        hopAmount = dialogView.findViewById(R.id.hop_amount);
        alphacid = dialogView.findViewById(R.id.alpha_acid);
        minute = dialogView.findViewById(R.id.minute);
        Button addHopButton = dialogView.findViewById(R.id.add_hop_button);

        addHopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hopAmount.getText().toString().equals("") && !alphacid.getText().toString().equals("") && !minute.getText().toString().equals("")){
                    Hop hop = new Hop(Double.parseDouble(hopAmount.getText().toString()), Double.parseDouble(alphacid.getText().toString()), Double.parseDouble(minute.getText().toString()));
                    listHops.add(hop);
                    adapter.setHops(listHops);
                    if (!liters.getText().toString().equals("") && !gravity.getText().toString().equals("")) {
                        if (IBUResult.getText().toString().equals("")) {
                            Double result = hop.calculateResult(Double.parseDouble(liters.getText().toString()), Double.parseDouble(gravity.getText().toString()));

                            IBUResult.setText(df.format(result));
                        } else {
                            Double previousResult = Double.parseDouble(IBUResult.getText().toString());
                            previousResult += hop.calculateResult(Double.parseDouble(liters.getText().toString()), Double.parseDouble(gravity.getText().toString()));

                            IBUResult.setText(df.format(previousResult));
                        }
                    } else {
                        View view = findViewById(R.id.layout_ibu_calculator);
                        Snackbar.make(view, R.string.add_error, Snackbar.LENGTH_LONG);
                    }
                }
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!liters.getText().toString().equals("") && !gravity.getText().toString().equals("") && listHops.size()>0) {
                Double result = 0.0;
                for(Hop hop : listHops){
                    result += hop.calculateResult(Double.parseDouble(liters.getText().toString()),Double.parseDouble(gravity.getText().toString()));
                }
                IBUResult.setText(df.format(result));

            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private class HopHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener, View.OnLongClickListener*/{

        private TextView hopAmountHolder;
        private TextView alphaHolder;
        private TextView minuteHolder;
        ImageView editHop;
        ImageView deleteHop;
        Hop hop;

        public HopHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.hop_element,parent,false));
            hopAmountHolder = itemView.findViewById(R.id.hop_amount_element);
            alphaHolder = itemView.findViewById(R.id.hop_alpha_element);
            minuteHolder = itemView.findViewById(R.id.hop_minute_element);
            editHop = itemView.findViewById(R.id.edit_hop);
            deleteHop = itemView.findViewById(R.id.delete_hop);

            editHop.setOnClickListener( e -> {
                clickEditHop(hopAmountHolder.getText().toString(),alphaHolder.getText().toString(),minuteHolder.getText().toString(), hop);
            });

            deleteHop.setOnClickListener(e -> {
                clickDeleteHop(hop);
            });
        }

        public void bind(Hop hop)
        {
            this.hop = hop;
            hopAmountHolder.setText(hop.getAmount().toString());
            alphaHolder.setText(hop.getAlpha().toString());
            minuteHolder.setText(hop.getMinute().toString());

        }
    }

    void clickEditHop(String hopAmountHolder, String alphaHolder, String minuteHolder, Hop hop){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(IBUCalculator.this).create();
        LayoutInflater inflater2 = getLayoutInflater();
        View dialogView = inflater2.inflate(R.layout.add_hop_layout, null);
        Log.d("onClick","OnClick");
        hopAmount = dialogView.findViewById(R.id.hop_amount);
        hopAmount.setText(hopAmountHolder);
        alphacid = dialogView.findViewById(R.id.alpha_acid);
        alphacid.setText(alphaHolder);
        minute = dialogView.findViewById(R.id.minute);
        minute.setText(minuteHolder);
        Button addHopButton = dialogView.findViewById(R.id.add_hop_button);

        addHopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hopAmount.getText().toString().equals("") || alphacid.getText().toString().equals("") || minute.getText().toString().equals("")) {
                    Snackbar.make(dialogView,R.string.add_error,Snackbar.LENGTH_LONG);
                }
                else{
                    Double previousResult = Double.parseDouble(IBUResult.getText().toString());
                    previousResult -= hop.getResult();
                    listHops.remove(hop);
                    hop.setAmount(Double.parseDouble(hopAmount.getText().toString()));
                    hop.setAlpha(Double.parseDouble(alphacid.getText().toString()));
                    hop.setMinute(Double.parseDouble(minute.getText().toString()));
                    listHops.add(hop);
                    adapter.setHops(listHops);
                    if(!liters.getText().toString().equals("") && !gravity.getText().toString().equals("") && listHops.size()>0) {
                        previousResult += hop.calculateResult(Double.parseDouble(liters.getText().toString()), Double.parseDouble(gravity.getText().toString()));
                        IBUResult.setText(df.format(previousResult));
                    }
                    dialogBuilder.dismiss();
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    void clickDeleteHop(Hop hop){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(IBUCalculator.this);
        builder1.setMessage("Are you sure you want to delete this hop?");
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
                        listHops.remove(hop);
                        adapter.setHops(listHops);
                        if(!liters.getText().toString().equals("") && !gravity.getText().toString().equals("") && listHops.size()>0) {
                            Double previousResult = Double.parseDouble(IBUResult.getText().toString());
                            previousResult -= hop.getResult();
                            IBUResult.setText(df.format(previousResult));
                            dialog.cancel();
                        }
                    }
                });



        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private class HopAdapter extends RecyclerView.Adapter<HopHolder>
    {
        private List<Hop> hops;

        public void setHops(List<Hop> hops)
        {
            this.hops = hops;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public HopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HopHolder(getLayoutInflater(), parent);

        }


       @Override
        public void onBindViewHolder(@NonNull HopHolder holder, int position) {

            if (hops != null) {
                Hop hop = hops.get(position);
                holder.bind(hop);
            } else {
                Log.d("MainActivity", "No hops");
            }
        }

        @Override
        public int getItemCount()
        {
            if (hops != null){
                return hops.size();
            } else {
                return 0;
            }
        }

    }
}
