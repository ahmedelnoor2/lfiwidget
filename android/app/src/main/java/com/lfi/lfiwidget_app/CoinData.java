package com.lfi.lfiwidget_app;

public class CoinData {

    private String symbol;
    private double open;
    private double close;

    public CoinData(String symbol, double open, double close) {
        this.symbol = symbol;
        this.open = open;
        this.close = close;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }
    public double getPercentage() {
        return ((open - close) / open) * 100;
    }
}
