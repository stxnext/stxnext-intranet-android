package com.stxnext.management.android.games.poker;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class SetupFragmentAdapter  extends FragmentPagerAdapter implements IconPagerAdapter {
    protected String[] contentTitles;
    private ArrayList<Fragment> fragments;

    public SetupFragmentAdapter(Context context,FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        contentTitles = new String[]{"1","2"};
    }
    
    public ArrayList<Fragment> getFragments() {
        return fragments;
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

    @Override
    public int getIconResId(int index) {
        return 0;
    }
}