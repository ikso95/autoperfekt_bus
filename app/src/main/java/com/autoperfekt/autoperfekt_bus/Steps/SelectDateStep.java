package com.autoperfekt.autoperfekt_bus.Steps;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;

import com.autoperfekt.autoperfekt_bus.R;

import java.util.Calendar;

import ernestoyaquello.com.verticalstepperform.Step;

public class SelectDateStep extends Step<String> {


    private DatePickerDialog picker;
    private TextView dateTextView;
    private AppCompatImageButton pickDateButton;
    private LayoutInflater inflater;
    private View view;
    private String date;
    private SharedPreferences sharedPreferences;

    public SelectDateStep(String stepTitle) {
        super(stepTitle);
    }



    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.step_select_date,null);

        pickDateButton = view.findViewById(R.id.pick_date_button);
        dateTextView = view.findViewById(R.id.date_TextView);

        sharedPreferences = getContext().getSharedPreferences("AppData", Context.MODE_PRIVATE); // 0 - for private mode

        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    dateTextView.setVisibility(View.VISIBLE);
                                    dateTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    date=dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                    markAsCompletedOrUncompleted(true);
                                }
                            }, year, month, day);
                    picker.setTitle("Data usterki");
                    picker.show();
                    dateTextView.setError(null);
            }
        });


        return view;
    }

    public String getDate() {
        return date;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        
        boolean isDateValid = !dateTextView.getText().toString().matches("");
        Log.d("isDateValid",String.valueOf(isDateValid));

        return new IsDataValid(isDateValid);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        String date = dateTextView.getText().toString();
        return date != null ? dateTextView.getText().toString() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String userName = getStepData();
        return !userName.isEmpty() ? userName : "(Empty)";
    }


    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
        hideKeyboardFrom(getContext(),view);

        dateTextView.setText(sharedPreferences.getString("Date",""));
        date = sharedPreferences.getString("Date","");


        if(date!=""){
            dateTextView.setVisibility(View.VISIBLE);
            markAsCompletedOrUncompleted(true);
        }


    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.


    }

    public void saveStepData()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Date", getDate());
        editor.commit(); // commit changes
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
        dateTextView.setText(stepData);
    }



}