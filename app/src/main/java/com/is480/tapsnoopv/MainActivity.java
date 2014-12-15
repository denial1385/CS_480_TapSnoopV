package com.is480.tapsnoopv;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
//import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
//import android.hardware.SensorEventListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements SensorEventListener {

    //variable declaration
    private SensorManager senman;
    //private boolean color = false;
    TextView vw;
    //private long highWaterMark;
    FileOutputStream myFile;

    // end variable declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //default
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //end default

        vw = (TextView)findViewById(R.id.acceleration);
        vw.setBackgroundColor(Color.GREEN);

        //String storage = Environment.getExternalStorageDirectory().toString();
        //System.out.write(storage);
        //File myDir = new File(storage + "/storage/sdc");
        //myDir.mkdirs();

        //file check and make
        Date curDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
        String file_time = sdf.format(curDate);

        File file = new File((getFilesDir()+File.separator+"MyFile."+  file_time + ".out") );


        //if (file.exists()) file.delete();

        try {
          myFile = new FileOutputStream(file);
        } catch (IOException e) {
            return;
        } // end try catch file

        senman = (SensorManager) getSystemService(SENSOR_SERVICE);
        //long highWaterMark = System.currentTimeMillis();

        vw.setText("Now listening... ");
    } // end method onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } // end method onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    } // end method onOptionsItemSelected

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    } // end method onSensorChanged

    public void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        double x = values[0];
        double y = values[1];
        double z = values[2];

        double actualTime = event.timestamp/1000.0;
        vw.setText("Press back to quit. \n\nms: " + actualTime + "\nX: " + x + "\nY: " + y + "\nZ: " + z
                + "\n\n\nAll done! Your file should be in 'data/data/com.is480.tapsnoopv/files' with a timestamp. Enjoy.");

        byte[] ms = ByteBuffer.allocate(8).putDouble(actualTime).array();
        byte[] bx = ByteBuffer.allocate(8).putDouble(x).array();
        byte[] by = ByteBuffer.allocate(8).putDouble(y).array();
        byte[] bz = ByteBuffer.allocate(8).putDouble(z).array();

        try {
                myFile.write(ms, 0, 8);
                myFile.write(bx, 0, 8);
                myFile.write(by, 0, 8);
                myFile.write(bz, 0, 8);

        } catch (IOException e) {
            //return;
        } // end trycatch
    } // end method getAccelerometer

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    } // end method onAccuracyChanged

    @Override
    public void onResume() {
        super.onResume();

        //senman.registerListener(this, senman.getDefaultSensor(Sensor.TYPE_ACCELEROMETER,Sensor.SENSOR_DELAY_GAME);

        senman.registerListener(this, senman.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    } // end method onResume

    @Override
    public void onBackPressed() {

        try {
            myFile.flush();
            myFile.close();
        } catch (IOException e) {
            return;
        } // end trycatch

        senman.unregisterListener(this);
        super.onBackPressed();

    } // end method onBackPressed

    @Override
    protected void onPause() {
        super.onPause();

        senman.unregisterListener(this);
    } // end method onPause

    // ************************************************************
}
