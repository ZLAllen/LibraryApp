package com.example.zhaoqi.libraryapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ScanBookActivity extends AppCompatActivity implements View.OnClickListener{

    // use a compound button so either checkbox or switch widgets work.
    public final int BOOK_ON_LOAN = 1;
    public final int BOOK_ON_SHELF = 0;
    public final int TWO_WEEK = 14;
    public final String SHELF = "ON SHELF";
    public final String LOAN = "ON LOAN";
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    public Book book;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    public final static String[] items = new String[]{"Chapin Apartment", "Science and Engineering Library", "Music Library"};








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_book);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = mFirebaseUser.getUid();

        statusMessage = (TextView)findViewById(R.id.status_message);
        barcodeValue = (TextView)findViewById(R.id.barcode_value3);

        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_barcode).setOnClickListener(this);

        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner);
//create a list of items for the spinner.
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    barcodeValue.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);


                    mDatabase.child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            EditText editText1 = findViewById(R.id.editText);
                            editText1.setVisibility(View.VISIBLE);
                            EditText editText2 = findViewById(R.id.editText2);
                            editText2.setVisibility(View.VISIBLE);
                            Spinner spinner = findViewById(R.id.spinner);
                            spinner.setVisibility(View.VISIBLE);
                            Button button_add = findViewById(R.id.addButton2);
                            button_add.setVisibility(View.VISIBLE);
                            Button button_borrow = findViewById(R.id.borrowButton);
                            button_borrow.setVisibility(View.VISIBLE);
                            Button button_return = findViewById(R.id.returnButton);
                            button_return.setVisibility(View.VISIBLE);
                            TextView textView = findViewById(R.id.textView3);
                            textView.setVisibility(View.VISIBLE );

                            if(!dataSnapshot.hasChild(barcode.displayValue)){
                                //if not exist, try to add a book
                                button_borrow.setEnabled(false);
                                button_return.setEnabled(false);
                                new BookRetrieve(barcode.displayValue, editText1, editText2, textView).execute("Hello");


                                button_add.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        book = new Book();
                                        book.setAuthor(editText2.getText().toString());
                                        book.setISBN(barcode.displayValue);
                                        book.setTitle(editText1.getText().toString());
                                        book.setLocation(spinner.getSelectedItemPosition());
                                        book.setStatus(0);
                                        book.setCover(textView.getText().toString());
                                        mDatabase.child("books").child(barcode.displayValue).setValue(book);
                                        finish();
                                    }
                                });

                            }else{
                                textView.setVisibility(View.VISIBLE);
                                for(DataSnapshot messageSnapshot: dataSnapshot.getChildren()){
                                    if(messageSnapshot.getKey().equals(barcode.displayValue)){
                                        book = messageSnapshot.getValue(Book.class);
                                        editText2.setText(book.getAuthor());
                                        editText1.setText(book.getTitle());
                                        spinner.setSelection(book.getLocation());
                                        spinner.setClickable(false);
                                        if(book.getStatus() == BOOK_ON_SHELF)
                                        {
                                            textView.setText(SHELF);
                                        }else{
                                            textView.setText(LOAN);
                                        }
                                    }
                                }

                                if(book.getStatus() == BOOK_ON_SHELF){
                                    button_add.setEnabled(false);
                                    button_return.setEnabled(false);
                                    button_borrow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                            Date date = new Date();
                                            date.setDate(date.getDate() + TWO_WEEK);
                                            book.setStatus(BOOK_ON_LOAN);
                                            book.setDueDay(dateFormat.format(date));
                                            book.setHolder(mUserId);
                                            mDatabase.child("books").child(barcode.displayValue).setValue(book);
                                            mDatabase.child("users").child(mUserId).child(barcode.displayValue).setValue(book);
                                            finish();
                                        }
                                    });
                                }else{
                                    mDatabase.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(mUserId)){
                                                button_borrow.setEnabled(false);
                                                button_add.setEnabled(false);
                                                button_return.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        mDatabase.child("books").child(barcode.displayValue).child("status").setValue(BOOK_ON_SHELF);
                                                        mDatabase.child("users").child(book.getHolder()).child(barcode.displayValue).removeValue();
                                                        finish();
                                                    }
                                                });
                                            }else{
                                                button_add.setEnabled(false);
                                                button_borrow.setEnabled(false);
                                                button_return.setEnabled(false);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }

                                    });

                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}
