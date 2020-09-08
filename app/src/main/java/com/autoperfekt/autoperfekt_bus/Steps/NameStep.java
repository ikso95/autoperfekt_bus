package com.autoperfekt.autoperfekt_bus.Steps;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.autoperfekt.autoperfekt_bus.R;

import ernestoyaquello.com.verticalstepperform.Step;

public class NameStep extends Step<String> {

    private EditText userNameEditText;
    private LayoutInflater inflater;
    private View view;
    private String userName;
    private SharedPreferences sharedPreferences;

    public NameStep(String stepTitle) {
        super(stepTitle);
    }


    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.step_name, null);

        userNameEditText = view.findViewById(R.id.name_EditText);

        sharedPreferences = getContext().getSharedPreferences("AppData", Context.MODE_PRIVATE); // 0 - for private mode

        userNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Whenever the user updates the user name text, we update the state of the step.
                // The step will be marked as completed only if its data is valid, which will be
                // checked automatically by the form with a call to isStepDataValid().
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                userName= userNameEditText.getText().toString().trim();
            }
        });


        return view;
    }


    public String getUserName() {
        return userName;
    }


    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        boolean isNameValid = stepData.length() > 0;
        return new IsDataValid(isNameValid);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        Editable userName = userNameEditText.getText();
        return userName != null ? userName.toString() : "";
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

        // simulate a click, which consists of ACTION_DOWN and ACTION_UP
        MotionEvent eventDown = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
        userNameEditText.dispatchTouchEvent(eventDown);
        eventDown.recycle();

        MotionEvent eventUp = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0);
        userNameEditText.dispatchTouchEvent(eventUp);
        eventUp.recycle();

        // To be on the safe side, also use another method
        userNameEditText.setFocusableInTouchMode(true);
        userNameEditText.requestFocus();
        userNameEditText.requestFocusFromTouch();

        userNameEditText.setText(sharedPreferences.getString("Name",""));

    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", getUserName());
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
        userNameEditText.setText(stepData);
    }
}