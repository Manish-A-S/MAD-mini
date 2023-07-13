package com.example.finalmedia;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Displaying extends AppCompatActivity {

    TextView tv1,tv2;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaying);
        tv1=findViewById(R.id.textView);
        tv2=findViewById(R.id.textView4);
        button=findViewById(R.id.button);

        String head=getIntent().getStringExtra("head");
        String body=getIntent().getStringExtra("body");

        tv1.setText(head);
        tv2.setText(body);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Displaying.this,MainActivity.class);
                startActivity(intent);
            }
    });

}
}