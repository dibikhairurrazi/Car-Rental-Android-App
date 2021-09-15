package com.example.carrentalapp.ActivityPages;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.carrentalapp.FragmentPages.AccountFragment;
import com.example.carrentalapp.FragmentPages.AdminAccountFragment;
import com.example.carrentalapp.FragmentPages.AdminBookingFragment;
import com.example.carrentalapp.FragmentPages.BookingFragment;
import com.example.carrentalapp.FragmentPages.VehicleCategoryFragment;
import com.example.carrentalapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminViewActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private AdminBookingFragment bookingFragment;
    private AdminAccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        initComponents();
        setFragment(bookingFragment);

        clickListener();

    }

    private void clickListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_booking:
                        setFragment(bookingFragment);
                        return true;

                    case R.id.nav_account :
                        setFragment(accountFragment);
                        return true;
                }

                return false;
            }
        });
    }

    private void setFragment(Fragment fragment,String Data) {
        Bundle bundle = new Bundle();
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
        bottomNavigationView = findViewById(R.id.bottom_nav_admin);
        frameLayout = findViewById(R.id.framelayout);

        bookingFragment= new AdminBookingFragment();
        accountFragment = new AdminAccountFragment();

    }
}
