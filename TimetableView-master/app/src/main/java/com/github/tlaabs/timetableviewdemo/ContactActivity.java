package com.github.tlaabs.timetableviewdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactActivity extends AppCompatActivity {

    EditText firstname;
    EditText lastname;
    EditText number;
    EditText email;
    Button button;
    Button contact;
    Button call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);
        setTitle("전화번호부");

        button = (findViewById(R.id.button));
        contact=(findViewById(R.id.contact));
        call=(findViewById(R.id.call));

        firstname = (findViewById(R.id.name_edit1));
        lastname=(findViewById(R.id.name_edit));
        number = (findViewById(R.id.number_edit));
        email = (findViewById(R.id.email_edit));

        //저장된 내용을 전화번호부 인텐트로 데이터를 넘기기 위한 코드
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, email.getText())
                        .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .putExtra(ContactsContract.Intents.Insert.PHONE,number.getText())
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .putExtra(ContactsContract.Intents.Insert.NAME,firstname.getText() +" "+lastname.getText())
                ;
                startActivity(intent);
            }
        });

        //전화번호부에 저장된 내용을 출력하기위한 코드
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent,0);
            }
        });

        //입력된 전화번호로 바로 전화걸기 위한 코드
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data=number.getText().toString();
                Intent intent3=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+data));
                startActivity(intent3);
            }
        });
    }
}
