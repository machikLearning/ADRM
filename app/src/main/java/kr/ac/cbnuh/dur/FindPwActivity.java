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

public class FindPwActivity extends AppCompatActivity {
    private EditText idText;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_findpw);
        idText = (EditText) findViewById(R.id.id);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.FindPw:
                id = idText.getText().toString();
                idCheck();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.goLoginPage:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.goFindIdPage:
                startActivity(new Intent(this, FindIdActivity.class));
                break;
            case R.id.goJoinPage:
                startActivity(new Intent(this, JoinActivity.class));
                break;
        }
    }

    private void idCheck() {
        HttpUtil httputil = new HttpUtil();
        List<String> result = new ArrayList<String>();
        String content = "id=" + id;
        try {
            result = httputil.execute(URL +"/ADRM/patient/pwCheck",content).get();
        } catch (InterruptedException e) {
            Toast.makeText(this, "ERROR_CODE : " + INTERRUPTED_EXCEPTION, Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(this, "ERROR_CODE : " + EXECUTION_EXCEPTION, Toast.LENGTH_SHORT).show();
        }
        switch (result.get(0)){
            case "PWSEND":
                Toast.makeText(this, "이메일로 임시 비밀번호가 발급 되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case "ERROR":
                Toast.makeText(this, "아이디를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();
        }
    }
}
