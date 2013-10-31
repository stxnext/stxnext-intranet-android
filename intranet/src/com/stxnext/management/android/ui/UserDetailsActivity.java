package com.stxnext.management.android.ui;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.UserProperty;
import com.stxnext.management.android.ui.controls.RoundedImageView;
import com.stxnext.management.android.ui.dependencies.AsyncTaskEx;
import com.stxnext.management.android.ui.dependencies.BitmapUtils;
import com.stxnext.management.android.ui.dependencies.PropertyListAdapter;

public class UserDetailsActivity extends AbstractSimpleActivity{

    public static final String EXTRA_USER = "user";
    
    RoundedImageView userImageView;
    TextView nameView;
    ListView listView;
    PropertyListAdapter adapter;
    
    ViewGroup loadingView;
    ViewGroup loadedView;
    
    IntranetUser user;
    
    @Override
    protected void fillViews() {
        userImageView = (RoundedImageView) findViewById(R.id.userImageView);
        nameView = (TextView) findViewById(R.id.nameView);
        listView = (ListView) findViewById(R.id.listView);
        loadingView = (ViewGroup) findViewById(R.id.loadingView);
        loadedView = (ViewGroup) findViewById(R.id.loadedView);
        userImageView.setCornersRadius(12F);
        
        Bundle bundle = getIntent().getExtras();
        user = (IntranetUser) bundle.getSerializable(EXTRA_USER);
        
        nameView.setText(user.getName());
        new LoadDataTask().execute();
    }

    @Override
    protected void setActions() {
        
    }

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_user_details;
    }
    
    private void setViewLoading(boolean loading){
        loadingView.setVisibility(loading?View.VISIBLE:View.GONE);
        loadedView.setVisibility(loading?View.GONE:View.VISIBLE);
    }
    
    private class LoadDataTask extends AsyncTaskEx<Void, Void, List<UserProperty>>{

        Bitmap bmp;
        
        @Override
        protected void onPreExecute() {
            setViewLoading(true);
            super.onPreExecute();
        }
        
        @Override
        protected List<UserProperty> doInBackground(Void... params) {
            bmp = BitmapUtils.getTempBitmap(UserDetailsActivity.this, user.getId().toString());
            return user.getProperties();
        }
        
        @Override
        protected void onPostExecute(List<UserProperty> result) {
            super.onPostExecute(result);
            if(isFinishing())
                return;
            
            if(bmp!=null){
                userImageView.setImageBitmap(bmp);
            }
            adapter = new PropertyListAdapter(UserDetailsActivity.this, listView, result);
            listView.setAdapter(adapter);
            setViewLoading(false);
            applyListAnimation(listView);
        }
        
    }

}
