package com.lfi.lfiwidget_app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCall {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://openapi.lyotrade.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    CoinCall lyoTradeAPI = retrofit.create(CoinCall.class);
}
