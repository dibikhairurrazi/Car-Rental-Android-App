package com.example.carrentalapp.ActivityPages;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.room.Room;

import com.example.carrentalapp.BuildConfig;
import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Model.Customer;
import com.example.carrentalapp.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import c.e.c.Util.SimpleSHA1;


public class RegistrationActivity extends AppCompatActivity{

    private Button register, takePicture;
    private TextView login;

    private TextView expiryDate;
    private TextView dob;
    private CustomerDao customerDao;

    private String currentPhotoPath;
    private ImageView photoView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_CODE = 1000;
    // private static final int IMAGE_CAPTURE_CODE = 1001;

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

        photoView = findViewById(R.id.photoView);
        takePicture = findViewById(R.id.take_picture_button);

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
            customer.setImagePath(currentPhotoPath);

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

        takePicture.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                //permission not enabled, request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup to request permissions
                requestPermissions(permission, PERMISSION_CODE);
            }
            dispatchTakePictureIntent();
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                toast("error creating file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Bundle extras = data.getExtras();
            // Bitmap imageBitmap = (Bitmap) extras.get("data");
            // photoView.setImageBitmap(imageBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        toast(image.getAbsolutePath());
        return image;
    }

}
