package com.mtanasyuk.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
    @BindView(R.id.tvBeginDate) TextView tvBeginDate;
    @BindView(R.id.sort_order) TextView tvSortOrder;
    @BindView(R.id.news_desk) TextView tvNewsDesk;


    private Unbinder unbinder;
    String dateToSend;

    public SettingsDialogFragment() {}

    public static SettingsDialogFragment newInstance(String title) {
        SettingsDialogFragment frag = new SettingsDialogFragment();
        return frag;
    }

    // Defines the listener interface with a method passing back data result
    public interface SettingsDialogListener {
        void onFinishEditDialog(Filter filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        etSetDate.requestFocus();
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        etSetDate.setOnClickListener(v -> showDatePicker());


        btnSaveFilters.setOnClickListener(v -> {
            String date = etSetDate.getText().toString();
            if (date.isEmpty()) dateToSend = "";
            boolean isArts = checkArts.isChecked();
            boolean isFashion = checkFashion.isChecked();
            boolean isSports = checkSports.isChecked();
            String value = spinner.getSelectedItem().toString();
            Filter filter = new Filter(dateToSend, isArts, isFashion, isSports, value);
            SettingsDialogListener listener = (SettingsDialogListener) getActivity();
            Snackbar.make(view, R.string.applied, Snackbar.LENGTH_INDEFINITE).show();
            listener.onFinishEditDialog(filter);
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
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
        SimpleDateFormat str = new SimpleDateFormat("yyyyMMdd");
        dateToSend = str.format(c.getTime());
        SimpleDateFormat dateToShow = new SimpleDateFormat("dd-MM-yyyy");
        etSetDate.setText(dateToShow.format(c.getTime()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
