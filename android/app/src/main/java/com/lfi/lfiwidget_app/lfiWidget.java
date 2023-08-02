package com.lfi.lfiwidget_app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class lfiWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for(int appWidgets: appWidgetIds){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.coin_layout);
            Toast.makeText(context,"Wigets", Toast.LENGTH_SHORT).show();
        }
        // Fetch data for all three coins asynchronously
        new FetchCoinDataTask(context, appWidgetManager, appWidgetIds).execute();
    }

    private static class FetchCoinDataTask extends AsyncTask<Void, Void, CoinData[]> {
        private Context context;
        private AppWidgetManager appWidgetManager;
        private int[] appWidgetIds;
        private CoinData[] coins;
        private CountDownLatch countDownLatch;

        public FetchCoinDataTask(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetIds = appWidgetIds;
            this.coins = new CoinData[3];
            this.countDownLatch = new CountDownLatch(3);
        }

        @Override
        protected CoinData[] doInBackground(Void... voids) {
            // Call API for BTC
            fetchBTCData();

            // Call API for LFi
            fetchLFiData();

            // Call API for cLFi
            fetchCLFiData();

            // Wait for all API calls to complete
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return coins;
        }

        private void fetchBTCData() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://openapi.lyotrade.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CoinCall lyoTradeAPI = retrofit.create(CoinCall.class);
            Call<List<Candle>> btcCall = lyoTradeAPI.getData("BTCUSDT", "1day", 1);
            btcCall.enqueue(new Callback<List<Candle>>() {
                @Override
                public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Candle candle = response.body().get(0);
                        // Format the text with bold and bigger size
                        SpannableString spannableString = new SpannableString("BTC / USDT");
                        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        coins[0] = new CoinData(spannableString.toString(), Double.parseDouble(candle.getOpen()), Double.parseDouble(candle.getClose()));
                    } else {
                        Log.e("MyAppWidgetProvider", "BTC ERROR");
                    }
                    countDownLatch.countDown();
                }

                @Override
                public void onFailure(Call<List<Candle>> call, Throwable t) {
                    Log.e("MyAppWidgetProvider", "BTC ERROR: " + t.getMessage());
                    countDownLatch.countDown();
                }
            });
        }

        private void fetchLFiData() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://openapi.lyotrade.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CoinCall lyoTradeAPI = retrofit.create(CoinCall.class);
            Call<List<Candle>> lfiCall = lyoTradeAPI.getData("LFI1USDT", "1day", 1);
            lfiCall.enqueue(new Callback<List<Candle>>() {
                @Override
                public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Candle candle = response.body().get(0);
                        coins[1] = new CoinData("LFi / USDT", Double.parseDouble(candle.getOpen()), Double.parseDouble(candle.getClose()));
                    } else {
                        Log.e("MyAppWidgetProvider", "LFi ERROR");
                    }
                    countDownLatch.countDown();
                }

                @Override
                public void onFailure(Call<List<Candle>> call, Throwable t) {
                    Log.e("MyAppWidgetProvider", "LFi ERROR: " + t.getMessage());
                    countDownLatch.countDown();
                }
            });
        }

        private void fetchCLFiData() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://openapi.lyotrade.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CoinCall lyoTradeAPI = retrofit.create(CoinCall.class);
            Call<List<Candle>> clfiCall = lyoTradeAPI.getData("CLFIUSDT", "1day", 1);
            clfiCall.enqueue(new Callback<List<Candle>>() {
                @Override
                public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Candle candle = response.body().get(0);
                        coins[2] = new CoinData("cLFi / USDT", Double.parseDouble(candle.getOpen()), Double.parseDouble(candle.getClose()));
                    } else {
                        Log.e("MyAppWidgetProvider", "CLFi ERROR");
                    }
                    countDownLatch.countDown();
                }

                @Override
                public void onFailure(Call<List<Candle>> call, Throwable t) {
                    Log.e("MyAppWidgetProvider", "CLFi ERROR: " + t.getMessage());
                    countDownLatch.countDown();
                }
            });
        }

        @Override
        protected void onPostExecute(CoinData[] coins) {
            super.onPostExecute(coins);

            // Update the widget with fetched data
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, coins);
            }
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, CoinData[] coins) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lfi_widget);

        // Find the container view for coins
        int containerId = R.id.container_coins;
        views.removeAllViews(containerId);

        // Inflate the divider layout
        RemoteViews dividerLayout = new RemoteViews(context.getPackageName(), R.layout.divider_layout);

        // Add the first coin
        for (CoinData coin : coins) {
            // Create the coin layout
            RemoteViews coinLayout = new RemoteViews(context.getPackageName(), R.layout.coin_layout);

            // Populate data for each coin
// Get the coin symbol
            String coinSymbol = coin.getSymbol();

// Create a SpannableString to apply style to the symbol text
            SpannableString spannableSymbol = new SpannableString(coinSymbol);

// Apply the style to the first three characters (assuming coinSymbol.length() >= 3)
            spannableSymbol.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableSymbol.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
// Increase the text size for the first three characters (adjust the size value as needed)
            spannableSymbol.setSpan(new AbsoluteSizeSpan(16, true), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
// Set the formatted and styled symbol to the TextView
            coinLayout.setTextViewText(R.id.text_symbol, spannableSymbol);

            coinLayout.setTextViewText(R.id.text_price, String.format("%.4f", coin.getClose()));
            coinLayout.setTextViewText(R.id.text_percentage, String.format("%.2f", coin.getPercentage()) + "%");
            coinLayout.setInt(R.id.text_percentage, "setWidth", 30);
            // Determine the color for the button based on price change
            if (coin.getPercentage() > 0) {
                coinLayout.setInt(R.id.text_percentage, "setBackgroundResource", R.drawable.button_background); // Set green button background
            } else if (coin.getPercentage() < 0) {
                coinLayout.setInt(R.id.text_percentage, "setBackgroundResource", R.drawable.red_button_background); // Set red button background
            } else {
                coinLayout.setInt(R.id.text_percentage, "setBackgroundResource", R.drawable.button_background); // Set default button background (if needed)
            }

            // Add the coin layout to the container
            views.addView(containerId, coinLayout);

            // Add the divider after each coin except the last one
            if (coin != coins[coins.length - 1]) {
                views.addView(containerId, dividerLayout);
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}