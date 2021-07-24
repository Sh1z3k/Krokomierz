package com.example.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class GpsService extends Service {

    private final IBinder mBinder = new MyBinder();
    private Location pozycja, popPozycja;
    public double szerokość, długość, odległośćWM, całkOdległość, długośćKroku;
    private boolean czyStopnie = false;
    private LocationManager zarzadcaPozycji;
    private TextView tVSzer;
    int i=0;
    File plik;
    String nazwa = "przykladowyPlik";
    public GpsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    public class MyBinder extends Binder {
        GpsService getService(){
            return GpsService.this;
        }
    }

    public void settVSzer(TextView textView) {
        this.tVSzer = textView;
    }

    public void zmienStopnieGps() {
        this.czyStopnie = !czyStopnie;
    }

    public void kasujGps() {
        this.całkOdległość = 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        zarzadcaPozycji = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            zarzadcaPozycji.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, sluchacz);
        } catch(SecurityException se) {
            Log.v("niedostępne","Problemy bezpieczeństwa");
        }
        if(i==10){
            getFilesDir().getPath();

            i=0;
        }else
            i++;



    }



    public void zapisz(int a, int b, String c) {

        plik = new File(nazwa);
        if (plik.exists())
            Toast.makeText(this, "Istnieje! ", Toast.LENGTH_LONG).show();
        else Toast.makeText(this, "Nie istnieje!", Toast.LENGTH_LONG).show();
        if (plik.isDirectory()) {
            for (String element : plik.list())
                Log.v("pliki", element);
        }if (plik.isFile()) plik.delete();
    }


        LocationListener sluchacz = new LocationListener() {

        @Override
        public void onLocationChanged(Location pozycjaW) {

            pozycja = pozycjaW;
            if (popPozycja == null) {
                popPozycja = pozycjaW;
            }
            szerokość = pozycjaW.getLatitude();
            długość = pozycjaW.getLongitude();
            odległośćWM = pozycjaW.distanceTo(popPozycja);
            całkOdległość += odległośćWM;
            String odl;
            if (!czyStopnie)
                odl = "\nszerokość" + String.format("%10.6f", szerokość) + "\ndługość" + String.format("%10.6f", długość);
            else
                odl = "\nszerokość" + zamieńNaStopnie(true, szerokość) + "\ndługość" + zamieńNaStopnie(false, długość);

            odl += "\nodległość" + String.format("%10.1f", odległośćWM) + "\ncałkowita" + String.format("%10.1f", całkOdległość) + "\nliczbakroków " + String.format("%06d", (int) (całkOdległość / długośćKroku));
            tVSzer.setText(odl);
            popPozycja = pozycjaW;
        }
    };

    public String zamieńNaStopnie(boolean czyFi, double x) {
        int stopnieSZ = (int) x;
        int minutySZ = (int)((x - (int) x) * 60);
        double sekundySZ = (x - (stopnieSZ + minutySZ / 60.0)) * 3600;
        char a = 176;
        String zamienione;
        if(czyFi)
            zamienione = String.format(String.format("%02d"+a+"%02d'%04.1f\"", stopnieSZ, minutySZ, sekundySZ));
        else
            zamienione = String.format(String.format("%03d"+a+"%02d'%04.1f\"", stopnieSZ, minutySZ, sekundySZ));
        return zamienione;
    }

    //###########################################
    public void ustawDlKrokuGps(double krok) {
        this.długośćKroku = krok;
    }

}