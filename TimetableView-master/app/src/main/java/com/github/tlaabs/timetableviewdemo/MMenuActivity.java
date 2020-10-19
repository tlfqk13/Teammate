package com.github.tlaabs.timetableviewdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class MMenuActivity extends AppCompatActivity {

    ImageButton btnTime;
    ImageButton btnCall;
    ImageButton btnPage;
    ImageButton btnCafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mmactivity);

        btnTime=(findViewById(R.id.btnTime));
        btnCall=(findViewById(R.id.btnCall));
        btnPage=(findViewById(R.id.btnPage));
        btnCafe=(findViewById(R.id.btnCafe));


        //이미지 버튼을 클릭시 수행하는 동작 구현.

        btnTime.setOnClickListener(new View.OnClickListener() { //TimeTable 버튼
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() { //PhoneBook 버튼
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ContactActivity.class);
                startActivity(intent);
            }
        });

        btnPage.setOnClickListener(new View.OnClickListener() { //Homepage 버튼
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kpu.ac.kr"));
                startActivity(intent);
            }
        });

        btnCafe.setOnClickListener(new View.OnClickListener() { //CafeSearch 버튼
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),CafeSearchActivity.class);
                startActivity(intent);
            }
        });
    }

}
