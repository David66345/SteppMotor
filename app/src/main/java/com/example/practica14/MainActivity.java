package com.example.practica14;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ingenieriajhr.blujhr.BluJhr;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BluJhr blue;
    List<String> requiredPermissions;
    ArrayList<String> devicesBluetooth = new ArrayList<String>();
    LinearLayout viewConn;
    ListView listDeviceBluetooth;
    ImageButton left, right; //1: izquierda, 2: derecha
    Button paso, medioPaso, doblePaso; //1: un paso, 2: medio paso, 2: dos pasos
    TextView consola;
    ImageView imagenRueda, imagenSwitch;
    String tipoPaso ="1";
    long velocidad;

    private static final int DIRECTION_CLOCKWISE = 1;
    private static final int DIRECTION_COUNTERCLOCKWISE = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blue = new BluJhr(this);
        blue.onBluetooth();
        listDeviceBluetooth = findViewById(R.id.listDeviceBluetooth);
        viewConn = findViewById(R.id.viewConn);
        left = findViewById(R.id.b_push_left);
        right = findViewById(R.id.b_push_right);
        paso = findViewById(R.id.b_paso);
        medioPaso = findViewById(R.id.b_medio_paso);
        doblePaso = findViewById(R.id.b_doble_paso);
        consola = findViewById(R.id.tv_consola);
        imagenRueda = findViewById(R.id.i_m_paso_rueda);
        imagenSwitch = findViewById(R.id.i_switch);

        listDeviceBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!devicesBluetooth.isEmpty()){
                    blue.connect(devicesBluetooth.get(i));
                    blue.setDataLoadFinishedListener(new BluJhr.ConnectedBluetooth() {
                        @Override
                        public void onConnectState(@NonNull BluJhr.Connected connected) {
                            if (connected == BluJhr.Connected.True){
                                Toast.makeText(getApplicationContext(),"Exito",Toast.LENGTH_SHORT).show();
                                listDeviceBluetooth.setVisibility(View.GONE);
                                viewConn.setVisibility(View.VISIBLE);
                                rxReceived();
                            }else{
                                if (connected == BluJhr.Connected.Pending){
                                    Toast.makeText(getApplicationContext(),"Pendiente",Toast.LENGTH_SHORT).show();
                                }else{
                                    if (connected == BluJhr.Connected.False){
                                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                                    }else{
                                        if (connected == BluJhr.Connected.Disconnect){
                                            Toast.makeText(getApplicationContext(),"Desconectado",Toast.LENGTH_SHORT).show();
                                            listDeviceBluetooth.setVisibility(View.VISIBLE);
                                            viewConn.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        paso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cambia la imagen para que se muestre el primer switch
                imagenSwitch.setImageResource(R.drawable.switch1);
                tipoPaso="1";
                velocidad=1000;
                Toast.makeText(MainActivity.this, "Ha elegido paso normal", Toast.LENGTH_SHORT).show();
            }
        });

        paso.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                blue.closeConnection();
                return false;
            }
        });

        medioPaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cambia la imagen para que se muestre el segundo switch
                imagenSwitch.setImageResource(R.drawable.switch2);
                tipoPaso="2";
                velocidad=1500;
                Toast.makeText(MainActivity.this, "Ha elegido medio paso", Toast.LENGTH_SHORT).show();
            }
        });

        medioPaso.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                blue.closeConnection();
                return false;
            }
        });

        doblePaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cambia la imagen para que se muestre el tercer switch
                imagenSwitch.setImageResource(R.drawable.switch3);
                tipoPaso="3";
                velocidad=500;
                Toast.makeText(MainActivity.this, "Ha elegido doble paso", Toast.LENGTH_SHORT).show();
            }
        });

        doblePaso.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                blue.closeConnection();
                return false;
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blue.bluTx("1"+tipoPaso);
                // Rotar 360 grados hacia la izquierda con duración de 500 milisegundos
                rotateImage(imagenRueda, 360, velocidad, DIRECTION_COUNTERCLOCKWISE);
            }
        });

        left.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                blue.closeConnection();
                return false;
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blue.bluTx("2"+tipoPaso);
                // Rotar 360 grados hacia la derecha con duración de 500 milisegundos
                rotateImage(imagenRueda, 360, velocidad, DIRECTION_CLOCKWISE);
            }
        });

        right.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                blue.closeConnection();
                return false;
            }
        });
    }

    // Método para rotar la imagen
    private void rotateImage(ImageView imageView, float degrees, long duration, int direction) {
        // Calcular el ángulo final de la rotación teniendo en cuenta la dirección
        float toDegrees = (direction == DIRECTION_CLOCKWISE) ? degrees : -degrees;

        // Crear una animación de rotación
        RotateAnimation rotateAnimation = new RotateAnimation(0, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // Configurar la duración de la animación
        rotateAnimation.setDuration(duration);

        // Asegurarse de que la imagen permanezca en su posición final después de la animación
        rotateAnimation.setFillAfter(true);

        // Iniciar la animación en la imagen
        imageView.startAnimation(rotateAnimation);
    }

    private void rxReceived() {

        blue.loadDateRx(new BluJhr.ReceivedData() {
            @Override
            public void rxDate(@NonNull String s) {
                consola.setText(consola.getText().toString()+s);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (blue.checkPermissions(requestCode,grantResults)){
            Toast.makeText(this, "Salir", Toast.LENGTH_SHORT).show();
            blue.initializeBluetooth();
        }else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                blue.initializeBluetooth();
            }else{
                Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (!blue.stateBluetoooth() && requestCode == 100){
            blue.initializeBluetooth();
        }else{
            if (requestCode == 100){
                devicesBluetooth = blue.deviceBluetooth();
                if (!devicesBluetooth.isEmpty()){
                    ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,devicesBluetooth);
                    listDeviceBluetooth.setAdapter(adapter);
                }else{
                    Toast.makeText(this, "No tienes vinculados dispositivos", Toast.LENGTH_SHORT).show();
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}