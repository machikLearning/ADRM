package kr.ac.cbnuh.dur;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static kr.ac.cbnuh.dur.R.id.userId;
import static kr.ac.cbnuh.dur.R.id.userPassword;
import static kr.ac.cbnuh.dur.common.Constraints.EXECUTION_EXCEPTION;
import static kr.ac.cbnuh.dur.common.Constraints.INTERRUPTED_EXCEPTION;
import static kr.ac.cbnuh.dur.common.Constraints.URL;
/**
 * Created by typebreaker on 2017-08-12.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText userid;
    private EditText userpassword;
    private String id;
    private String pw;
    private String username;

    private CheckBox autoLogin;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        userid = (EditText) findViewById(userId);
        userpassword = (EditText) findViewById(userPassword);
        autoLogin = (CheckBox) findViewById(R.id.autoLogin);
        preferences= getSharedPreferences("setting", 0);
        editor= preferences.edit();

        if(preferences.getString("autoLoginEnable","").equals("true")){
            id = preferences.getString("id","");
            pw = preferences.getString("pw","");
            loginCheck();
        }
    }



    public void onClick(View v) {
        switch (v.getId()){
            case R.id.LoginButton:
                id = userid.getText().toString();
                pw = userpassword.getText().toString();
                loginCheck();
                break;
            case R.id.goFindIdPage:
                startActivity(new Intent(this, FindIdActivity.class));
                break;
            case R.id.goFindPwPage:
                startActivity(new Intent(this, FindPwActivity.class));
                break;
            case R.id.goJoinPage:
                startActivity(new Intent(this, JoinActivity.class));
                break;
            case R.id.goListPage:
                startActivity(new Intent(this, ListActivity.class));
                break;
        }
    }
    public void loginCheck() {
        HttpUtil httputil = new HttpUtil();
        List<String> result = new ArrayList<String>();
        String content = "id=" + id + "&pw=" + pw;
        try {
            result = httputil.execute(URL+"/ADRM/patient/loginCheck",content).get();
        } catch (InterruptedException e) {
            Toast.makeText(this, "ERROR_CODE : " + INTERRUPTED_EXCEPTION, Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(this, "ERROR_CODE : " + EXECUTION_EXCEPTION, Toast.LENGTH_SHORT).show();
        }
        switch (result.get(0)){
            case "LOGIN_SUCCESS":
                Intent intent = new Intent(this, MedicineCardActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("username", result.get(1)+"님");
                startActivity(intent);
                if(autoLogin.isChecked()){
                    editor.putString("id", id);
                    editor.putString("pw", pw);
                    editor.putString("username", result.get(1));
                    editor.putString("autoLoginEnable", "true");
                    editor.commit();
                }
                break;
            case "ERROR":
                Toast.makeText(this, "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                break;
            case "NOT_APPROVAL":
                Toast.makeText(this, "비활성화 상태인 계정입니다.", Toast.LENGTH_SHORT).show();
                break;
            case "NOT_PATIENT":
                Toast.makeText(this, "환자 외의 접근은 준비중입니다.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();
        }
    }

}
