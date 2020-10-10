package org.konata.udpsender.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.tabs.TabLayout;

import org.konata.udpsender.R;

public class HomeFragment extends Fragment {
    ViewPager2 viewPager;

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HomeTabAdapter homeTabAdapter;
        TabLayout tabLayout = view.findViewById(R.id.tab);
        ViewPager2 viewPager = view.findViewById(R.id.view_page);
        homeTabAdapter = new HomeTabAdapter(this);
        viewPager.setAdapter(homeTabAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0) {
                            tab.setText("UDP");
                        } else if (position == 1) {
                            tab.setText("WoL");
                        }
                    }
                }
        ).attach();
    }
}

