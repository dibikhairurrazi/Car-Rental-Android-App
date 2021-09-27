package com.example.carrentalapp.ActivityPages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.carrentalapp.Database.BookingDao;
import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.InsuranceDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.Booking;
import com.example.carrentalapp.Model.Customer;
import com.example.carrentalapp.Model.Insurance;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.R;

import java.io.File;

import c.e.c.Util.Common;

public class AdminViewBookingActivity extends AppCompatActivity {

    private Button back, approve;

    //DRIVER DETAILS
    private TextView name, email, phoneNumber;

    // private ImageView customerPhoto;

    //BOOKING SUMMARY
    private TextView bookingID, vehicleName, rate, totalDays, _pickup, _return, insurance, insuranceRate, totalCost;

    private CustomerDao customerDao;

    //BOOKING
    private Booking booking;
    //INSURANCE
    private Insurance chosenInsurance;
    //VEHICLE
    private Vehicle vehicle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_booking);

        initComponents();
        listenHandler();
        displayCustomerInformation();
        displaySummary();
        displayTotalCost();
    }

    private void initComponents() {
        back = findViewById(R.id.back);
        approve = findViewById(R.id.approve);

        //DRIVER DETAILS
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);

        // customerPhoto = findViewById(R.id.customerPhoto);

        //BOOKING SUMMARY
        vehicleName = findViewById(R.id.vehicleName);
        rate = findViewById(R.id.rate);
        totalDays = findViewById(R.id.totalDays);
        _pickup = findViewById(R.id.pickup);
        _return = findViewById(R.id.dropoff);

        //INSURANCE TYPE
        insurance = findViewById(R.id.insurance);
        insuranceRate = findViewById(R.id.insuranceRate);

        //TOTAL COST
        totalCost = findViewById(R.id.totalCost);

        //DATABASE TABLE
        //DATABASE TABLE
        BookingDao bookingDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .bookingDao();
        customerDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .customerDao();
        VehicleDao vehicleDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .vehicleDao();
        InsuranceDao insuranceDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .insuranceDao();

        //GET BOOKING OBJECT WHICH WAS PASSED FROM PREVIOUS PAGE
        int _bookingID = Integer.parseInt(getIntent().getStringExtra("BOOKINGID"));
        booking = bookingDao.findBooking(_bookingID);
        chosenInsurance = insuranceDao.findInsurance(booking.getInsuranceID());
        vehicle = vehicleDao.findVehicle(booking.getVehicleID());

        bookingID = findViewById(R.id.bookingID);
    }

    private void listenHandler() {
        back.setOnClickListener(v -> finish());

        approve.setOnClickListener(view -> {
            approveBooking();
            Intent viewBooking = new Intent(AdminViewBookingActivity.this, AdminViewActivity.class);
            startActivity(viewBooking);
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayCustomerInformation() {
        Customer customer = customerDao.findUser(booking.getCustomerID());
        //DISPLAY DRIVER INFO
        name.setText(customer.getFullName());
        email.setText(customer.getEmail());
        phoneNumber.setText(customer.getPhoneNumber());

        /* File imgFile = new File(customer.getImagePath());

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            customerPhoto.setImageBitmap(myBitmap);
        } */

        bookingID.setText("BookingID: " + booking.getBookingID());
    }

    @SuppressLint("SetTextI18n")
    private void displaySummary(){

        vehicleName.setText(vehicle.fullTitle());
        rate.setText("Rp "+vehicle.getPrice()+"/Day");
        totalDays.setText(Common.getDayDifference(booking.getPickupDate(),booking.getReturnDate())+" Days");
        _pickup.setText(booking.getPickupTime());
        _return.setText(booking.getReturnTime());

        insurance.setText(chosenInsurance.getCoverageType());
        insuranceRate.setText(Common.getFormattedPrice(chosenInsurance.getCost()));
    }

    private void displayTotalCost(){
        double cost = calculateTotalCost();
        totalCost.setText(Common.getFormattedPrice(cost));
    }

    private double calculateTotalCost(){
        long _days = Common.getDayDifference(booking.getPickupDate(),booking.getReturnDate());
        double _vehicleRate = vehicle.getPrice();
        double _insuranceRate = chosenInsurance.getCost();

        return (_days*_vehicleRate) + _insuranceRate;
    }

    private void approveBooking() {
        BookingDao bookingDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .bookingDao();
        int _bookingID = Integer.parseInt(getIntent().getStringExtra("BOOKINGID"));
        bookingDao.updateStatus(_bookingID, "approved");
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent homepage = new Intent(getApplicationContext(), UserViewActivity.class);
        homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Will clear out your activity history stack till now
        startActivity(homepage);
    }
}
