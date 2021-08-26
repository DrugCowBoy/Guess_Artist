package com.demo.guessartist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    // метод при нажатии на кнопку
    public void onClickStartApp(View view) {
        boolean connect = false;// переменная, которая говорит о том, есть ли соединение

        // создаём объект класса ConnectivityManager, который отвечает на запросы о состоянии сети
        ConnectivityManager manager = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        // получим информацию о соединении с сетью
        NetworkInfo nInfo = manager.getActiveNetworkInfo();
        // установим переменную connect как true, если подключение к сети есть
        if (nInfo != null && nInfo.isConnectedOrConnecting()){
            connect = true;
        }

        if (connect){
            Intent intentStart = new Intent(this, MainActivity.class);// создадим интент для запуска другой активности
            startActivity(intentStart);
        } else{
            Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
        }

    }
}