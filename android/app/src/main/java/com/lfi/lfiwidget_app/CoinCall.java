package com.lfi.lfiwidget_app;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CoinCall {

    @GET("sapi/v1/klines")
    Call<List<Candle>> getData(@Query("symbol") String symbol, @Query("interval") String interval, @Query("limit") int limit);

}