package com.stxnext.management.android.ui.fragment;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class FormFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    protected static final String[] CONTENT = new String[] { "Absence", "Out Of Office" };
    private ArrayList<Fragment> fragments;

    public FormFragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
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
      return FormFragmentAdapter.CONTENT[position % CONTENT.length];
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