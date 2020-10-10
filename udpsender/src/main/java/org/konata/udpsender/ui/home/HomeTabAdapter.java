package org.konata.udpsender.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomeTabAdapter extends FragmentStateAdapter {
    public HomeTabAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            // Return a NEW fragment instance in createFragment(int)
            Fragment fragment = new UDPPacketFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        } else {
            // Return a NEW fragment instance in createFragment(int)
            Fragment fragment = new WakeOnLanFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
