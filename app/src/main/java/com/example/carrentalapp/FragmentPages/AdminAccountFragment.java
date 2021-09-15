package com.example.carrentalapp.FragmentPages;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.carrentalapp.ActivityPages.AddVehicleActivity;
import com.example.carrentalapp.ActivityPages.AddVehicleCategoryActivity;
import com.example.carrentalapp.ActivityPages.LoginActivity;
import com.example.carrentalapp.R;
import com.example.carrentalapp.Session.Session;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminAccountFragment extends Fragment {

    private Button logout;
    private Button addVehicle;
    private Button addVehicleCategory;

    public AdminAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_admin, container, false);
        initComponents(view);
        listenHandler();
        return view;
    }

    private void listenHandler() {

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session.close(getContext());
                Intent loginPage = new Intent(getActivity(), LoginActivity.class);
                loginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginPage);
            }
        });

        addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addVehicle = new Intent(getActivity(), AddVehicleActivity.class);
                addVehicle.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(addVehicle);
            }
        });

        addVehicleCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCategory = new Intent(getActivity(), AddVehicleCategoryActivity.class);
                addCategory.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(addCategory);
            }
        });

    }

    private void initComponents(View view) {
        logout = view.findViewById(R.id.logout);
        addVehicle = view.findViewById(R.id.add_vehicle_button);
        addVehicleCategory = view.findViewById(R.id.add_vehicle_category_button);
    }

}
