package ru.sunnywolf.dashboardsm2019;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;
import java.util.List;

import android.util.Log;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.PeripheralManager;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String gpioButtonPinName = "BUS NAME";
    private Button mButton;
    private GpioLogic gpioLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpioLogic = new GpioLogic(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gpioLogic.release();
    }
}
