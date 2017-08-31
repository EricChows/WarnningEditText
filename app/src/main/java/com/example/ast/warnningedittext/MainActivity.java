package com.example.ast.warnningedittext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WarnningEditText warnningEditText = (WarnningEditText) findViewById(R.id.warnning_et);
        warnningEditText.setRegex("^\\d{5}$");
        warnningEditText.setOnInputMatchListener(new WarnningEditText.OnInputMatchListener() {
            @Override
            public void onInputWrong() {
                Toast.makeText(MainActivity.this, "输入错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInputCorrect() {
                Toast.makeText(MainActivity.this, "输入正确", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
