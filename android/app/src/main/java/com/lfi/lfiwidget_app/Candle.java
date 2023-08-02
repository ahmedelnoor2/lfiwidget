package com.lfi.lfiwidget_app;

import com.google.gson.annotations.SerializedName;

public class Candle {
    @SerializedName("open")
    private String open;

    @SerializedName("close")
    private String close;

    @SerializedName("symbol")
    private String symbol;

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
