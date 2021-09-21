package com.example.carrentalapp.ActivityPages;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.example.carrentalapp.Database.BillingDao;
import com.example.carrentalapp.Database.BookingDao;
import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.InsuranceDao;
import com.example.carrentalapp.Database.PaymentDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.Billing;
import com.example.carrentalapp.Model.Booking;
import com.example.carrentalapp.Model.Customer;
import com.example.carrentalapp.Model.Insurance;
import com.example.carrentalapp.Model.Payment;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.R;
import com.github.ybq.android.spinkit.style.Wave;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.WordUtils;

import java.util.Calendar;
import java.util.Random;

import c.e.c.Util.Common;
import c.e.c.Util.SendMail;

public class BookingSummaryActivity extends AppCompatActivity {

    private Button back, book, payNow;

    //DRIVER DETAILS
    private TextView name, email, phoneNumber;

    //BOOKING SUMMARY
    private TextView vehicleName, rate, totalDays, _pickup, _return, insurance, insuranceRate, totalCost, pickupLocation;

    //VEHICLE IMAGE
    private ImageView vehicleImage;

    //DATABASE TABLE
    private CustomerDao customerDao;
    private VehicleDao vehicleDao;
    private BookingDao bookingDao;
    private InsuranceDao insuranceDao;
    private BillingDao billingDao;
    private PaymentDao paymentDao;

    //BOOKING
    private Booking booking;
    //INSURANCE
    private Insurance chosenInsurance;
    //VEHICLE
    private Vehicle vehicle;

