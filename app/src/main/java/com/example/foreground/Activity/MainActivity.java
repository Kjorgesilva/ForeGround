package com.example.foreground.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foreground.ForeGround.ForeGround;
import com.example.foreground.InterfaceHelp.InterfaceHelp;
import com.example.foreground.R;

public class MainActivity extends AppCompatActivity implements InterfaceHelp {


    private Context contexto = this;
    private Button btnGPS, btnWIFI, btnForeGround,btnParar;
    private TextView txtGPS, txtWIFI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        metodoPermissao();
        findView();
        clickView();

    }

    @Override
    public void findView() {
        btnGPS = findViewById(R.id.btnGPS);
        btnWIFI = findViewById(R.id.btnWIFI);
        txtGPS = findViewById(R.id.statusGPS);
        txtWIFI = findViewById(R.id.statusWIFI);
        btnForeGround = findViewById(R.id.btnForeGround);
        btnParar = findViewById(R.id.btnParar);
    }

    @Override
    public void clickView() {
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarGPS();
            }
        });

        btnWIFI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarWifi()) {
                    txtWIFI.setText("Ativado");
                    Log.e("tag", "ativo");
                } else {
                    txtWIFI.setText("Desativado");
                    Log.e("tag", "nao ativo");
                    ativarWifi();


                }
            }
        });

        btnForeGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(contexto, ForeGround.class);
                intent.setAction(ForeGround.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);
            }
        });

        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contexto, ForeGround.class);
                intent.setAction(ForeGround.STOP_FOREGROUND_SERVICE);
                startService(intent);
            }
        });


    }


    //Metodos para o GPS
    public void verificarGPS() {
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("gps", "GPS Desligado");

            txtGPS.setText("Desativado");
            ativaLocalizacaoGPS();
        } else {
            Log.e("gps", "GPS Ativado");
            txtGPS.setText("Ativado");
        }


    }
    private void ativaLocalizacaoGPS() {

        AlertDialog.Builder alerta = new AlertDialog.Builder(contexto);
        alerta.setTitle("Atenção");
        alerta.setMessage("É necessário que aceite a permissão de acesso a localização " +
                "para que as funções do aplicativo possam funcionar corretamente. Deseja ativar ?");
        alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            }
        });
        alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alerta.setCancelable(false);
        alerta.show();

    }


    //Metodos para o WI-FI
    private void ativarWifi() {

        final AlertDialog.Builder alertaWifi = new AlertDialog.Builder(contexto);
        alertaWifi.setTitle("Atenção");
        final AlertDialog alerta = alertaWifi.create();
        alertaWifi.setMessage("É necessário que ligue o Wi-fi, " +
                "para que as funções do aplicativo possam funcionar corretamente. Deseja ativar ? ");


        alertaWifi.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertaWifi.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                if (verificarWifi()) {
                    txtWIFI.setText("Ativado");
                    Log.e("tag", "ativo");
                } else {
                    txtWIFI.setText("Desativado");
                    Log.e("tag", "nao ativo");

                }

            }
        });
        alertaWifi.setCancelable(false);
        alertaWifi.show();


    }
    public boolean verificarWifi() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    //Metodos para permissao
    public void metodoPermissao() {
        //VERIFICAR METODO PARA MELHORIA
        try {
            if (verPermissao()) {
                Log.println(Log.DEBUG, "entrou", "tem Permissao");
            } else {
                Log.println(Log.ERROR, "entrou", "Nao Tem permissao");
                pedirPermissao();
            }
        } catch (Exception e) {
            Log.println(Log.ERROR, "entrou", "erro" + e.getMessage());
        }
    }
    public Boolean verPermissao() {
        int verificarLocalizacaoPreciso = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int verificarLocalizacaoAproximada = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (verificarLocalizacaoPreciso == PackageManager.PERMISSION_GRANTED && verificarLocalizacaoAproximada == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
    public void pedirPermissao() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
    }

}