package com.example.testbarang;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TambahData extends AppCompatActivity {
    private DatabaseReference database;
    //variable fields EditText dan Button
    private Button btSubmit;
    private EditText etKode;
    private EditText etNama;

    @Override
    protected void onCreate(Bundle savedinstanceState) {
        super.onCreate(savedinstanceState);
        setContentView(R.layout.activity_tambah_data);

        etKode = (EditText) findViewById(R.id.editNo);
        etNama = (EditText) findViewById(R.id.editNama);
        btSubmit = (Button) findViewById(R.id.btnOk);

        //mengambil referensi ke firebase database
        database= FirebaseDatabase.getInstance().getReference();

        final Barang barang = (Barang) getIntent().getSerializableExtra("data");

        if (barang != null) {
            etKode.setText(barang.getKode());
            etNama.setText(barang.getNama());
            btSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    barang.setKode(etKode.getText().toString());
                    barang.setNama(etNama.getText().toString());

                    updateBrg(barang);
                }
            });
        } else {
            btSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isEmpty(etKode.getText().toString()) && !isEmpty(etNama.getText().toString()))
                        submitBrg(new Barang(etKode.getText().toString(), etNama.getText().toString()));
                    else
                        Toast.makeText(getApplicationContext(),"Data barang tidak boleh kosong", Toast.LENGTH_LONG).show();

                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            etNama.getWindowToken(), 0);
                }
            });
        }
    }

    private boolean isEmpty(String s){
        // Cek apakah ada fields yang kosong, sebelum disubmit
        return TextUtils.isEmpty(s);
    }
    private void updateBrg(Barang barang) {
        /**
         * Baris kode yang digunakan untuk mengupdate data barang
         * yang sudah dimasukkan di Firebase Realtime Database
         */
        database.child("Barang") //akses parent index, ibaratnya seperti nama tabel
                .child(barang.getKode())
                .setValue(barang) //set value barang yang baru
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {

                        /**
                         * Baris kode yang akan dipanggil apabila proses update barang sukses
                         */

                        Toast.makeText(getApplicationContext(),"Data berhasil diedit",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void submitBrg (Barang barang) {
    /* Berikut ini adalah kode yang digunakan untuk mengirimkan data ke
    * Firebase Realtime Database dan juga kita set onSuccessListener yang
    * berisi kode yang akan dijalankan ketika data berhasil ditambahkan
    */
    database.child ("Barang"). push() .setValue(barang). addOnSuccessListener(this, new OnSuccessListener<Void>() {

        @Override
        public void onSuccess(Void avoid) {
        etKode.setText("");
        etNama.setText("");
        Toast. makeText(getApplicationContext(),"Data berhasil ditambahkan",Toast.LENGTH_LONG).show();
            }
        });
    }
    public static Intent getActIntent(Activity activity) {
        // kode untuk pengambilan Intent
        return new Intent(activity, TambahData.class);
    }
}
