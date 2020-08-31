package com.autoperfekt.autoperfekt_bus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.hbisoft.pickit.PickiT;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.List;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GET_SINGLE_FILE = 3;

    private MaterialDialog mDialog;

    private VerticalStepperFormView verticalStepperForm;


    private boolean isAttachment = false;
    private List<String> storageFilesPathsList;
    private boolean doubleBackToExitPressedOnce = false;
    private PickiT pickiT;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }






}