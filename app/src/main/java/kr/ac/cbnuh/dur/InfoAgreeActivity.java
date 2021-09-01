package kr.ac.cbnuh.dur;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

/**
 * Created by typebreaker on 2017-10-26.
 */

public class InfoAgreeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_infoagree);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.agreeButton:
                break;
            case R.id.cancelButton:
                break;
        }
    }
}
