package com.example.sagar.chatapp;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.sagar.chatapp.fragments.ChatFragment;
import com.example.sagar.chatapp.fragments.FriendFragment;
import com.example.sagar.chatapp.fragments.RequestFragment;

/**
 * Created by SAGAR on 16-Jan-18.
 */

class TabPagerAdapter extends FragmentPagerAdapter{
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                RequestFragment requestFragment=new RequestFragment();
                return requestFragment;
            case 1:
                ChatFragment chatFragment=new ChatFragment();
                return chatFragment;
            case 2:
                FriendFragment friendFragment=new FriendFragment();
                return friendFragment;
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position){
            case 0:
                return "Requests";
            case 1:
                return "Chats";
            case 2:
                return "Friends";
            default:
                return null;


        }

    }
}
