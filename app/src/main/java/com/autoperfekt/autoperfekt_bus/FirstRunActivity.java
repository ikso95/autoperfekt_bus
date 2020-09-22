package com.autoperfekt.autoperfekt_bus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;

import com.example.myloadingbutton.MyLoadingButton;

public class FirstRunActivity extends AppCompatActivity {

    private EditText password;
    private MyLoadingButton myLoadingButton;
    private SharedPreferences sharedPreferences;
    private final static String appPassword="busraport";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);


        password=findViewById(R.id.app_password_EditText);
        myLoadingButton=findViewById(R.id.my_loading_button);

        sharedPreferences = getApplicationContext().getSharedPreferences("AppPassword", Context.MODE_PRIVATE); // 0 - for private mode

        if(isLoggedIn())
        {
            Intent goToMainActivityIntent = new Intent(FirstRunActivity.this, Reminder.class);
            startActivity(goToMainActivityIntent);
            finish();
        }


        myLoadingButton.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
            @Override
            public void onMyLoadingButtonClick() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(password.getText().toString().equals(appPassword))
                        {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("AppPassword", true); // Storing boolean - true/false
                                    editor.commit(); // commit changes

                                    Intent goToMainActivityIntent = new Intent(FirstRunActivity.this, MainActivity.class);
                                    startActivity(goToMainActivityIntent);
                                    finish();
                                }
                            }, 1000);
                            myLoadingButton.showDoneButton();
                        }
                        else
                        {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myLoadingButton.showNormalButton();
                                }
                            }, 1000);
                            myLoadingButton.showErrorButton();
                        }
                    }
                }, 1000);
                myLoadingButton.showLoadingButton();

            }
        });


    }


    private boolean isLoggedIn() {
        if(!sharedPreferences.getBoolean("AppPassword",false))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

}