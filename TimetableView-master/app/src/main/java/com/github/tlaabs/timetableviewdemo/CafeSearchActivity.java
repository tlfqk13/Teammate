package com.github.tlaabs.timetableviewdemo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CafeSearchActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    ListView myList;
    TextView tv;
    Button btn1;
    EditText edit;
    ArrayList<String> myArrayList;
    ArrayList<String> myClearList;
    ArrayAdapter<String> myArrayAdapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafe_search);
        setTitle("주변 카페 검색");
        btn1 = findViewById(R.id.btn1);
        edit = findViewById(R.id.edit);
        myList = (ListView) findViewById(R.id.my_listView);
        myArrayList = new ArrayList<>();
        myClearList = new ArrayList<>();
        myArrayAdapterList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myArrayList);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonRead();
            }
        });

    }

    //조건에 맞는 Json 파일 읽기
    private void jsonRead() {
        AssetManager assetManager = getAssets();
        String json;
        try {
            InputStream is = assetManager.open("jsons/data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            String s = "";

            myArrayAdapterList.clear();//검색 내용 초기화를 통해 연속된 검색 가능

            //조건에 맞는 Json의 내용을 찾기 위한 코드
            //"시군명"을 기준으로 출력
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                if (jo.getString("시군명").equals(edit.getText().toString())) {
                    myArrayList.add(jo.getString("사업장명"));
                    myArrayList.add(jo.getString("소재지도로명주소"));
                    myList.setAdapter(myArrayAdapterList);
                    myArrayAdapterList.notifyDataSetChanged();

                    //리스트에 담긴 내용을 클릭할 경우 자동으로 구글로 검색되는 기능 구현
                    myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            String selected_item=(String)adapterView.getItemAtPosition(position);
                            Toast.makeText(getApplicationContext(),selected_item,Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(Intent.ACTION_WEB_SEARCH);
                            intent.putExtra(SearchManager.QUERY,selected_item);
                            startActivity(intent);
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

