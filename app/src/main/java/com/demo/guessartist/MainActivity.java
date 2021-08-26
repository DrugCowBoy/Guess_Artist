package com.demo.guessartist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.FieldClassification;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.demo.guessartist.api.ApiFactory;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String URL_PICTURE = "https://lermontovgallery.ru";

    private ArrayList<String> titles;
    private ArrayList<String> urlImages;

    private ImageView imageViewArt;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    private ArrayList<Button> buttons;// массив с кнопками

    private int trueRandomButton;// номер кнопки с правильным ответом

    private boolean screenIsWorking;// логическая переменная, которая показывает работает или не работает экран

    private ApiFactory apiFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        titles = new ArrayList<>();
        urlImages = new ArrayList<>();
        imageViewArt = findViewById(R.id.imageViewArt);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        buttons = new ArrayList<>();
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);

        screenIsWorking = true;

        // загрузим html-код с сайта
        apiFactory = ApiFactory.getApiFactory();
        Observable<String> content = apiFactory.getApiService().getContent();
        Disposable disposable = content
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        // получим названия и изображения из обрезанного контента
                        getTitlesAndImages(s);

                        // установим рандомное изображение в макет и названия картин в кнопки
                        setGame();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
                        Log.i("My", "Error: "+ throwable.getMessage());
                    }
                });

    }


    // метод для вытаскивания названий и изображений из контента
    private void getTitlesAndImages(String content){
        // (.*?) - пропущенная часть строки; \" - кавычка
        Pattern patternTitle = Pattern.compile("\" title=\"(.*?)\" align=\"");// паттерн для отбора названий
        Pattern patternImage = Pattern.compile("\" src=\"(.*?)\" height=\"");// паттерн для отбора изображений
        Matcher matcherTitle = patternTitle.matcher(content);
        Matcher matcherImage = patternImage.matcher(content);
        while(matcherTitle.find()){
            titles.add(matcherTitle.group(1));// добавляем все названия в массив titles
        }
        while (matcherImage.find()){
            urlImages.add(matcherImage.group(1));// добавляем все адреса картинок в массив urlImages
        }
    }

    // метод для установки в макет рандомного изображения и вариантов ответа
    private void setGame(){
        // установим рандомное изображение в макет
        int trueRandomVariant = (int)(Math.random()*urlImages.size());// получаем случайное число от 0 до 48
        String trueRandomImg = URL_PICTURE + urlImages.get(trueRandomVariant);// получаем случайную строку с адресом изображения из массива
        Picasso.get().load(trueRandomImg).into(imageViewArt);// установили изображение

        // установим правильный ответ (соответствует изображению) в случайную кнопку
        String trueRandomTitle = titles.get(trueRandomVariant);// получим название, которое соответствует изображению
        trueRandomButton = (int)(Math.random()*4);// получили рандомную позицию кнопки от 0 до 3
        buttons.get(trueRandomButton).setText(trueRandomTitle);// в рандомную кнопку устанавливаю текст с правильным ответом
        // установили правильный ответ в кнопку trueRandomButton

        // установим неправильные ответы для остальных кнопок
        for (int i = 0; i<=3;i++){
            if (i != trueRandomButton){
                int falseRandomVariant;
                do{
                    falseRandomVariant = (int)(Math.random()*urlImages.size());// получаем случайное число от 0 до 48
                } while (falseRandomVariant == trueRandomVariant);
                String falseRandomTitle = titles.get(falseRandomVariant);// получили случайное неправильное название
                buttons.get(i).setText(falseRandomTitle);// в кнопку устанавливаю текст с неправильным ответом
            }

        }
    }

    // метод при нажатии на одну из кнопок
    public void onClickAnswer(View view) {
        if (screenIsWorking){
            Button button = (Button) view;// взяли нажатую кнопку
            int buttonPosition = Integer.parseInt(button.getTag().toString());// взяли позицию нажатой кнопки по её тэгу

            // проверяем, совпадает ли позиция выбранной кнопки с правильным ответом
            if (trueRandomButton == buttonPosition){
                button.setBackgroundColor(Color.parseColor("#00e673"));
            } else{
                button.setBackgroundColor(Color.parseColor("#ff3300"));
            }
            screenIsWorking = false;// изменим screenIsWorking на false, чтобы нельзя было нажать экран

            // создадим handler для задержки, ведь мы должы успеть показать результат пользователю - установим для всех кнопок синий цвет через 0.5 сек
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    button1.setBackgroundColor(Color.parseColor("#0099cc"));
                    button2.setBackgroundColor(Color.parseColor("#0099cc"));
                    button3.setBackgroundColor(Color.parseColor("#0099cc"));
                    button4.setBackgroundColor(Color.parseColor("#0099cc"));
                    // установим рандомное изображение в макет и названия картин в кнопки
                    setGame();
                    screenIsWorking = true;
                }
            };
            handler.postDelayed(runnable, 500);
        }

    }


}