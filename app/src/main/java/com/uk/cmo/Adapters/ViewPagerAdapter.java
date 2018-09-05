package com.uk.cmo.Adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uk.cmo.Fragments.Members;
import com.uk.cmo.Fragments.Notifications;
import com.uk.cmo.Fragments.Posts;

/**
 * Created by usman on 22-02-2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Fragment fragment;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                fragment = Posts.getInstance();
                break;
            case 1:
                fragment = Members.getInstance();
                break;
            case 2:
                fragment = Notifications.getInstance();
                break;
            default:
                fragment=null;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title;
        switch (position){
            case 0:
                title = "Posts";
                break;
            case 1:
                title = "Members";
                break;
            case 2:
            default:
                title = null;
                break;
        }
        return title;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        //returning position none makes it possible to reload adapter
        return POSITION_NONE;
    }
}
