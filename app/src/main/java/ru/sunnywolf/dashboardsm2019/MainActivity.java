package ru.sunnywolf.dashboardsm2019;

import androidx.appcompat.app.AppCompatActivity;
import ru.sunnywolf.dashboardsm2019.MCP2515.CanMessage;
import ru.sunnywolf.dashboardsm2019.MCP2515.MCP2515;

import android.os.Bundle;
import java.io.IOException;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private GpioLogic gpioLogic;
    private MCP2515 mcp2515;

    private TextView textSpeed;
    private TextView textSpeedMax;
    private TextView textCurrent;

    private MCP2515.MessageReceivedListener canListener = new MCP2515.MessageReceivedListener() {
        @Override
        public void onReceived(CanMessage message) {
            if (message.getId() == 0x42){
                int value = ((int)message.getData().get(1) << 8) | (message.getData().get(0));
                if (value < 0) {
                    value = 0;
                }
                double speed = 2 * Math.PI * 0.000317f * value * 16.0 / 45.0 * 60;

                Log.w(TAG, "NPM: " + value + "  SPEED: " + String.valueOf(speed));

                int oldvalue;

                textSpeed.setText(String.valueOf((int)speed));

                String oldText = textSpeedMax.getText().toString();
                if (oldText.equals("")){
                    textSpeedMax.setText(String.valueOf((int)speed));
                } else {
                    oldvalue = Integer.valueOf(oldText);
                    if ((int)speed > oldvalue){
                        textSpeedMax.setText(String.valueOf((int)speed));
                    }
                }

            }
            if (message.getId() == 0x43){
                int value = ((int)message.getData().get(7) << 8) | (message.getData().get(6));
                textCurrent.setText(String.valueOf(value));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpioLogic = new GpioLogic(this);

        textSpeed = findViewById(R.id.t_speed);
        textSpeedMax = findViewById(R.id.t_speed_max);
        textCurrent = findViewById(R.id.t_current);

        try {
            mcp2515 = MCP2515.create(BoardConfig.MCP2515_SPI, BoardConfig.MCP2515_INT_PIN);

            mcp2515.setListener(canListener);
        } catch (IOException e){
            Log.e(TAG, "onCreate: Unable to connect MCP2515", e);
        }

        Button test = findViewById(R.id.b_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcp2515.processInterrupt();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gpioLogic.release();
        mcp2515.close();
    }
}
