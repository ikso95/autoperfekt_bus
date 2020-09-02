package com.autoperfekt.autoperfekt_bus.Steps.PhotoStep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.autoperfekt.autoperfekt_bus.R;
import com.autoperfekt.autoperfekt_bus.Steps.BusNumberStep;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.Step;

public class PhotoStep extends Step<String> {


    private AppCompatImageButton takePhotoButton;
    private AppCompatImageButton loadPhotoButton;
    private LayoutInflater inflater;
    private View view;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int CAMERA_PERMISSION_CODE = 2;
    static final int REQUEST_GET_SINGLE_FILE = 3;
    static final int STORAGE_PERMISSION_CODE = 4;

    private File imageFile;
    private Uri imageUri;

    private Activity myParentActivity;

    private String registrationNumber;

    private String currentPhotoPath = null;

    private String imageName;

    private BusNumberStep busNumberStep;

    //---------------------------------------
    public RecyclerView recyclerView;

    private GalleryAdapter galleryAdapter;

    private List<String> storageFilesPathsList = new ArrayList<>();

    public void setStorageFilesPathsList(List<String> storageFilesPathsList) {
        this.storageFilesPathsList = storageFilesPathsList;
    }
    //-------------------------

    public PhotoStep(String stepTitle, Activity activity, BusNumberStep busNumberStep) {
        super(stepTitle);
        this.myParentActivity = activity;     //potrzebne zeby wywoływać intenty z poziomy mainActivity
        this.busNumberStep = busNumberStep;
    }



    @Override
    public View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.

        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.step_photo, null);

        takePhotoButton = view.findViewById(R.id.take_photo_button);
        loadPhotoButton = view.findViewById(R.id.load_photo_button);

        recyclerView = view.findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check Camera permission
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Permission not granted, request permission
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

                } else {
                    // Permission has already been granted
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);      //wywołanie uruchomienia aparatu


                    if (cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        imageFile = null;

                        imageFile = createImageFile();                                  //stworzenie pliku do którego zostanie zapisane zdjecie - bitmapa


                        if (imageFile != null) {                                            //jezeli plik istnieje podajemy Uri-adres pod ktorym ma byc zapisany obraz, miejsce na dysku
                            imageUri = FileProvider.getUriForFile(getContext(), "com.autoperfekt.autoperfekt_bus.fileprovider", imageFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);   //dodajemy obraz do intent  - przez to ze dodajemy uri mamy adres zdjecia, nie otrzymamy w extras thumbnail obrazu (miniaturki)
                            myParentActivity.startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);    //uruchamiamy intnet
                        }


                    }

                }
            }
        });


        loadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //Permission not granted, request permission
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

                } else {
                    //Permission granted
                    Intent intent = new Intent();
                    intent.setType("image/* video/*");                  //to choose all files image/* or image/jpg or video/* or video/mp4
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    myParentActivity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);
                }

            }
        });

        return view;

    }


    public void setAdapter()
    {
        if(storageFilesPathsList.size()==0)
        {
            recyclerView.setVisibility(View.GONE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            galleryAdapter = new GalleryAdapter(storageFilesPathsList, getContext(),PhotoStep.this);
            recyclerView.setAdapter(galleryAdapter);
            Log.d("photosSize", String.valueOf(storageFilesPathsList.size()));
        }

    }

    private File createImageFile() {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("_yyyyMMdd_HHmmss").format(new Date());


        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        registrationNumber = busNumberStep.getBusNumber();

        imageName = registrationNumber + timeStamp + ".jpg";

        File image = new File(storageDir, imageName);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public String getImageName() {
        return imageName;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.

        boolean isTimeValid = true;
        return new IsDataValid(isTimeValid);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        return "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String userName = getStepData();
        return !userName.isEmpty() ? userName : "";
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
        setAdapter();
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        //timeTextView.setText(stepData);
    }
}