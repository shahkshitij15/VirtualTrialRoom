package com.example.arshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Form extends AppCompatActivity {

    Button submit;
    EditText height,weight;
    SharedPreferences sharedPreferences;

    public static final String MyPREFERENCES = "ARShop" ;
    public static final String Height = "heightKey";
    public static final String Weight = "weightKey";
    public static final String Size = "sizeKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        submit=findViewById(R.id.button);
        height=findViewById(R.id.height);
        weight=findViewById(R.id.weight);
        sharedPreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if(sharedPreferences.contains(Size))
        {
            startActivity(new Intent(this,WelcomeActivity.class));
            finish();
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int h=6;
                int w=72;
                try {
                    h = Integer.parseInt(height.getText().toString());
                    w = Integer.parseInt(weight.getText().toString());
                }
                catch (NumberFormatException nfe)
                {
                    System.out.println("Could not parse " + nfe);
                }

                String s="L";

                if(h>=180)
                {
                    s="L";
                }
                else if(h>=165 && w>70)
                {
                    s="L";
                }
                else if(h>165 && w<70)
                {
                    s="M";
                }
                else if(h>=150 && w>60)
                {
                    s="M";
                }
                else
                {
                    s="S";
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt(Height, h);
                editor.putInt(Weight, w);
                editor.putString(Size, s);
                editor.commit();
                Toast.makeText(Form.this,"Thanks",Toast.LENGTH_LONG).show();

                startActivity(new Intent(Form.this, WelcomeActivity.class));
            }
        });
    }
    public boolean checkPreference() {
        boolean status = false ;

        if(sharedPreferences.getString(Size,"null").equals("null")){
            status = false;
        }
        else {
            status = true;
        }

        return status;
    }
}
