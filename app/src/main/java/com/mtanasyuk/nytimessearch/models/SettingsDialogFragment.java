package com.mtanasyuk.nytimessearch.models;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.mtanasyuk.nytimessearch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText etSetDate;
    Button btnSaveFilters;
    Spinner spinner;
    CheckBox checkArts;
    CheckBox checkFashion;
    CheckBox checkSports;

    public SettingsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SettingsDialogFragment newInstance(String title) {
        SettingsDialogFragment frag = new SettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    // Defines the listener interface with a method passing back data result
    public interface SettingsDialogListener {
        void onFinishEditDialog(Filter filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etSetDate = (EditText) view.findViewById(R.id.pick_date);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        etSetDate.requestFocus();
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

//        btnDatePicker = (Button) view.findViewById(R.id.btnDatePicker);
        etSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        spinner = (Spinner) view.findViewById(R.id.mySpinner);
        checkArts = (CheckBox) view.findViewById(R.id.checkbox_arts);
        checkFashion = (CheckBox) view.findViewById(R.id.checkbox_fashion);
        checkSports = (CheckBox) view.findViewById(R.id.checkbox_sports);
        btnSaveFilters = (Button) view.findViewById(R.id.btnSaveFilters);
        btnSaveFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = etSetDate.getText().toString();
                String value = spinner.getSelectedItem().toString();
                boolean isArts = checkArts.isChecked();
                boolean isFashion = checkFashion.isChecked();
                boolean isSports = checkSports.isChecked();
                Filter filter = new Filter(date, isArts, isFashion, isSports, value);
                SettingsDialogListener listener = (SettingsDialogListener) getActivity();
                listener.onFinishEditDialog(filter);
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });
    }

    // attach to an onclick handler to show the date picker
    private void showDatePicker() {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        // SETS the target fragment for use later when sending results
        datePickerFragment.setTargetFragment(SettingsDialogFragment.this, 300);
        datePickerFragment.show(fm, "fragment_date_picker");
    }

    // handle the date selected
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formatted = format.format(c.getTime());
        etSetDate.setText(formatted);
    }
}
