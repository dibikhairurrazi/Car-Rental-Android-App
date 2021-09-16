package com.example.carrentalapp.ActivityPages;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Model.Customer;
import com.example.carrentalapp.R;

import java.util.Random;

import c.e.c.Util.SimpleSHA1;


public class RegistrationActivity extends AppCompatActivity{

    private Button register;
    private TextView login;

    private TextView expiryDate;
    private TextView dob;
    private CustomerDao customerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initComponents();

        clickListenHandler();

    }

    //Initialize all the components in Register Page
    private void initComponents(){

        //Register Button
        register = findViewById(R.id.register);
        //Login Button
        login = findViewById(R.id.login);
        //Expiry Button
        expiryDate = findViewById(R.id.expiryDate);
        //Date of Birth Button
        dob = findViewById(R.id.dob);

        //Get the Customer Room (table)
        customerDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .customerDao();

    }

    //This method handles all the click events
    private void clickListenHandler(){

        //Expiry Date Listener
        expiryDate.setOnClickListener(v -> openCalendar(expiryDate));

        //Date of Birth Listener
        dob.setOnClickListener(v -> openCalendar(dob));

        //Login Listener
        login.setOnClickListener(v -> {
            Intent registerPage = new Intent(RegistrationActivity.this,LoginActivity.class);
            startActivity(registerPage);
        });

        //Register Listener
        register.setOnClickListener(v -> {
            Customer customer = createCustomerObject();

            if(customerDao != null) {
                //If customer object is null -> Incomplete form
                //If customer object not null -> Complete form
                if(customer != null) {
                    customerDao.insert(customer); //Insert the customer object into database
                    toast("Registration Successful");
                    finish();
                }
            }
        });
    }

    //Opening a Calendar Dialog
    private void openCalendar(final TextView dateFieldButton) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);

        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            String date = year + "-" + month + "-" + dayOfMonth;
            dateFieldButton.setText(date);
        });

        datePickerDialog.show();
    }

    //Create the customer object from the form
    private Customer createCustomerObject(){

        String firstName = ((EditText)findViewById(R.id.firstName)).getText().toString().toLowerCase();
        String middleName = ((EditText)findViewById(R.id.middleName)).getText().toString().toLowerCase();
        String lastName = ((EditText)findViewById(R.id.lastName)).getText().toString().toLowerCase();

        String email = ((EditText)findViewById(R.id.email)).getText().toString();

        String driverLicense = ((EditText)findViewById(R.id.license)).getText().toString();
        String expiry = expiryDate.getText().toString();
        String dateOfBirth = dob.getText().toString();

        String phoneNumber = ((EditText)findViewById(R.id.phoneNumber)).getText().toString();

        String street = ((EditText)findViewById(R.id.street)).getText().toString();
        String city = ((EditText)findViewById(R.id.city)).getText().toString();
        String postalCode = ((EditText)findViewById(R.id.postalCode)).getText().toString();

        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        String confirm_password = ((EditText)findViewById(R.id.confirmPassword)).getText().toString();

        boolean success = fieldRequiredCheck(firstName,lastName,email,driverLicense,expiry,dateOfBirth,phoneNumber,street,city,postalCode);

        int id = generateID();

        while(customerDao.exist(id)){
            id = generateID();
        }
        //This is to check if all fields are entered
        if(success){

            //Password and Confirm Password Check
            if(password.length() > 0 && password.equals(confirm_password)) {
                String pw = "";
                try{
                    pw = SimpleSHA1.SHA1(password);
                } catch (Exception e) {
                    toast("Internal Server Error");
                }

                return new Customer(id,firstName, middleName, lastName,
                        email, driverLicense, expiry,
                        dateOfBirth, phoneNumber, street,
                        city, postalCode, pw
                );
            }else{
                toast("Password do not match");
            }
        }else{
            toast("Incomplete Form");
        }

        return null;
    }

    private boolean fieldRequiredCheck(String firstName,String lastName, String email, String driverLicense, String expiry, String dateOfBirth, String phoneNumber, String street, String city, String postalCode) {
        return  !firstName.equals("") && !lastName.equals("") &&
                !email.equals("") && !driverLicense.equals("") && !expiry.equals("") &&
                !dateOfBirth.equals("") && !phoneNumber.equals("") && !street.equals("") &&
                !city.equals("") && !postalCode.equals("");
    }


    //DEBUGGING
    private void toast(String txt){
        Toast toast = Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT);
        toast.show();
    }

    private int generateID(){
        Random rnd = new Random();
        return 202000 + rnd.nextInt(65)+10;
    }





}
