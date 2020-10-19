package com.github.tlaabs.timetableviewdemo;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int RESULT_OK_ADD = 1;
    public static final int RESULT_OK_EDIT = 2;
    public static final int RESULT_OK_DELETE = 3;

    private Context context;

    private Button deleteBtn;
    private Button submitBtn;
    private EditText subjectEdit;
    private Spinner daySpinner;
    private TextView startTv;
    private TextView endTv;

    private int mode;

    private Schedule schedule;
    private int editIdx;

    // 파일명
    int a= 1;
    ArrayList<String> day = new ArrayList<>();
    ArrayList<String> starttime = new ArrayList<>();
    ArrayList<String> endtime = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("시간표 추가");
        init();
    }

    private void init(){
        this.context = this;
        deleteBtn = findViewById(R.id.delete_btn);
        submitBtn = findViewById(R.id.submit_btn);
        subjectEdit = findViewById(R.id.subject_edit);
        daySpinner = findViewById(R.id.day_spinner);
        startTv = findViewById(R.id.start_time);
        endTv = findViewById(R.id.end_time);

        //기본 시간 입력
        schedule = new Schedule();
        schedule.setStartTime(new Time(10,0));
        schedule.setEndTime(new Time(13,30));

        checkMode();
        initView();
    }

   //추가 모드인지 수정 모드인지 확인
    private void checkMode(){
        Intent i = getIntent();
        mode = i.getIntExtra("mode",MainActivity.REQUEST_ADD);

        if(mode == MainActivity.REQUEST_EDIT){
            loadScheduleData(); //저장했던 데이터 로드
            deleteBtn.setVisibility(View.VISIBLE); //삭제버튼 보이기
        }
    }

    //뷰 초기화
    private void initView(){
        submitBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schedule.setDay(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //시작시가 텍스트뷰 선택 시
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(context,listener,schedule.getStartTime().getHour(), schedule.getStartTime().getMinute(), false);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    startTv.setText(hourOfDay + ":" + minute);
                    schedule.getStartTime().setHour(hourOfDay);
                    schedule.getStartTime().setMinute(minute);
                }
            };
        });
        //끝나는 시간 텍스트뷰 선택시
        endTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(context,listener,schedule.getEndTime().getHour(), schedule.getEndTime().getMinute(), false);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    endTv.setText(hourOfDay + ":" + minute);
                    schedule.getEndTime().setHour(hourOfDay);
                    schedule.getEndTime().setMinute(minute);
                }
            };
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_btn: //추가 버튼을 눌렀을 때 추가하는지 수정하는지 결정
                if(mode == MainActivity.REQUEST_ADD){
                    inputDataProcessing();
                    Intent i = new Intent();
                    ArrayList<Schedule> schedules = new ArrayList<Schedule>();
                    schedules.add(schedule);
                    i.putExtra("schedules",schedules);
                    setResult(RESULT_OK_ADD,i);
                    finish();

                    int a = 0;
                    String a_str = String.valueOf(a);
                    for(int i2 =0; i2<100; i2++){
                        a_str = String.valueOf(a);
                        if(checkfile(a_str)) {
                            //     checkclear(a_str);
                            break;
                        }
                        else{
                            a++;
                            break;
                        }
                    }


                    a_str = String.valueOf(a);
                    WriteTextFile(a_str,schedule.getDay() + "/" + startTv.getText().toString().replace(":",".") + "/" + endTv.getText().toString().replace(":","."));
                    Log.e("aaa",schedule.getDay() + "/" + startTv.getText().toString().replace(":",".") + "/" + endTv.getText().toString().replace(":","."));

                }
                else if(mode == MainActivity.REQUEST_EDIT){ //메인액티비티에서 수정 신호를 받았을 때
                    inputDataProcessing();
                    Intent i = new Intent();
                    ArrayList<Schedule> schedules = new ArrayList<Schedule>();
                    schedules.add(schedule);
                    i.putExtra("idx",editIdx);
                    i.putExtra("schedules",schedules);
                    setResult(RESULT_OK_EDIT,i); //수정신호 반환
                    finish();
                }
                break;
            case R.id.delete_btn: //수정할 때 생기는 삭제버튼을 눌렀을 때
                Intent i = new Intent();
                i.putExtra("idx",editIdx);
                setResult(RESULT_OK_DELETE, i); //삭제신호 반환
                finish();
                break;
        }
    }

    //저장한 데이터를 다시 불러옴
    private void loadScheduleData(){
        Intent i = getIntent();
        editIdx = i.getIntExtra("idx",-1);
        ArrayList<Schedule> schedules = (ArrayList<Schedule>)i.getSerializableExtra("schedules");
        schedule = schedules.get(0);
        subjectEdit.setText(schedule.getClassTitle());
        daySpinner.setSelection(schedule.getDay());
    }

    private void inputDataProcessing(){
        String a_str = String.valueOf(a);

        for(int i =0; i<100; i++){

            if(checkfile(String.valueOf(i))) {
                String s = ReadTextFile(String.valueOf(i));
                day.add(s.trim().split("/")[0]);
                starttime.add(s.trim().split("/")[1]);
                endtime.add(s.trim().split("/")[2]);
            }
            else{ }
        }

        Log.e("aaa", "배열 사이즈 몇 : " + starttime.size());
        if(starttime.size() !=0) {
            String max_start = starttime.get(0);
            String min_start = endtime.get(0);
            for (int j = 0; j < starttime.size(); j++) {
                if(day.get(j).equals(String.valueOf(schedule.getDay()))) {
                    if (Double.parseDouble(min_start) > Double.parseDouble(starttime.get(j))) {
                        a++;
                    }
                }
            }
        }


        a_str = String.valueOf(a);

        schedule.setClassTitle(subjectEdit.getText().toString());
        schedule.setClassPlace(a_str);
        schedule.setProfessorName("");
    }




    // 파일 쓰기
    public void WriteTextFile(String foldername, String contents) {
        try{
            File file = new File(getFilesDir() + foldername +".txt");
            if(file.exists())  file.delete();
            BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() + foldername + ".txt", true));
            bw.write(contents+ '\n');
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean checkfile(String foldername){
        boolean c = false;
        File file = new File(getFilesDir() + foldername +".txt");
        if(file.exists()) c = true;
        return c;
    }
    public void checkclear(String foldername){
        File file = new File(getFilesDir() + foldername +".txt");
        if(file.exists()) file.delete();
    }
    //파일 읽기
    public String ReadTextFile(String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(getFilesDir() + name +".txt"));
            String readStr = "";
            String str = null;
            while (((str = br.readLine()) != null)) {
                readStr += str + "\n";
            }
            br.close();
            return readStr;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

}
