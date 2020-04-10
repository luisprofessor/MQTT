package com.example.mqtt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WAKE_LOCK;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
private String HTTPHOST="tcp://M10.cloudmqtt.com:15748";
private String USER="KSLKSKS";
private String PASSWORD="SKLSKS";
private MqttAndroidClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validaPermisos();
        Log.d("Salida","Valido");

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), HTTPHOST,
                        clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USER);
        options.setPassword(PASSWORD.toCharArray());




        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected

                    Toast.makeText(MainActivity.this,"Conectado",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this,"Conexión Fallida",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(WAKE_LOCK)== PackageManager.PERMISSION_GRANTED)&&((checkSelfPermission(INTERNET)== PackageManager.PERMISSION_GRANTED))
        &&(checkSelfPermission(ACCESS_NETWORK_STATE)== PackageManager.PERMISSION_GRANTED)&&(checkSelfPermission(READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(WAKE_LOCK))&&
                (shouldShowRequestPermissionRationale(INTERNET))&&
                        (shouldShowRequestPermissionRationale(ACCESS_NETWORK_STATE))&&
                                (shouldShowRequestPermissionRationale(READ_PHONE_STATE))){

        }else{
            requestPermissions(new String[]{WAKE_LOCK,INTERNET,ACCESS_NETWORK_STATE,READ_PHONE_STATE},100);
        }

        return false;
    }




    public void cambiar(View v){

        String topic = "/zdusolfi/led";
        String accion=((Button)v).getText().toString();
        String payload="";
        if(accion.equals("ON")){
            payload="OFF";
            ((Button)v).setText("OFF");
        }else {
            payload="ON";
            ((Button)v).setText("ON");
        }

        byte[] encodedPayload = new byte[0];
        try {

            client.publish(topic, payload.getBytes(),0,false);
        } catch (MqttException  e) {
            e.printStackTrace();
        }
    }
}
