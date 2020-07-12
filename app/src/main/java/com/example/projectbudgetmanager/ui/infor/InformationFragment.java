package com.example.projectbudgetmanager.ui.infor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projectbudgetmanager.R;

public class InformationFragment extends Fragment {

    private InformationViewModel informationViewModel;
    androidx.appcompat.widget.Toolbar information_fragment_toolbar;

    public static InformationFragment newInstance() {
        return new InformationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        informationViewModel = ViewModelProviders.of(this).get(InformationViewModel.class);
        //Read file .xml from View
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        informationViewModel = ViewModelProviders.of(this).get(InformationViewModel.class);
        // TODO: Use the ViewModel

//        information_fragment_toolbar= getView().findViewById(R.id.information_fragment_toolbar);
//        information_fragment_toolbar.setTitle("Information of Project");
//        ((AppCompatActivity)getActivity()).setSupportActionBar(information_fragment_toolbar);
//        //Display back button (Back arrow  <- )
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

//        //Back arrow to return the before fragment
//        information_fragment_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });
    }
}
