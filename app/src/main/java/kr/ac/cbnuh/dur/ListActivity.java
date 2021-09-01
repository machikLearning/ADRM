package kr.ac.cbnuh.dur;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by typebreaker on 2017-08-12.
 */

public class ListActivity extends AppCompatActivity {
    private String id;
    private String username;
    private TextView userName;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list);
        id = getIntent().getStringExtra("id");
        username = getIntent().getStringExtra("username");
        userName = (TextView) findViewById(R.id.userName);
        userName.setText(username);

        preferences= getSharedPreferences("setting", 0);
        editor= preferences.edit();
    }
    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.logout:
                intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editor.clear();
                editor.commit();
                startActivity(intent);
                finish();
                break;
            case R.id.goMedicineCardPage:
                intent = new Intent(this, MedicineCardActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("username",username);
                startActivity(intent);
                break;
            case R.id.goListPage:
                intent = new Intent(this, ListActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("username",username);
                startActivity(intent);
                break;
            case R.id.goSearchPage:
                intent = new Intent(this, SearchActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("username",username);
                startActivity(intent);
                break;
            case R.id.goQRCodePage:
                intent = new Intent(this, QRCodeActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("username",username);
                startActivity(intent);
                break;
        }
    }
}
