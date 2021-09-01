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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by typebreaker on 2017-08-15.
 */

public class HttpUtil extends AsyncTask<String, List<String>, List<String>> {

    @Override
    protected List<String> doInBackground(String... params) {
        URL url = null;
        List<String> result = new ArrayList<String>();
        String page = "";
        try {
            url = new URL(params[0]);
            HttpURLConnection connector = (HttpURLConnection) url.openConnection();
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
            result.add(json.getString("resultType"));
            if(result.get(0).equals("LOGIN_SUCCESS"))
                result.add(json.getString("userName"));
            connector.disconnect();
        } catch (IOException e) {
            result.add("네트워크 상태가 좋지 않아, 잠시후 시도해주시기 바랍니다.");
        } catch (JSONException e) {
            result.add("잘못된 접근입니다.");
        }
        return result;
    }

}