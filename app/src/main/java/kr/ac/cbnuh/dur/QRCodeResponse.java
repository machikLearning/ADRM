package kr.ac.cbnuh.dur;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by moodeath on 2017-09-29.
 */

public class QRCodeResponse extends AsyncTask<String, List<String>, String> {
    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        String result = "";
        String page = "";
        HttpURLConnection connector = null;
        try {
            url = new URL(params[0]);
            connector = (HttpURLConnection) url.openConnection();

            connector.setConnectTimeout(5000);
            connector.setReadTimeout(5000);

            connector.setDoInput(true);
            connector.setDoOutput(true);
            connector.setRequestMethod("POST");

            connector.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connector.getOutputStream(), "UTF-8"));

            writer.write(params[1]);
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connector.getInputStream(),"UTF-8"));
            String line = null;
            page = "";
            while((line = reader.readLine()) != null){
                page += line;
            }
            JSONObject json = new JSONObject(page);
            if(!json.isNull("message")) {
                result =json.getString("message");
            } else {
                result = "통신 에러입니다. 다시 시도해 주세요.";
            }
        } catch (IOException e) {
            result = "네트워크 상태가 좋지 않아, 잠시후 시도해주시기 바랍니다.";
        } catch (JSONException e) {
            result = "잘못된 접근입니다.";
        } finally {
            if(connector != null)
                connector.disconnect();
        }
        return result;
    }
}
