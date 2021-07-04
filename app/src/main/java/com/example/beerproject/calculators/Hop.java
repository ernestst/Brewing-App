package com.example.beerproject.calculators;

public class Hop {

    Double amount;
    Double alpha;
    Double minute;

    Double result;

    Hop(Double amount, Double alpha, Double minute){
        this.amount = amount;
        this.alpha = alpha;
        this.minute = minute;
    }

    Double calculateResult(Double liters, Double gravity){
        result = 18.11 + (13.86 * Math.tanh(minute - 31.32)/18.27);
        result = result * amount * alpha * 263.2143;
        result = result/10000;
        Double plato = gravity;
        Double SG = 1 + (plato /(258.6 - ((plato/258.2) *227.1)));
        Double GA = 0.0;
        if(SG > 1.050){
            GA = (SG - 1.050)/0.2;
        }
        result = result/((liters/3.785) * (1+GA));
        return result;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Double getMinute() {
        return minute;
    }

    public void setMinute(Double minute) {
        this.minute = minute;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }
}
