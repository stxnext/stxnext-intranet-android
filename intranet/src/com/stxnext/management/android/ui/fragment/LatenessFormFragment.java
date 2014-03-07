package com.stxnext.management.android.ui.fragment;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;

import com.actionbarsherlock.ActionBarSherlock;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog.OnDateChangedListener;
import com.doomonafireball.betterpickers.calendardatepicker.SimpleMonthAdapter.CalendarDay;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog.OnTimeSetListener;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.postmessage.AbstractMessage;
import com.stxnext.management.android.dto.postmessage.LatenessMessage;
import com.stxnext.management.android.dto.postmessage.LatenessPayload;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.ui.dependencies.TimeUtil;
import com.stxnext.management.android.ui.dependencies.TouchResistantEditText;
import com.stxnext.management.android.web.api.IntranetApi;

public class LatenessFormFragment   extends Fragment {

    private static final String DATE_PICKER_TAG = "datePicker";
    private static final String START_TIME_PICKER_TAG = "startTimePicker";
    private static final String END_TIME_PICKER_TAG = "endTimePicker";
    
    private final static int initial_start_hour = 9;
    private final static int initial_minute = 0;
    private final static int initial_end_hour = 17;
    
    private TableRow dateRow;
    private TableRow startTimeRow;
    private TableRow endTimeRow;
    private TableRow workFromHomeRow;
    
    private EditText oooExplanationView;
    private TouchResistantEditText oooDateView;
    private TouchResistantEditText startTimeView;
    private TouchResistantEditText endTimeView;
    private ImageView workFromHomeCheck;
    private Button oooSubmitButton;
    
    private View view;
    
    Calendar submitDate = Calendar.getInstance();
    Calendar startTime = Calendar.getInstance();
    Calendar endTime = Calendar.getInstance();
    
    CalendarDatePickerDialog datePickerDialog;
    RadialTimePickerDialog startTimePicker;
    RadialTimePickerDialog endTimePicker;
    
    StoragePrefs prefs;
    IntranetApi api;
    ActionBarSherlock sherlock;
    Integer daysLeft;
    FormActionReceiver formReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        prefs = StoragePrefs.getInstance(getActivity());
        api = IntranetApi.getInstance(getActivity().getApplication());
        formReceiver = (FormActionReceiver) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_outofoffice, container,
                false);
        
        dateRow = (TableRow) view.findViewById(R.id.dateRow);
        startTimeRow = (TableRow) view.findViewById(R.id.startTimeRow);
        endTimeRow = (TableRow) view.findViewById(R.id.endTimeRow);
        workFromHomeRow = (TableRow) view.findViewById(R.id.workFromHomeRow);
        
        oooExplanationView = (EditText) view.findViewById(R.id.oooExplanationView);
        oooDateView = (TouchResistantEditText) view.findViewById(R.id.oooDateView);
        startTimeView = (TouchResistantEditText) view.findViewById(R.id.startTimeView);
        endTimeView = (TouchResistantEditText) view.findViewById(R.id.endTimeView);
        workFromHomeCheck = (ImageView) view.findViewById(R.id.workFromHomeCheck);
        oooSubmitButton = (Button) view.findViewById(R.id.oooSubmitButton);
        
        setupTimeAndPickers();
        
        oooDateView.setText(TimeUtil.defaultDateFormat.format(submitDate.getTime()));
        setWorkFromHomeSelected(false);
        setActions();
        return view;
    }
    
    private void setupTimeAndPickers(){
        datePickerDialog = CalendarDatePickerDialog
                .newInstance(null, submitDate.get(Calendar.YEAR), submitDate.get(Calendar.MONTH),
                        submitDate.get(Calendar.DAY_OF_MONTH));
        startTime.set(Calendar.HOUR_OF_DAY, initial_start_hour);
        startTime.set(Calendar.MINUTE, initial_minute);
        endTime.set(Calendar.HOUR_OF_DAY, initial_end_hour);
        endTime.set(Calendar.MINUTE, initial_minute);
        
        startTimePicker = RadialTimePickerDialog
                .newInstance(null,initial_start_hour, initial_minute,
                        true);
        endTimePicker = RadialTimePickerDialog
                .newInstance(null, initial_end_hour, initial_minute,
                        true);
        
        startTimeView.setText(TimeUtil.updateCalendarTimeAndGetFormat(startTime, initial_start_hour, initial_minute));
        endTimeView.setText(TimeUtil.updateCalendarTimeAndGetFormat(endTime, initial_end_hour, initial_minute));
        
        startTimePicker.setOnTimeSetListener(new OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                startTimeView.setText(TimeUtil.updateCalendarTimeAndGetFormat(startTime, hourOfDay, minute));
            }
        });
        
        endTimePicker.setOnTimeSetListener(new OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                endTimeView.setText(TimeUtil.updateCalendarTimeAndGetFormat(endTime, hourOfDay, minute));
            }
        });
        
        datePickerDialog.registerOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged() {
               CalendarDay day = datePickerDialog.getSelectedDay();
               oooDateView.setText(TimeUtil.updateCalendarAndGetFormat(submitDate, day.getYear(), day.getMonth(), day.getDay()));
               datePickerDialog.dismiss();
            }
        });
    }
    
    private void setActions(){
        dateRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                datePickerDialog.show(fm,DATE_PICKER_TAG);
            }
        });
        
        startTimeRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                startTimePicker.setStartTime(startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE));
                startTimePicker.show(fm, START_TIME_PICKER_TAG);
            }
        });
        
        endTimeRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                endTimePicker.setStartTime(endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE));
                endTimePicker.show(fm, END_TIME_PICKER_TAG);
            }
        });
        
        workFromHomeRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWorkFromHomeSelected(!(Boolean) workFromHomeCheck.getTag());
            }
        });
        
        oooSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    LatenessMessage message = new LatenessMessage();
                    message.setSubmissionDate(submitDate.getTime());
                    message.setStartHour(startTime.getTime());
                    message.setEndHour(endTime.getTime());
                    message.setExplanation(oooExplanationView.getText().toString().trim());
                    message.setWorkFromHome((Boolean) workFromHomeCheck.getTag());
                    formReceiver.onSubmitFormWithMessage(new LatenessPayload(message));
                }
            }
        });
    }
    
    private boolean validateForm(){
        if(startTime.after(endTime)){
            startTimeView.setError(getString(R.string.validation_start_time_after_end_time));
            startTimeView.requestFocus();
            return false;
        }
        startTimeView.setError(null);
        return true;
    }
    
    //should prepare checkbox control
    private void setWorkFromHomeSelected(boolean selected){
        workFromHomeCheck.setTag(selected);
        workFromHomeCheck.setImageLevel(selected?1:0);
    }
    
    
    public void setFormEnabled(boolean enabled){
        dateRow.setEnabled(enabled);
        startTimeRow.setEnabled(enabled);
        endTimeRow.setEnabled(enabled);
        workFromHomeRow.setEnabled(enabled);
        oooSubmitButton.setEnabled(enabled);
    }

    public void setSherlock(ActionBarSherlock sherlock) {
        this.sherlock = sherlock;
    }

    
}