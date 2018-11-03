package com.android.wondercom.BLUETOOTH.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> listadoFragments = new ArrayList<>();
    private final List<String> listadoTitulosFragments = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){
        listadoFragments.add(fragment);
        listadoTitulosFragments.add(title);
    }

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return listadoFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listadoTitulosFragments.get(position);
    }

    @Override
    public int getCount() {
        return listadoFragments.size();
    }

}
