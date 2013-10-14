package com.r2mobile.mytravelasia;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class SplashActivity extends FragmentActivity {
    private TextView mTxtHello;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mTxtHello = (TextView) findViewById(R.id.txt_hello);

        mTxtHello.setText("MyTravelPhilippines");
    }
}
