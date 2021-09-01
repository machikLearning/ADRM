package kr.ac.cbnuh.dur;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.edb.bpdec.CResult;
import com.edb.bpdec.EDBBpDec;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ubcare.upharmbarcodelib.cbnuh.Upharm2DDecoder;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kr.ac.cbnuh.dur.common.Constraints;

import static kr.ac.cbnuh.dur.common.Constraints.EXECUTION_EXCEPTION;
import static kr.ac.cbnuh.dur.common.Constraints.INTERRUPTED_EXCEPTION;
import static kr.ac.cbnuh.dur.common.Constraints.URL;

/**
 * Created by typebreaker on 2017-08-12.
 */

public class QRCodeActivity extends AppCompatActivity {
    private String id;
    private TextView medicinenumber;
    private IntentIntegrator qrScan;
    private String username;
    private TextView userName;

    private EDBBpDec EDBDec;
    private Upharm2DDecoder decoder;
    private Context context;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = QRCodeActivity.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        preferences= getSharedPreferences("setting", 0);
        editor= preferences.edit();

        id = getIntent().getStringExtra("id");

        username = getIntent().getStringExtra("username");
        userName = (TextView) findViewById(R.id.userName);
        userName.setText(username);

        medicinenumber = (TextView) findViewById(R.id.MedicineNumber);
        qrScan = new IntentIntegrator(this);


        if(this.EDBDec == null) {
            this.EDBDec = new EDBBpDec(this.context);
        }

        new UserChecker().execute();
        decoder = new Upharm2DDecoder(this, "cbnuh00", "cbnuh00");
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
            case R.id.QRCodeScan:
                qrScan.setPrompt("Scanning...");
                qrScan.initiateScan();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(QRCodeActivity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {
                //data를 json으로 변환
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String params = this.checkMedicine(data.getStringExtra("SCAN_RESULT"));
                if(!params.equals("")) {
                    params = "id="+this.id+params;
                    try {
                        QRCodeResponse qrCodeResponse = new QRCodeResponse();
                        String message = qrCodeResponse.execute(URL+ "/ADRM/patient/checkQRcode", params).get();

                        builder.setMessage(message);

                    } catch (InterruptedException e) {
                        builder.setMessage("ERROR_CODE : " + Constraints.INTERRUPTED_EXCEPTION);
                    } catch (ExecutionException e) {
                        builder.setMessage("ERROR_CODE : " + Constraints.EXECUTION_EXCEPTION);
                    }
                } else {
                    builder.setMessage("죄송합니다. 지금 스캔하신 처방전의 QR코드는 아직 인식되지 않습니다.\n 처방된 약제를 직접 검색해주세요.");
                }
                builder.show();
                medicinenumber.setText("완료되었습니다");
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String checkMedicine(String data) {
        String parameter = "";
        if(this.EDBDec == null) {
            this.EDBDec = new EDBBpDec(this.context);
        }
        try {
            data = new String(data.getBytes("ISO-8859-1"), "EUC-KR");
            data = data.replace("http://www.edb.co.kr/m.htm?", "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            CResult decResult = this.EDBDec.EDB_DecodeData(data);
            if(decResult.nRet == 0) {
                CResult cBResult = this.EDBDec.EDB_GetPrescriptionCount("#B");
                for(int i=0 ; i<Integer.parseInt(cBResult.sRet) ; i++) {
                    CResult codesResult = EDBDec.EDB_SearchData("#B", i+1, 4);
                    if(codesResult.nRet == 0) {
                        parameter += "&codes=" + codesResult.sRet;
                    } else {
                        parameter = "";
                    }
                }
                CResult cCResult = this.EDBDec.EDB_GetPrescriptionCount("#C");
                for(int i=0 ; i<Integer.parseInt(cCResult.sRet) ; i++) {
                    CResult codesResult = EDBDec.EDB_SearchData("#C", i+1, 4);
                    if(codesResult.nRet == 0) {
                        parameter += "&codes=" + codesResult.sRet;
                    } else {
                        parameter = "";
                    }
                }
            }
            // TODO 리턴되는 에러 메시지에 따른 처리 구현 필요(김용기)
            else {
                List<String> list = decoder.exportDrugcodes(data);

                if(!list.isEmpty()) {
                    for(String code : list) {
                        parameter += "&codes=" + code;
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);
        }
        return parameter;
    }

    private class UserChecker extends AsyncTask<Void, Void, CResult> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            this.progress = new ProgressDialog(context);
            this.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progress.setMessage("Loading...");
            this.progress.show();
        }

        @Override
        protected void onPostExecute(CResult result) {
            if(this.progress != null && this.progress.isShowing()) {
                this.progress.dismiss();
                this.progress = null;
            }

            if(result != null) {
                int resultCode = result.nRet;
                if(resultCode == 0) {
                    medicinenumber.setText(result.sRet);
                } else {
                    medicinenumber.setText("error : "+resultCode+","+result.sRet);
                }
            }
        }

        @Override
        protected CResult doInBackground(Void... params) {
            if(EDBDec == null) {
                EDBDec = new EDBBpDec(context);
            }
            return EDBDec.EDB_CheckUser("edb_cbnuh", "edbpass_cbnuh", "40");

        }
    }
}
