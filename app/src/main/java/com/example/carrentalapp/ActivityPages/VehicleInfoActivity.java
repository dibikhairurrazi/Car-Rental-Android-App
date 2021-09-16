package com.example.carrentalapp.ActivityPages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.carrentalapp.Database.InsuranceDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Model.Insurance;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.R;
import com.squareup.picasso.Picasso;

import c.e.c.Util.Common;

public class VehicleInfoActivity extends AppCompatActivity {

    //VEHICLE OBJECT
    private Vehicle vehicle;
    //VEHICLE TITLE
    private TextView vehicleTitle;
    //VEHICLE IMAGE OBJECT
    private ImageView vehicleImage;
    //VEHICLE PRICE
    private TextView vehiclePrice;

    //VEHICLE AVAILABILITY FIELD
    private ConstraintLayout available;
    private ConstraintLayout notAvailable;

    //GOING BACK BUTTON
    private Button back;
    private Button book;

    //VEHICLE INFO FIELD
    private TextView year, manufacturer, model, mileage, seats, type;

    //INSURANCE OPTION
    private RadioGroup insuranceOption;

    private String chosenInsurance = "";

    //INSURANCE DATABASE TABLE
    InsuranceDao insuranceDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);

        initComponents();
        listenHandler();
        displayVehicleInfo();



    }

    private void initComponents() {

        //INITIALIZING COMPONENTS
        vehicle = (Vehicle) getIntent().getSerializableExtra("VEHICLE");
        back = findViewById(R.id.back);
        vehicleTitle = findViewById(R.id.vehicleTitle);
        vehicleImage = findViewById(R.id.vehicleImage);

        available = findViewById(R.id.available);
        notAvailable = findViewById(R.id.notAvailable);

        //VEHICLE INFO FIELD
        year = findViewById(R.id.year);
        manufacturer = findViewById(R.id.manufacturer);
        model = findViewById(R.id.model);
        mileage = findViewById(R.id.mileage);
        seats = findViewById(R.id.seats);
        type = findViewById(R.id.type);

        //VEHICLE PRICE
        vehiclePrice = findViewById(R.id.vehiclePrice);

        //INSURANCE OPTION
        insuranceOption = findViewById(R.id.insuranceOption);

        //BOOK BUTTON
        book = findViewById(R.id.book_this_car);

        //INSURANCE DATABASE TABLE
        insuranceDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db")
                .allowMainThreadQueries()
                .build()
                .insuranceDao();
    }

    private void listenHandler() {

        //BACK ARROW BUTTON LISTENER
        back.setOnClickListener(v -> finish());

        //BOOKING BUTTON -> THIS WILL REDIRECT TO BOOKING PAGE
        book.setOnClickListener(v -> {
            Intent informationPage = new Intent(VehicleInfoActivity.this, BookingCarActivity.class);
            informationPage.putExtra("INSURANCEID",getInsuranceID(chosenInsurance));
            informationPage.putExtra("VEHICLEID",vehicle.getVehicleID()+"");
            startActivity(informationPage);
        });


        insuranceOption.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton option = findViewById(checkedId);
            chosenInsurance = option.getText().toString().toLowerCase();
        });


    }

    private String getInsuranceID(String chosenInsurance){
        Insurance insurance = new Insurance(chosenInsurance,-1);
        return insurance.getInsuranceID();
    }

    @SuppressLint("SetTextI18n")
    private void displayVehicleInfo() {
        //SETTING THE TITLE TO VEHICLE NAME
        vehicleTitle.setText(vehicle.fullTitle());
        //LOADING THE VEHICLE IMAGE
        Picasso.get().load(vehicle.getVehicleImageURL()).into(vehicleImage);

        //IF VEHICLE AVAILABLE => DISPLAY AVAILABLE TEXT
        //IF VEHICLE NOT AVAILABLE => DISPLAY NOT AVAILABLE TEXT
        if(vehicle.isAvailability()){
            available.setVisibility(ConstraintLayout.VISIBLE);
            notAvailable.setVisibility(ConstraintLayout.INVISIBLE);
            book.setEnabled(true);
            book.setBackground(ContextCompat.getDrawable(VehicleInfoActivity.this,R.drawable.round_button));
            book.setText(R.string.book_this_car_message);
        }else{
            available.setVisibility(ConstraintLayout.INVISIBLE);
            notAvailable.setVisibility(ConstraintLayout.VISIBLE);
            book.setEnabled(false);
            book.setBackground(ContextCompat.getDrawable(VehicleInfoActivity.this,R.drawable.disable_button));
            book.setText(R.string.car_not_available_message);
        }

        //SET VEHICLE SPECS
        year.setText(vehicle.getYear()+"");
        manufacturer.setText(vehicle.getManufacturer());
        model.setText(vehicle.getModel());
        mileage.setText(vehicle.getMileage()+"");
        seats.setText(vehicle.getSeats()+"");
        type.setText(vehicle.getCategory());

        vehiclePrice.setText(Common.getFormattedPrice(vehicle.getPrice())+"/Hari");

    }
}
