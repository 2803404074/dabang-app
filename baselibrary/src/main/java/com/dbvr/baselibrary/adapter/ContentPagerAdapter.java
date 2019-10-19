package com.dbvr.baselibrary.adapter;

import java.util.ArrayList;
import java.util.List;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public  class ContentPagerAdapter extends FragmentPagerAdapter {

    private List<String>mTitles;
    private ArrayList<Fragment> mFragments;

    public ContentPagerAdapter(FragmentManager fm, List<String> mTitles, ArrayList<Fragment> fragments) {
        super(fm);
        this.mTitles = mTitles;
        this.mFragments=fragments;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    }