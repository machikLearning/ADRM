package kr.ac.cbnuh.dur;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static kr.ac.cbnuh.dur.common.Constraints.EXECUTION_EXCEPTION;
import static kr.ac.cbnuh.dur.common.Constraints.INTERRUPTED_EXCEPTION;
import static kr.ac.cbnuh.dur.common.Constraints.URL;

/**
 * Created by typebreaker on 2017-08-12.
 */

public class FindIdActivity extends AppCompatActivity{
    private EditText emailText;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_findid);
        emailText = (EditText) findViewById(R.id.EMAIL);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.FindId:
                email = emailText.getText().toString();
                emailCheck();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.goLoginPage:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.goFindPwPage:
                startActivity(new Intent(this, FindPwActivity.class));
                break;
            case R.id.goJoinPage:
                startActivity(new Intent(this, JoinActivity.class));
                break;
        }
    }

    private void emailCheck(){
        HttpUtil httputil = new HttpUtil();
        List<String> result = new ArrayList<String>();
        String content = "mail=" + email;
        try {
            result = httputil.execute(URL+"/ADRM/patient/idCheck",content).get();
        } catch (InterruptedException e) {
            Toast.makeText(this, "ERROR_CODE : " + INTERRUPTED_EXCEPTION, Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(this, "ERROR_CODE : " + EXECUTION_EXCEPTION, Toast.LENGTH_SHORT).show();
        }
        switch (result.get(0)){
            case "IDSEND":
                Toast.makeText(this, "이메일로 아이디가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR":
                Toast.makeText(this, "이메일을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();
        }
    }
}
