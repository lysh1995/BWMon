package edu.illinois.ncsa.bwmon;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public static ArrayList<PlaceholderFragment> fragmentList = new ArrayList<PlaceholderFragment>();
    int count = 0;
    String[] titles;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return count;
    }

    public void setCount(int count){
        this.count = count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position < count)
        {
            return titles[position];
        }
        else return null;
    }

    public void setPageTitle(String[] titles)
    {
        this.titles = new String[titles.length];
        for (int i=0; i < titles.length; i++)
        {
            this.fragmentList.add(PlaceholderFragment.newInstance(i));
            this.titles[i] = titles[i];
        }
    }
}
