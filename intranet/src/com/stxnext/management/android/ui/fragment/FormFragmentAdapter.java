package com.stxnext.management.android.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.stxnext.management.android.R;
import com.viewpagerindicator.IconPagerAdapter;

public class FormFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    protected String[] contentTitles = new String[] { "Nieobecność", "Poza biurem" };
    private ArrayList<Fragment> fragments;

    public FormFragmentAdapter(Context context,FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        contentTitles = new String[]{context.getString(R.string.label_absence),context.getString(R.string.label_out_of_office)};
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return contentTitles[position % contentTitles.length];
    }

//    @Override
//    public int getIconResId(int index) {
//      return ICONS[index % ICONS.length];
//    }


    @Override
    public int getIconResId(int index) {
        // TODO Auto-generated method stub
        return 0;
    }
}