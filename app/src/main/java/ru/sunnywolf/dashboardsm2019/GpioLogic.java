package ru.sunnywolf.dashboardsm2019;

import android.content.Context;
import android.util.Log;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class GpioLogic {
    private static final String TAG = GpioLogic.class.getSimpleName();

    private AppCompatActivity mActivity;

    private HashMap<String, Gpio> mGpioList;

    private ImageView mImgTurnLightL;
    private ImageView mImgTurnLightR;
    private ImageView mImgLight;

    private AlphaAnimation mTurnAnimation;
    private PeripheralManager mPeripheralManager;

    private boolean isInitialized = false;

    public GpioLogic(@NonNull AppCompatActivity activity) {
        mActivity = activity;

        mImgTurnLightL = mActivity.findViewById(R.id.i_tl_left);
        mImgTurnLightR = mActivity.findViewById(R.id.i_tl_right);
        mImgLight = mActivity.findViewById(R.id.i_light);

        mPeripheralManager = PeripheralManager.getInstance();

        mGpioList = new HashMap<>();

        mTurnAnimation = new AlphaAnimation(1.0f, 0.0f);
        mTurnAnimation.setDuration(500);
        mTurnAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        mTurnAnimation.setRepeatMode(AlphaAnimation.REVERSE);

        setup();
    }

    private GpioCallback mCBLight = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio){
            int state = 0;
            try {
                state = mGpioList.get(BoardConfig.PORT_LIGHT_SIDE).getValue() ? 1 : 0;
                state = mGpioList.get(BoardConfig.PORT_LIGHT_CLOSE).getValue() ? 2 : state;
                state = mGpioList.get(BoardConfig.PORT_LIGHT_FAR).getValue() ? 3 : state;
            } catch (NullPointerException|IOException e){
                Log.e(TAG, "onGpioEdge: ", e);
            }

            switch (state){
                case 0:
                case 1:
                    mImgLight.setAlpha(0.0f);
                    break;
                case 2:
                    mImgLight.setImageResource(R.drawable.ic_light_close);
                    mImgLight.setAlpha(1.0f);
                    break;
                case 3:
                    mImgLight.setImageResource(R.drawable.ic_light_far);
                    mImgLight.setAlpha(1.0f);
                    break;
            }

            mImgLight.invalidate();

            return true;
        }
    };

    private GpioCallback mCBTurnLight = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio){
            int state = 0;

            try {
                state |= mGpioList.get(BoardConfig.PORT_TURN_LIGHT_L).getValue() ? 1 : 0;
                state |= mGpioList.get(BoardConfig.PORT_TURN_LIGHT_R).getValue() ? 2 : 0;
            } catch (NullPointerException|IOException e){
                Log.e(TAG, "onGpioEdge: ", e);
            }

            mImgTurnLightL.setAlpha(0.0f);
            mImgTurnLightR.setAlpha(0.0f);
            mImgTurnLightL.clearAnimation();
            mImgTurnLightR.clearAnimation();

            if ((state & 1) > 0){
                mImgTurnLightL.setAlpha(1.0f);
                mImgTurnLightL.startAnimation(mTurnAnimation);
            }
            if ((state & 2) > 0){
                mImgTurnLightR.setAlpha(1.0f);
                mImgTurnLightR.startAnimation(mTurnAnimation);
            }

            return true;
        }
    };

    private void setup(){
        try {
            mGpioList.put(BoardConfig.PORT_LIGHT_SIDE, mPeripheralManager.openGpio(BoardConfig.PORT_LIGHT_SIDE));
            mGpioList.put(BoardConfig.PORT_LIGHT_CLOSE, mPeripheralManager.openGpio(BoardConfig.PORT_LIGHT_CLOSE));
            mGpioList.put(BoardConfig.PORT_LIGHT_FAR, mPeripheralManager.openGpio(BoardConfig.PORT_LIGHT_FAR));
            mGpioList.put(BoardConfig.PORT_TURN_LIGHT_L, mPeripheralManager.openGpio(BoardConfig.PORT_TURN_LIGHT_L));
            mGpioList.put(BoardConfig.PORT_TURN_LIGHT_R, mPeripheralManager.openGpio(BoardConfig.PORT_TURN_LIGHT_R));

            for (Gpio gpio: mGpioList.values()) {
                gpio.setDirection(Gpio.DIRECTION_IN);
                gpio.setActiveType(Gpio.ACTIVE_HIGH);
                gpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            }

            mGpioList.get(BoardConfig.PORT_TURN_LIGHT_L).registerGpioCallback(mCBTurnLight);
            mGpioList.get(BoardConfig.PORT_TURN_LIGHT_R).registerGpioCallback(mCBTurnLight);

            mGpioList.get(BoardConfig.PORT_LIGHT_SIDE).registerGpioCallback(mCBLight);
            mGpioList.get(BoardConfig.PORT_LIGHT_CLOSE).registerGpioCallback(mCBLight);
            mGpioList.get(BoardConfig.PORT_LIGHT_FAR).registerGpioCallback(mCBLight);

            isInitialized = true;
        } catch (NullPointerException|IOException e){
            Log.e(TAG, "setup: ", e);
        }
    }

    public void release(){
        if (mGpioList.size() > 0){
            for (Gpio gpio: mGpioList.values()) {
                try {
                    gpio.close();
                } catch (IOException e) {
                    Log.e(TAG, "release: ", e);
                }
                gpio = null;
            }
            mGpioList.clear();
        }
    }
}
