package kr.ac.cbnuh.dur;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * Created by typebreaker on 2017-07-30.
 */

public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        ImageView imageView = (ImageView) findViewById(R.id.loadingImage);
        imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };

        handler.sendEmptyMessageDelayed(0,1500);
    }
}
