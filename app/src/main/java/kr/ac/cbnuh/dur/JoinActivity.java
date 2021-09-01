package kr.ac.cbnuh.dur;

import android.content.Intent;
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

import static kr.ac.cbnuh.dur.common.Constraints.EXECUTION_EXCEPTION;
import static kr.ac.cbnuh.dur.common.Constraints.INTERRUPTED_EXCEPTION;
import static kr.ac.cbnuh.dur.common.Constraints.URL;
/**
 * Created by typebreaker on 2017-08-12.
 */

public class JoinActivity extends AppCompatActivity {
    private EditText userid;
    private EditText userpassword;
    private EditText userpasswordcheck;
    private EditText useremail;
    private EditText username;
    private EditText usercode;
    private CheckBox userrole1;
    private CheckBox userrole2;
    private String id;
    private String pw;
    private String pw2;
    private String name;
    private String email;
    private String code;
    private boolean role1;
    private boolean role2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_join);
        role1 = false;
        role2 = false;
        userid = (EditText) findViewById(R.id.userId);
        userpassword = (EditText) findViewById(R.id.userPassword);
        userpasswordcheck = (EditText) findViewById(R.id.userPasswordCheck);
        useremail = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.name);
        usercode = (EditText) findViewById(R.id.code);
        userrole1 = (CheckBox) findViewById(R.id.role1);
        userrole2 = (CheckBox) findViewById(R.id.role2);

    }


    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitButton:
                id = userid.getText().toString();
                pw = userpassword.getText().toString();
                pw2 = userpasswordcheck.getText().toString();
                name = username.getText().toString();
                email = useremail.getText().toString();
                code = usercode.getText().toString();
                if(userrole1.isChecked())
                    role1 = true;
                if(userrole2.isChecked())
                    role2 = true;
                joinCheck();
                break;
            case R.id.cancelButton:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    public void joinCheck() {
        HttpUtil httputil = new HttpUtil();
        List<String> result = new ArrayList<String>();

        if(!pw.equals(pw2)){
            Toast.makeText(this, pw + " " + pw2, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
            return ;
        }
        if(!role1 && !role2){
            Toast.makeText(this, "회원형태를 선택해주세요", Toast.LENGTH_SHORT).show();
            return ;
        }

        String content = "id=" + id + "&pw=" + pw + "&pw2=" + pw2 + "&name=" + name + "&email=" + email + "&code=" + code + "&role1=" + role1  + "&role2=" + role2;

        try {
            result = httputil.execute(URL+"/ADRM/patient/joinCheck",content).get();
        } catch (InterruptedException e) {
            Toast.makeText(this, "ERROR_CODE : " + INTERRUPTED_EXCEPTION, Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(this, "ERROR_CODE : " + EXECUTION_EXCEPTION, Toast.LENGTH_SHORT).show();
        }
        switch (result.get(0)){
            case "JOIN_SUCCESS":
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case "REJECT":
                Toast.makeText(this, "회원가입에 실패하였습니다. 다시 시도해 주세요 .", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();
        }
    }
}