    private ProgressBar paidLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);

        initComponents();

        Wave wave = new Wave();
        paidLoading.setIndeterminateDrawable(wave);

        listenHandler();
        displayCustomerInformation();
        displaySummary();
        displayTotalCost();

    }

    private void initComponents() {
        back = findViewById(R.id.back);
        book = findViewById(R.id.book);
        payNow = findViewById(R.id.payNow);

        //DRIVER DETAILS
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);

        //BOOKING SUMMARY
        vehicleName = findViewById(R.id.vehicleName);
        rate = findViewById(R.id.rate);
        totalDays = findViewById(R.id.totalDays);
        _pickup = findViewById(R.id.pickup);
        _return = findViewById(R.id.dropoff);
        pickupLocation = findViewById(R.id.pickupLocation);

        //INSURANCE TYPE
        insurance = findViewById(R.id.insurance);
        insuranceRate = findViewById(R.id.insuranceRate);

        //TOTAL COST
        totalCost = findViewById(R.id.totalCost);

        //VEHICLE IMAGE
        vehicleImage = findViewById(R.id.vehicleImage);

        //DATABASE TABLE
        customerDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .customerDao();
        vehicleDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .vehicleDao();
        bookingDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .bookingDao();
        insuranceDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .insuranceDao();
        billingDao  = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .billingDao();
        paymentDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .paymentDao();
        //GET BOOKING OBJECT WHICH WAS PASSED FROM PREVIOUS PAGE
        booking = (Booking) getIntent().getSerializableExtra("BOOKING");
        chosenInsurance = insuranceDao.findInsurance(booking.getInsuranceID());
        vehicle = vehicleDao.findVehicle(booking.getVehicleID());

        paidLoading = findViewById(R.id.paidLoading);
        paidLoading.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void listenHandler() {
        back.setOnClickListener(v -> finish());

        book.setOnClickListener(v -> {

            if(!book.isEnabled()){
                toast(getString(R.string.must_finished_payment_first));
                return;
            }
            generateBilling_Payment();

            chosenInsurance = insuranceDao.findInsurance(booking.getInsuranceID());
            vehicle = vehicleDao.findVehicle(booking.getVehicleID());

            Customer customer = customerDao.findUser(booking.getCustomerID());

            Log.d("SEND_MESSAGE", "start sending message");

            SendMail sm = new SendMail(customer.getEmail(), "Booking Summary #" + booking.getBookingID(),  getEmailString(customer, vehicle, chosenInsurance));
            sm.execute();

            Log.d("SEND_MESSAGE", "finished sending message");

            Intent bookingCompletePage = new Intent(BookingSummaryActivity.this,BookingCompleteActivity.class);
            bookingCompletePage.putExtra("BOOKING",booking);
            startActivity(bookingCompletePage);
        });

        payNow.setOnClickListener(v -> {
            String title=getString(R.string.order_received_title);
            String body=getString(R.string.order_received_body);

            showNotification(title, body);

            paidLoading.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                paidLoading.setVisibility(View.INVISIBLE);
                payNow.setText(R.string.paid_text);
                payNow.setEnabled(false);
                book.setEnabled(true);
            },7000);
        });
    }

    private void generateBilling_Payment() {

        //GENERATE PAYMENT ID
        int paymentID = generateID(600,699);
        while(paymentDao.exist(paymentID)){
            paymentID=generateID(600,699);
        }

        //GENERATE BILLING ID
        int billingID = generateID(500,599);
        while(billingDao.exist(billingID)){
            billingID=generateID(500,599);
        }

        Calendar currentDate = Calendar.getInstance();

        Payment payment = new Payment(paymentID,"Credit",calculateTotalCost(),0);
        Billing billing = new Billing(billingID,"Paid",currentDate,0,paymentID);
        booking.setBillingID(billingID);
        booking.setBookingStatus("Waiting for approval");

        bookingDao.insert(booking);
        billingDao.insert(billing);
        paymentDao.insert(payment);

        vehicle.setAvailability(false);
        vehicleDao.update(vehicle);
    }

    private void displayCustomerInformation() {
        Customer customer = customerDao.findUser(booking.getCustomerID());
        //DISPLAY DRIVER INFO
        name.setText(WordUtils.capitalize(customer.getFullName()));
        email.setText(customer.getEmail());
        phoneNumber.setText(customer.getPhoneNumber());
    }

    @SuppressLint("SetTextI18n")
    private void displaySummary(){

        vehicleName.setText(vehicle.fullTitle());
        rate.setText(Common.getFormattedPrice(vehicle.getPrice())+"/Hari");
        totalDays.setText(Common.getDayDifference(booking.getPickupDate(),booking.getReturnDate())+" Hari");
        _pickup.setText(booking.getPickupTime());
        _return.setText(booking.getReturnTime());
        pickupLocation.setText(booking.getBookingLocation());

        insurance.setText(chosenInsurance.getCoverageType());
        insuranceRate.setText(Common.getFormattedPrice(chosenInsurance.getCost()));

        Picasso.get().load(vehicle.getVehicleImageURL()).into(vehicleImage);
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

    private int generateID(int start, int end){
        Random rnd = new Random();
        int bound = end%100;
        return rnd.nextInt(bound)+start;
    }

    //DEBUGGING
    private void toast(String txt){
        Toast toast = Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT);
        toast.show();
    }

    void showNotification(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID_01234",
                    "NOTIFICATION_CHANNEL_NO_1234",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Desc");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_ID_01234")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), UserViewActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    String getEmailString(Customer customer, Vehicle vehicle, Insurance chosenInsurance) {
        return  "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <title></title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "    <style type=\"text/css\">\n" +
                "        body,\n" +
                "        table,\n" +
                "        td,\n" +
                "        a {\n" +
                "            -webkit-text-size-adjust: 100%;\n" +
                "            -ms-text-size-adjust: 100%;\n" +
                "        }\n" +
                "\n" +
                "        table,\n" +
                "        td {\n" +
                "            mso-table-lspace: 0pt;\n" +
                "            mso-table-rspace: 0pt;\n" +
                "        }\n" +
                "\n" +
                "        img {\n" +
                "            -ms-interpolation-mode: bicubic;\n" +
                "        }\n" +
                "\n" +
                "        img {\n" +
                "            border: 0;\n" +
                "            height: auto;\n" +
                "            line-height: 100%;\n" +
                "            outline: none;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            border-collapse: collapse !important;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            height: 100% !important;\n" +
                "            margin: 0 !important;\n" +
                "            padding: 0 !important;\n" +
                "            width: 100% !important;\n" +
                "        }\n" +
                "\n" +
                "        a[x-apple-data-detectors] {\n" +
                "            color: inherit !important;\n" +
                "            text-decoration: none !important;\n" +
                "            font-size: inherit !important;\n" +
                "            font-family: inherit !important;\n" +
                "            font-weight: inherit !important;\n" +
                "            line-height: inherit !important;\n" +
                "        }\n" +
                "\n" +
                "        @media screen and (max-width: 480px) {\n" +
                "            .mobile-hide {\n" +
                "                display: none !important;\n" +
                "            }\n" +
                "\n" +
                "            .mobile-center {\n" +
                "                text-align: center !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        div[style*=\"margin: 16px 0;\"] {\n" +
                "            margin: 0 !important;\n" +
                "        }\n" +
                "    </style>\n" +
                "\n" +
                "</head>\n" +
                "<body style=\"margin: 0 !important; padding: 0 !important; background-color: #eeeeee;\" bgcolor=\"#eeeeee\">\n" +
                "    <div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: Open Sans, Helvetica, Arial, sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\">\n" +
                "        \n" +
                "    </div>\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"background-color: #eeeeee;\" bgcolor=\"#eeeeee\">\n" +
                "                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" valign=\"top\" style=\"font-size:0; padding: 35px;\" bgcolor=\"#03a9f4\">\n" +
                "                            <div style=\"display:inline-block; max-width:100%; min-width:100px; vertical-align:top; width:100%;\">\n" +
                "                                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\n" +
                "                                    <tr>\n" +
                "                                        <td align=\"left\" valign=\"top\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 36px; font-weight: 800; line-height: 48px;\" class=\"mobile-center\">\n" +
                "                                            <h1 style=\"font-size: 36px; font-weight: 800; margin: 0; color: #ffffff;\">SewaSini</h1>\n" +
                "                                        </td>\n" +
                "                                    </tr>\n" +
                "                                </table>\n" +
                "                            </div>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"padding: 35px 35px 20px 35px; background-color: #ffffff;\" bgcolor=\"#ffffff\">\n" +
                "                            <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\n" +
                "                                <tr>\n" +
                "                                    <td align=\"center\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding-top: 25px;\"> <img src=\"https://img.icons8.com/carbon-copy/100/000000/checked-checkbox.png\" width=\"125\" height=\"120\" style=\"display: block; border: 0px;\" /><br>\n" +
                "                                        <h2 style=\"font-size: 30px; font-weight: 800; line-height: 36px; color: #333333; margin: 0;\"> Pesanan anda sudah kami terima</h2>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding-top: 10px;\">\n" +
                "                                        <p style=\"font-size: 16px; font-weight: 400; line-height: 24px; color: #777777;\"> Anda dapat melihat detail dari pesanan ada dibawah ini </p>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td align=\"left\" style=\"padding-top: 20px;\">\n" +
                "                                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 800; line-height: 24px; padding: 10px;\"> Booking Summary # </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 800; line-height: 24px; padding: 10px;\"> " + booking.getBookingID() +"</td>\n" +
                "                                            </tr>\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 15px 10px 5px 10px;\"> Pengemudi </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 15px 10px 5px 10px;\"> " + WordUtils.capitalize(customer.getFullName()) + " </td>\n" +
                "                                            </tr>\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> Email </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> " + customer.getEmail() + " </td>\n" +
                "                                            </tr>\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> No. Telepon </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> " + customer.getPhoneNumber() + " </td>\n" +
                "                                            </tr>\n" +
                "                                          <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> Tanggal Pengambilan </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> " + booking.getPickupTime() + " </td>\n" +
                "                                            </tr>\n" +
                "                                          <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> Tanggal Pengembalian </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> " + booking.getReturnTime() + " </td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                              <tr>\n" +
                "                                    <td align=\"left\" style=\"padding-top: 20px;\">\n" +
                "                                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 800; line-height: 24px; padding: 10px;\"> Merk mobil </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 800; line-height: 24px; padding: 10px;\">" + vehicle.getModel() + "</td>\n" +
                "                                            </tr>\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 15px 10px 5px 10px;\"> Harga per-hari</td>\n" +
                "                                                <td width=\"25%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 15px 10px 5px 10px;\"> Rp" + vehicle.getPrice() + "</td>\n" +
                "                                            </tr>\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> Jumlah hari </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> " + Common.getDayDifference(booking.getPickupDate(),booking.getReturnDate()) + " </td>\n" +
                "                                            </tr>\n" +
                "                                             <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> Biaya Asuransi </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding: 5px 10px;\"> Rp" + chosenInsurance.getCost() + " </td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td align=\"left\" style=\"padding-top: 20px;\">\n" +
                "                                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"75%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 800; line-height: 24px; padding: 10px; border-top: 3px solid #eeeeee; border-bottom: 3px solid #eeeeee;\"> TOTAL </td>\n" +
                "                                                <td width=\"25%\" align=\"left\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 800; line-height: 24px; padding: 10px; border-top: 3px solid #eeeeee; border-bottom: 3px solid #eeeeee;\"> Rp" + calculateTotalCost() + " </td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td align=\"\" height=\"100%\" valign=\"top\" width=\"100%\" style=\"padding: 0 35px 35px 35px; background-color: #ffffff;\" bgcolor=\"#ffffff\">\n" +
                "                            <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:660px;\">\n" +
                "                                <tr>\n" +
                "                                    <td align=\"center\" valign=\"top\" style=\"font-size:0;\">\n" +
                "                                        <div style=\"display:inline-block; max-width:100%; min-width:240px; vertical-align:top; width:100%;\">\n" +
                "                                            <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\n" +
                "                                                <tr>\n" +
                "                                                    <td align=\"left\" valign=\"top\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 400; line-height: 24px;\">\n" +
                "                                                        <p style=\"font-weight: 800;\">Lokasi Pengantaran</p>\n" +
                "                                                        <p>" + booking.getBookingLocation() + "</p>\n" +
                "                                                    </td>\n" +
                "                                                </tr>\n" +
                "                                            </table>\n" +
                "                                        </div>\n" +
                "                                        \n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }
}
