package com.example.multithreadinglab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TMainActivity extends AppCompatActivity {

    private static final String TAG = "MultithreadingLab";

    // Элементы для вычислений
    private Button btnCalculate;
    private ProgressBar progressBarCalc;
    private TextView textViewResult;

    // Элементы для загрузки изображений
    private Button btnLoadImages;
    private ProgressBar progressBarImages;
    private LinearLayout imagesContainer;

    // Пул потоков для вычислений
    private ExecutorService executor;

    // Список URL изображений (3-5 изображений)
    private final String[] imageUrls = {
            "https://img.freepik.com/free-vector/cute-fox-sitting-with-scarf-autumn-cartoon-icon-illustration-animal-nature-icon-isolated-flat-cartoon-style_138676-3115.jpg?t=st=1776516390~exp=1776519990~hmac=8a7792a6fadd10009644a7bbc8e48ad3e824fc069481e2b9352f9150232b3c50&w=360",
            "https://img.freepik.com/free-vector/hand-drawn-animal-silhouette-illustration_23-2149561172.jpg?t=st=1776516453~exp=1776520053~hmac=57cfcaa955a3e97dd688317b918e399f9085b7009a740af7e1a649c0c2f79fe9&w=1480",
            "https://img.freepik.com/free-vector/origami-abstract-concept-vector-illustration-art-paper-folding-mental-practice-fine-motor-skills-development-useful-pastime-social-isolation-how-video-tutorial-abstract-metaphor_335657-1679.jpg?t=st=1776516619~exp=1776520219~hmac=9204453e26e55c5e1781eb509ae0c8f494795f3ea88ed70984745ee2f7647d51&w=360",
            "https://img.freepik.com/free-vector/hand-drawn-frog-outline-illustration_23-2149285228.jpg?t=st=1776516187~exp=1776519787~hmac=ec5bfbe7ca2c16fd428e1b07e8ba9b2e5df1aebba19f6d5c8039d7822c81a970&w=1060",
            "https://img.freepik.com/free-vector/flat-design-man-forest-illustration_52683-94451.jpg?t=st=1776516284~exp=1776519884~hmac=c9969729da5c61b381e7006a72779774eeea10a2edb9255112ac7c44617b7254&w=1060"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tactivity_main);

        // Инициализация элементов UI
        btnCalculate = findViewById(R.id.btnCalculate);
        progressBarCalc = findViewById(R.id.progressBarCalc);
        textViewResult = findViewById(R.id.textViewResult);

        btnLoadImages = findViewById(R.id.btnLoadImages);
        progressBarImages = findViewById(R.id.progressBarImages);
        imagesContainer = findViewById(R.id.imagesContainer);

        // Создаём пул из одного потока
        executor = Executors.newSingleThreadExecutor();

        // Обработчик кнопки вычислений
        btnCalculate.setOnClickListener(v -> performCalculations());

        // Обработчик кнопки загрузки изображений
        btnLoadImages.setOnClickListener(v -> loadImages());
    }

    /*
    Часть 1: Выполнение вычислений в фоновом потоке
    Вариант 1:
    a. Сумму отрицательных элементов массива
    b. Произведение элементов массива, расположенных между максимальным и минимальным элементом
    */
    private void performCalculations() {
        progressBarCalc.setVisibility(View.VISIBLE);
        progressBarCalc.setProgress(0);
        textViewResult.setText("Вычисления выполняются...");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Генерация массива случайных чисел (100 элементов, от -100 до 100)
                    final double[] array = generateRandomArray(100);

                    // Имитация прогресса вычислений
                    for (int i = 0; i <= 50; i += 10) {
                        final int progress = i;
                        runOnUiThread(() -> progressBarCalc.setProgress(progress));
                        SystemClock.sleep(100);
                    }

                    // Выполнение заданий варианта 11 в фоновом потоке
                    final int minAbsIndex = findIndexOfMinAbs(array);
                    final double sumAbsAfterNeg = sumAbsAfterFirstNegative(array);

                    // Обновление UI после завершения
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarCalc.setProgress(100);

                            StringBuilder sb = new StringBuilder();
                            sb.append("Результат вычислений (Вариант 11):\n\n");
                            sb.append("Массив: ").append(arrayToString(array)).append("\n\n");
                            // Примечание: нумерация с 1.
                            sb.append("a. Номер (индекс) минимального по модулю элемента: ").append(minAbsIndex + 1);
                            sb.append(" (значение: ").append(String.format("%.2f", array[minAbsIndex])).append(")\n\n");
                            sb.append("b. Сумма модулей элементов после первого отрицательного: ").append(String.format("%.2f", sumAbsAfterNeg)).append("\n");

                            textViewResult.setText(sb.toString());
                            progressBarCalc.setVisibility(View.GONE);
                            Toast.makeText(TMainActivity.this, "Вычисления завершены", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        progressBarCalc.setVisibility(View.GONE);
                        Toast.makeText(TMainActivity.this, "Ошибка вычислений", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private int findIndexOfMinAbs(double[] array) {
        if (array == null || array.length == 0) return -1;
        int minIndex = 0;
        double minAbsVal = Math.abs(array[0]);
        for (int i = 1; i < array.length; i++) {
            double currentAbs = Math.abs(array[i]);
            if (currentAbs < minAbsVal) {
                minAbsVal = currentAbs;
                minIndex = i;
            }
        }
        return minIndex;
    }

    private double sumAbsAfterFirstNegative(double[] array) {
        if (array == null || array.length == 0) return 0;

        int firstNegIndex = -1;
        // Поиск первого отрицательного элемента
        for (int i = 0; i < array.length; i++) {
            if (array[i] < 0) {
                firstNegIndex = i;
                break;
            }
        }

        // Если отрицательных элементов нет или он стоит последним — сумма равна 0
        if (firstNegIndex == -1 || firstNegIndex >= array.length - 1) {
            return 0;
        }

        double sum = 0;
        // Суммируем модули всех элементов ПОСЛЕ найденного индекса
        for (int i = firstNegIndex + 1; i < array.length; i++) {
            sum += Math.abs(array[i]);
        }
        return sum;
    }

    // Генерация массива случайных чисел
    private double[] generateRandomArray(int size) {
        double[] array = new double[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            // Генерируем числа от -100 до 100
            array[i] = (random.nextInt(201) - 100);
        }
        return array;
    }
/*
    // Вычисление суммы отрицательных элементов
    private double calculateSumOfNegative(double[] array) {
        double sum = 0;
        for (double value : array) {
            if (value < 0) {
                sum += value;
            }
        }
        return sum;
    }

    // Вычисление произведения элементов между максимальным и минимальным
    private double calculateProductBetweenMaxMin(double[] array) {
        if (array.length < 2) return 0;

        // Находим индексы максимального и минимального элементов
        int maxIndex = 0;
        int minIndex = 0;

        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
            if (array[i] < array[minIndex]) {
                minIndex = i;
            }
        }

        // Определяем начальный и конечный индексы
        int startIndex = Math.min(maxIndex, minIndex) + 1;
        int endIndex = Math.max(maxIndex, minIndex);

        // Если элементы соседние, возвращаем 0 или сообщение
        if (startIndex > endIndex) {
            return 0;
        }

        // Вычисляем произведение
        double product = 1;
        boolean hasElements = false;

        for (int i = startIndex; i < endIndex; i++) {
            product *= array[i];
            hasElements = true;
        }

        return hasElements ? product : 0;
    }*/

    // Преобразование массива в строку
    private String arrayToString(double[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(String.format("%.1f", array[i]));
            if (i < array.length - 1) {
                sb.append(", ");
            }
            // Ограничиваем длину строки
            if (i > 20) {
                sb.append(", ...");
                break;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // Часть 2: Загрузка изображений из интернета
    private void loadImages() {
        imagesContainer.removeAllViews();
        progressBarImages.setVisibility(View.VISIBLE);
        progressBarImages.setProgress(0);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final int totalImages = imageUrls.length;

                    for (int i = 0; i < totalImages; i++) {
                        final Bitmap bitmap = loadImage(imageUrls[i]);
                        final int index = i;

                        // Обновление прогресса и добавление в UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int progress = ((index + 1) * 100) / totalImages;
                                progressBarImages.setProgress(progress);

                                if (bitmap != null && !bitmap.isRecycled()) {
                                    ImageView imageView = new ImageView(TMainActivity.this);

                                    // ВАЖНО: Правильные LayoutParams
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            600,  // ширина в пикселях
                                            600   // высота в пикселях
                                    );
                                    params.setMargins(0, 8, 0, 8); // отступы
                                    imageView.setLayoutParams(params);

                                    // Устанавливаем масштабирование
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                    // Устанавливаем изображение
                                    imageView.setImageBitmap(bitmap);

                                    // Добавляем в контейнер
                                    imagesContainer.addView(imageView);

                                    Toast.makeText(TMainActivity.this, "Загружено " + (index + 1) + " из " + totalImages, Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Изображение " + (index + 1) + " добавлено. Размер: " +
                                            bitmap.getWidth() + "x" + bitmap.getHeight());
                                } else {
                                    Log.e(TAG, "Изображение " + (index + 1) + " не загружено или null");
                                }
                            }
                        });

                        SystemClock.sleep(500);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarImages.setVisibility(View.GONE);
                            Toast.makeText(TMainActivity.this,
                                    "Загружено " + totalImages + " изображений",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarImages.setVisibility(View.GONE);
                            Toast.makeText(TMainActivity.this,
                                    "Ошибка: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    // Загрузка изображения по URL
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Закрываем пул потоков
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}