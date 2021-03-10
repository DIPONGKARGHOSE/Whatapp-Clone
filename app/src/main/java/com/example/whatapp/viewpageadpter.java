package com.example.whatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class viewpageadpter extends FragmentPagerAdapter {

    public viewpageadpter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new chat();
            case 1:return  new status();
            case 2:return  new calls();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case  0: return "CHAT";
            case 1: return  "status";
            case 2:return  "CALLS";
        }
        return null;
    }
}
