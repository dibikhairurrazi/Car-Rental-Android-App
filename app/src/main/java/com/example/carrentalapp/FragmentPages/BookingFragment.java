package com.example.carrentalapp.FragmentPages;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carrentalapp.ActivityPages.ViewBookingActivity;
import com.example.carrentalapp.Adapter.BookingAdapter;
import com.example.carrentalapp.Database.BookingDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Model.Booking;
import com.example.carrentalapp.R;
import com.example.carrentalapp.Session.Session;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookingFragment extends Fragment implements BookingAdapter.onBookingListener{

    private ArrayList<Booking> bookings;

    public BookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        BookingDao bookingDao = Room.databaseBuilder(getContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .bookingDao();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        int customerID = Integer.parseInt(Session.read(getContext(), "customerID", "-1"));

        bookings = (ArrayList<Booking>) bookingDao.getAllCustomerBookings(customerID);
        BookingAdapter bookingAdapter = new BookingAdapter(getContext(), bookings, this);
        recyclerView.setAdapter(bookingAdapter);
    }

    @Override
    public void onClick(int position) {
        int bookingID = bookings.get(position).getBookingID();
        Intent viewBooking = new Intent(getContext(), ViewBookingActivity.class);
        viewBooking.putExtra("BOOKINGID",""+bookingID);
        startActivity(viewBooking);
    }
}
