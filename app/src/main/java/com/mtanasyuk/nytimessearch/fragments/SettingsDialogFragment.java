package com.mtanasyuk.nytimessearch.fragments;

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
import com.mtanasyuk.nytimessearch.models.Filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.pick_date) EditText etSetDate;
    @BindView(R.id.mySpinner) Spinner spinner;
    @BindView(R.id.checkbox_arts) CheckBox checkArts;
    @BindView(R.id.checkbox_fashion) CheckBox checkFashion;
    @BindView(R.id.checkbox_sports) CheckBox checkSports;
    @BindView(R.id.btnSaveFilters) Button btnSaveFilters;
    @BindView(R.id.btnCancel) Button btnCancel;
    private Unbinder unbinder;

    public SettingsDialogFragment() {}

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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        etSetDate.requestFocus();
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        etSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });


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
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
