package com.stxnext.management.android.ui.fragment;

import com.actionbarsherlock.ActionBarSherlock;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.web.api.IntranetApi;

import android.support.v4.app.Fragment;

public class LatenessFormFragment extends Fragment{

    
    StoragePrefs prefs;
    IntranetApi api;
    ActionBarSherlock sherlock;
    
    
    public void setFormEnabled(boolean enabled){
        
    }

    public void setSherlock(ActionBarSherlock sherlock) {
        this.sherlock = sherlock;
    }
}
