package kr.ac.cbnuh.dur;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import static kr.ac.cbnuh.dur.common.Constraints.URL;
/**
 * Created by typebreaker on 2017-08-12.
 */

public class SearchActivity extends AppCompatActivity {
    private String id;
    private String username;
    private TextView userName;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        id = getIntent().getStringExtra("id");

        username = getIntent().getStringExtra("username");
        userName = (TextView) findViewById(R.id.userName);
        userName.setText(username);

        preferences= getSharedPreferences("setting", 0);
        editor= preferences.edit();

        WebView webView = (WebView) findViewById(R.id.searchWebView);
        webView.setWebViewClient(new WebViewClient());

        webView.getSettings().setBuiltInZoomControls(false);
        // 자바 스크립트 허용
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        // jsp alert 기능을 웹뷰에 맞게 변환
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result){
                new AlertDialog.Builder(view.getContext())
                        .setTitle("메세지")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(true)
                        .create()
                        .show();
                return true;
            }

            // jsp confirm 기능을 웹뷰에 맞게 변환
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final android.webkit.JsResult result){
                new AlertDialog.Builder(view.getContext())
                        .setTitle("메세지")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .create()
                        .show();
                return true;
            }
        });
        webView.loadUrl(URL+"/ADRM/patient/searchMedicine?id="+id);
    }

    public void onClick(View v) {
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
        }
    }

}
