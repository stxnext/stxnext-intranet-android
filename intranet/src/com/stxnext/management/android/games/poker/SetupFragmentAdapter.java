package com.stxnext.management.android.games.poker;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class SetupFragmentAdapter  extends FragmentPagerAdapter implements IconPagerAdapter {
    private ArrayList<Fragment> fragments;

    FragmentManager manager;
    public SetupFragmentAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<Fragment>();
        this.manager = fm;
    }
    
    public void addFragment(Fragment fragment){
        this.fragments.add(fragment);
        this.notifyDataSetChanged();
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
    public int getIconResId(int index) {
        return 0;
    }
}