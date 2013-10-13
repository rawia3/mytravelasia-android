package com.r2mobile.mytravelasia;

import android.os.Bundle;
import android.widget.TextView;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

public class SplashActivity extends RoboFragmentActivity {
    @InjectView(R.id.txt_hello)
    private TextView mTxtHello;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mTxtHello.setText("MyTravelPhilippines");
    }
}
