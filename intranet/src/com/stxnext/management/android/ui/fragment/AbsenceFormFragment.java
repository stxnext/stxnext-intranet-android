package com.stxnext.management.android.ui.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.ActionBarSherlock;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.MandatedTime;
import com.stxnext.management.android.dto.postmessage.AbstractMessage;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.ui.dependencies.AsyncTaskEx;
import com.stxnext.management.android.ui.dependencies.Popup;
import com.stxnext.management.android.ui.dependencies.PopupItem;
import com.stxnext.management.android.web.api.HTTPResponse;
import com.stxnext.management.android.web.api.IntranetApi;

public class AbsenceFormFragment  extends Fragment implements CalendarDatePickerDialog.OnDateSetListener {

    private static final String START_PICKER_TAG = "startPicker";
    private static final String END_PICKER_TAG = "endPicker";
    
    private TableRow startDateRow;
    private TableRow endDateRow;
    private TableRow typeRow;
    
    private EditText absenceExplanationView;
    private TextView absenceTypeView;
    private TextView absenceEndDateView;
    private TextView absenceStartDateView;
    private TextView daysLeftView;
    
    private View view;
    private Popup typePopup;
    
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    
    CalendarDatePickerDialog startDatePickerDialog;
    CalendarDatePickerDialog endDatePickerDialog;
    
    StoragePrefs prefs;
    IntranetApi api;
    ActionBarSherlock sherlock;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        prefs = StoragePrefs.getInstance(getActivity());
        api = IntranetApi.getInstance(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_absence, container,
                false);
        
        startDateRow = (TableRow) view.findViewById(R.id.startDateRow);
        endDateRow = (TableRow) view.findViewById(R.id.endDateRow);
        typeRow = (TableRow) view.findViewById(R.id.typeRow);
        absenceExplanationView = (EditText) view.findViewById(R.id.absenceExplanationView);
        absenceTypeView = (TextView) view.findViewById(R.id.absenceTypeView);
        absenceEndDateView = (TextView) view.findViewById(R.id.absenceEndDateView);
        absenceStartDateView = (TextView) view.findViewById(R.id.absenceStartDateView);
        daysLeftView = (TextView) view.findViewById(R.id.daysLeftView);
        
        startDatePickerDialog = CalendarDatePickerDialog
                .newInstance(AbsenceFormFragment.this, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH)-1,
                        startDate.get(Calendar.DAY_OF_MONTH));
        endDatePickerDialog = CalendarDatePickerDialog
                .newInstance(AbsenceFormFragment.this, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH)-1,
                        endDate.get(Calendar.DAY_OF_MONTH));
        
        absenceStartDateView.setText(AbstractMessage.defaultDateFormat.format(startDate.getTime()));
        absenceEndDateView.setText(AbstractMessage.defaultDateFormat.format(endDate.getTime()));
        Integer daysOff = prefs.getDaysOffToTake();
        if(daysOff != null){
            daysLeftView.setText(String.valueOf(daysOff));
        }
        
        configureTypeSelector();
        setActions();
        new UpdateTimeLeft().execute();
        return view;
    }
    
    private void configureTypeSelector(){
        List<PopupItem> items = new ArrayList<PopupItem>();
        typePopup = new Popup(getActivity(), absenceTypeView);
        //typePopup.addItems(item1,item2);
    }

    private class UpdateTimeLeft extends AsyncTaskEx<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            setLoading(true, false);
        };
        
        Integer daysLeft;
        @Override
        protected Void doInBackground(Void... params) {
            HTTPResponse<MandatedTime> response = api.getDaysOffToTake();
            if(response.ok() && response.getExpectedResponse() != null){
                daysLeft = response.getExpectedResponse().getLeft().intValue();
                prefs.setDaysOffToTake(daysLeft);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(getActivity()!=null && !getActivity().isFinishing()){
                daysLeftView.setText(String.valueOf(daysLeft));
                setLoading(false, false);
            }
        }
    }
    
    private void setActions(){
        startDateRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                startDatePickerDialog.show(fm,START_PICKER_TAG);
            }
        });
        
        endDateRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                endDatePickerDialog.show(fm,END_PICKER_TAG);
            }
        });
    }
    
    private String updateCalendarAndGetFormat(Calendar cal, int year, int monthOfYear, int dayOfMonth ){
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return AbstractMessage.defaultDateFormat.format(cal.getTime());
    }
    
    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        
        if(dialog.getTag().equals(START_PICKER_TAG)){
            absenceStartDateView.setText(updateCalendarAndGetFormat(startDate, year, monthOfYear, dayOfMonth));
        }
        else if(dialog.getTag().equals(END_PICKER_TAG)){
            absenceEndDateView.setText(updateCalendarAndGetFormat(endDate, year, monthOfYear, dayOfMonth));
        }
        
    }
    
    private void setLoading(boolean loading, boolean affectForm){
        if(sherlock != null){
            sherlock.setProgressBarIndeterminateVisibility(loading);
        }
    }
    
    public void setFormEnabled(boolean enabled){
        startDateRow.setEnabled(enabled);
        endDateRow.setEnabled(enabled);
        typeRow.setEnabled(enabled);
        absenceExplanationView.setEnabled(enabled);
    }

    public void setSherlock(ActionBarSherlock sherlock) {
        this.sherlock = sherlock;
    }
    
}