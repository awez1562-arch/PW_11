package com.example.multithreadinglab;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        // Навигация к основному ходу работы
        findViewById(R.id.btn0).setOnClickListener(v ->
                startActivity(new Intent(MainMenu.this, MainActivity.class)));

        // Навигация к заданию
        findViewById(R.id.btn1).setOnClickListener(v ->
                startActivity(new Intent(MainMenu.this, TMainActivity.class)));
    }
}
