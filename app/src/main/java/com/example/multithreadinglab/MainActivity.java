package com.example.multithreadinglab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MultithreadingLab";
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        Button btnCalculateThread = findViewById(R.id.btnCalculateThread);
        Button btnLoadImage = findViewById(R.id.btnLoadImage);

        // ================= ЗАДАНИЕ 2 =================
        // Демонстрация "зависания" интерфейса
        btnCalculate.setOnClickListener(v -> {
            longCalculation();
            Toast.makeText(this, "Вычисления завершены", Toast.LENGTH_SHORT).show();
        });

        // ================= ЗАДАНИЕ 3 =================
        // Выполнение вычислений в отдельном потоке
        btnCalculateThread.setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    longCalculation(); // Выполняется в фоне, UI не блокируется

                    // Обновление UI только через runOnUiThread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Вычисления завершены", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        });

        // ================= ЗАДАНИЕ 4 =================
        // Загрузка изображения из интернета с отображением прогресса
        btnLoadImage.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Имитация прогресса (не связана с реальной загрузкой)
                        for (int i = 0; i <= 100; i += 10) {
                            Thread.sleep(200); // Имитация работы
                            final int progress = i;
                            runOnUiThread(() -> progressBar.setProgress(progress));
                        }

                        // Реальная загрузка изображения
                        Bitmap bitmap = loadImage("https://el.ncfu.ru/pluginfile.php/1/theme_moove/logo/1769692740/СКФУ%20северокавказскийфедеральныйуниверситет.png");

                        // Обновление UI после завершения загрузки
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }).start();
        });
    }

    // Метод, имитирующий длительные вычисления
    private void longCalculation() {
        long result = 0;
        // Цикл на 5 млрд итераций для демонстрации нагрузки
        for (int i = 0; i < 5_000_000_000L; i++) {
            result += i;
        }
        Log.d(TAG, "Результат: " + result);
    }

    // Метод загрузки изображения по URL (Задание 4)
    private Bitmap loadImage(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();

        InputStream input = connection.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }
}