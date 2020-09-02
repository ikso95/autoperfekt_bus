package com.autoperfekt.autoperfekt_bus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.autoperfekt.autoperfekt_bus.Steps.BusNumberStep;
import com.autoperfekt.autoperfekt_bus.Steps.CounterEndStep;
import com.autoperfekt.autoperfekt_bus.Steps.CounterStartStep;
import com.autoperfekt.autoperfekt_bus.Steps.DescriptionStep;
import com.autoperfekt.autoperfekt_bus.Steps.NameStep;
import com.autoperfekt.autoperfekt_bus.Steps.PhotoStep.PhotoStep;
import com.autoperfekt.autoperfekt_bus.Steps.SelectDateStep;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class MainActivity extends AppCompatActivity implements PickiTCallbacks, StepperFormListener{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GET_SINGLE_FILE = 3;

    private MaterialDialog mDialog;

    private boolean isAttachment = false;
    private List<String> storageFilesPathsList;
    private boolean doubleBackToExitPressedOnce = false;
    private PickiT pickiT;


    private VerticalStepperFormView verticalStepperForm;

    private NameStep nameStep;
    private BusNumberStep busNumberStep;
    private SelectDateStep selectDateStep;
    private CounterStartStep counterStartStep;
    private CounterEndStep counterEndStep;
    private DescriptionStep descriptionStep;
    private PhotoStep photoStep;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageFilesPathsList = new ArrayList<>();
        pickiT = new PickiT(this, this);

        nameStep = new NameStep("Imię i nazwisko");
        busNumberStep = new BusNumberStep("Numer busa");
        selectDateStep = new SelectDateStep("Data wyjazdu");
        counterStartStep = new CounterStartStep("Licznik - wyjazd");
        counterEndStep = new CounterEndStep("Licznik - powrót");
        descriptionStep = new DescriptionStep("Uwagi");
        photoStep = new PhotoStep("Zdjęcia busa", MainActivity.this, busNumberStep);

        verticalStepperForm = findViewById(R.id.stepper_form);

        verticalStepperForm.setup(this, nameStep, busNumberStep,selectDateStep, counterStartStep, photoStep, counterEndStep, descriptionStep)
                .stepNextButtonText("Dalej")
                .displayCancelButtonInLastStep(true)
                .lastStepNextButtonText("Wyślij")
                .confirmationStepTitle("Wyślij raport")
                .lastStepCancelButtonText("Anuluj")
                .init();


    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Aby wyjść wciśnij przycisk ponownie", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        //Jeżeli zrobiono zdjęcie
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            isAttachment = true;

            storageFilesPathsList.add("/storage/emulated/0/Android/data/com.autoperfekt.autoperfekt_bus/files/Pictures/" + photoStep.getImageName());

            setNewGalleryAdapter();
        }


        //Jeżeli wybrano plik z pamięci telefonu
        if (requestCode == REQUEST_GET_SINGLE_FILE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
            }
        }
    }


    private void setNewGalleryAdapter() {
        photoStep.setStorageFilesPathsList(storageFilesPathsList);
        photoStep.setAdapter();
    }

    private String makeEmailBody() {

        return "Zgłaszający: " + nameStep.getUserName();  /* + "\n" +
                "Dane kontaktowe zgłaszającego: " + phoneNumberStep.getPhoneNumber() + "\n" +
                "Numer rejestracyjny: " + registrationNumberStep.getRegistrationNumber() + "\n" +
                "Data usterki: " + selectDateStep.getDate() + "  " + (selectTimeStep.getTime().matches("") ? selectTimeStep.getTime() : "") + "\n" +
                "Opis usterki: " + descriptionStep.getDescription() + "\n" +
                "Miejsce zgłoszenia określone na podstawie GPS: " + (!localizationStep.getAddress().matches("") ? (localizationStep.getAddress() + "\n"+ "Długość geograficzna: " + localizationStep.getLongitude()+ "\n" +
                "Szerokość geograficzna: " + localizationStep.getLatitude()) : "użytkownik nie udostępnił lokalizacji") ;  */
    }


    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {

        isAttachment = true;
        storageFilesPathsList.add(path);
        Log.d("pickItPath", path + "   <--- wygenerwowane dzieki bibliotece pickit");
        setNewGalleryAdapter();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCompletedForm() {

       /* mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Proszę czekać ...");
        mDialog.show();*/

        mDialog = new MaterialDialog.Builder(MainActivity.this)
                .setTitle("Wysyłanie!")
                .setMessage("Proszę czekać, w zależności od ilości zdjęć, może to zająć chwilę")
                .setCancelable(false)
                .setAnimation(R.raw.mail_send_as_paper_plane)
                .build();
        mDialog.show();

        //sendEmail(makeEmailBody());


    }

    @Override
    public void onCancelledForm() {
        clearDirectory();
        Intent reloadActivity = new Intent(MainActivity.this, MainActivity.class);
        startActivity(reloadActivity);
        finish();
    }

    private void clearDirectory() {
        File dir = new File("/storage/emulated/0/Android/data/com.autoperfekt.autoperfekt_bus/files/Pictures");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }
}