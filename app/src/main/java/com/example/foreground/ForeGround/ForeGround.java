package com.example.foreground.ForeGround;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.app.Notification.PRIORITY_MAX;

import com.example.foreground.R;
import com.example.foreground.WebService.LocalizacaoAtualWs;

import java.util.HashMap;
import java.util.Map;


public class ForeGround extends Service {
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE";
    public boolean ativo = true;
    LocationManager locationManager;
    String lattitude, longitude;
    public Context contexto = this;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    notification();
                    ForeGround.Worke w = new Worke(startId);
                    ativo = true;
                    w.start();
                    break;
                case STOP_FOREGROUND_SERVICE:
                    ativo = false;
                    break;

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //Serve para usar em celular com a api maior
    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "snap map fake location ";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("snap map channel", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "snap map channel";
    }


    //mostra uma notificação no top da tela
    public Notification notification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        Intent intent = new Intent();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.ic_simbolo_logo_sighra_color);
        builder.setSubText("Localização");
        // Texto do card
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("SIGhRA");
        builder.setStyle(bigTextStyle);

        Notification notification = builder.setPriority(PRIORITY_MAX).setCategory(Notification.CATEGORY_SERVICE).build();
        // Start foreground service.
        startForeground(1, notification);

        return notification;
    }


    //Thread que vai ficar rodando
    class Worke extends Thread {
        public int startId = 0;

        public Worke(int startId) {
            this.startId = startId;
        }


        public void run() {
            while (ativo) {
                try {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Thread.sleep(3000);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Log.e("gps", "GPS Desligado");
                    } else {
                        PegarLocalizacao();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        }

    }


    //pegar a localização do usuario
    private void PegarLocalizacao() {

        if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            //GPS
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //REDE
            Location locationREDE = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                double latti = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                double longi = locationGPS.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Log.e("localização", " LattitudeGPS = " + lattitude + " LongitudeGPS = " + longitude);
                //enviaValor(latti, longi);

            } else if (locationREDE != null) {
                double latti = locationREDE.getLatitude();
                double longi = locationREDE.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Log.e("localização", " LattitudeNet= " + lattitude + " LongitudeNet = " + longitude);
                //enviaValor(latti, longi);


            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Log.e("localização", " Lattitude = " + lattitude + " Longitude = " + longitude);

            } else {
                Log.e("gps", "Esperando GPS");
                LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                MyLocationListener mlocListener = new MyLocationListener();
                Looper.prepare();
                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
                Looper.loop();


            }
        }
    }

    //Metodo que vai ser chamado quando a localizaçao estiver em espera ou nao encontrada
    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.e("localização", " Lattitude espera = " + location.getLongitude() + " Longitude espera = " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }


    //Metodo para Enviar a localizaçao do usuario para o servidor
    private void enviaValor(Double latitude, Double longitude) {
        Map<String, Double> map = new HashMap<>();
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        LocalizacaoAtualWs.enviarLocalizacao(contexto, "localizacao", map);

    }


}
