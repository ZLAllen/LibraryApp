package com.example.zhaoqi.libraryapp;

import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zhaoqi on 5/14/18.
 */

public class BookRetrieve extends AsyncTask<String, Void, Boolean> {

    private final String USER_AGENT = "Mozilla/5.0";
    private String barcode;
    private EditText text1;
    private EditText text2;
    private TextView text3;


    public BookRetrieve(String barcode, EditText text1, EditText text2, TextView text3){
        this.barcode = barcode;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(String... urls) {
        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:"+ barcode+ "&jscmd=data&format=json";
        try{
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
            JSONObject json = new JSONObject(response.toString());
            String author = json.getJSONObject("ISBN:"+barcode).getJSONArray("authors").getJSONObject(0).getString("name");
            String title = json.getJSONObject("ISBN:"+barcode).getString("title");
            String cover = json.getJSONObject("ISBN:"+barcode).getJSONObject("cover").getString("small");
            text1.setText(title);
            text2.setText(author);
            text3.setText(cover);

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {

    }

}
