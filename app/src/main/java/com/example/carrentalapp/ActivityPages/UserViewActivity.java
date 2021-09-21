package com.example.carrentalapp.ActivityPages;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.carrentalapp.FragmentPages.AccountFragment;
import com.example.carrentalapp.FragmentPages.BookingFragment;
import com.example.carrentalapp.FragmentPages.VehicleCategoryFragment;
import com.example.carrentalapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserViewActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private VehicleCategoryFragment vehicleCategoryFragment;
    private BookingFragment bookingFragment;
    private AccountFragment accountFragment;

    private String loggedInCustomerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        initComponents();
        setFragment(vehicleCategoryFragment);

        clickListener();

    }

    @SuppressLint("NonConstantResourceId")
    private void clickListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()){

                case R.id.nav_vehicle:
                    setFragment(vehicleCategoryFragment);
                    return true;

                case R.id.nav_booking:
                    setFragment(bookingFragment, loggedInCustomerID);
                    return true;

                case R.id.nav_account :
                    setFragment(accountFragment, loggedInCustomerID);
                    return true;
            }

            return false;
        });
    }

    private void setFragment(Fragment fragment,String Data) {
        Bundle bundle = new Bundle();
        bundle.putString("CUSTOMERID",Data);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,fragment);
        fragmentTransaction.commit();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,fragment);
        fragmentTransaction.commit();
    }

    private void initComponents(){
        bottomNavigationView = findViewById(R.id.bottom_nav);
        findViewById(R.id.framelayout);

        vehicleCategoryFragment = new VehicleCategoryFragment();
        bookingFragment= new BookingFragment();
        accountFragment = new AccountFragment();

        loggedInCustomerID = getIntent().getStringExtra("CUSTOMERID");

    }
}
