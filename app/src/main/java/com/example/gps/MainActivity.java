package com.example.gps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MainActivity extends AppCompatActivity {

    private TextView tVSzer;
    private Button kasuj, buttonZmienKrok, zmienStopnie, activity2;
    private EditText dlKroku;
    private GpsService gpsService;
    private File Plik;
    private RandomAccessFile rAF;

    Toast tost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tVSzer = findViewById(R.id.tVSzer);
        kasuj = findViewById(R.id.kasuj);
        buttonZmienKrok = findViewById(R.id.buttonZmienKrok);
        zmienStopnie = findViewById(R.id.zmienStopnie);
        dlKroku = findViewById(R.id.dlKroku);
        activity2 = findViewById(R.id.activity2);
        wydrukujWspolrzedne();
        wydrukujDaneGps();
    }

    public void wydrukujWspolrzedne(){


    }



    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            GpsService.MyBinder binder = (GpsService.MyBinder) iBinder;
            gpsService = binder.getService();
            gpsService.settVSzer(tVSzer);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public void zmienStopnie(View view) {
        gpsService.zmienStopnieGps();
    }

    public void kasuj(View view) {
        gpsService.kasujGps();
    }

    public void changeActivity(View view) {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    public void ustawDlKroku(View view) {
        double temp = 0D;
        try {
            temp = Double.parseDouble(dlKroku.getText().toString());
        } catch (NumberFormatException e) {}
        gpsService.ustawDlKrokuGps(temp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, GpsService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private void wydrukujDaneGps() {
        final TextView widokDanychGPS = findViewById(R.id.tVSzer);
        final Handler uchwyt = new Handler();
        uchwyt.post(new Runnable() {
            @Override
            public void run() {
                String wynik = "";
                if (gpsService != null) {
                    wynik += "\nSzerokosc: ";
                    wynik += gpsService.szerokość;
                    wynik += "\nDlugosc: ";
                    wynik += gpsService.długość;
                }
               // widokDanychGPS.setText(wynik);

                File folder = new File(getExternalFilesDir(null) +
                        File.separator + "FolderNaLogi");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    try {
                        String nazwa = folder.getCanonicalPath();
                        Log.v("sciezka", nazwa);
                        rAF = new RandomAccessFile(nazwa + "/ZrzutWspolrzednych.txt", "rw");
                        rAF.seek(rAF.length());
                        rAF.writeUTF(wynik);
                        rAF.close();
                    } catch (IOException ioE) {
                        Log.v("bledy", "brak zgody na utworzenie pliku");
                    }
                } else {
                    Log.v("bledy", "blad przy tworzeniu folderu");
                }
                uchwyt.postDelayed(this, 1000);
            }
        });
    }



}