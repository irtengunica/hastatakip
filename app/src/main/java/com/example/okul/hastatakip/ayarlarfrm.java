package com.example.okul.hastatakip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.apache.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by okul on 8.4.2016.
 */
public class ayarlarfrm extends ActionBarActivity {
    //ListView lv;
    TextView tekdeger;
    WifiManager wifi;
    WifiManager wifi2;
    String wifis[];
    WifiScanReceiver wifiReciever;
    int degerbul=-1;
    Spinner spinner;
    TextView textView2;
    Button OnayButton;
    EditText editcihazid;
    EditText editcihazadi;
    EditText editAgadi;
    EditText editsPass;

    SharedPreferences preferences;
    //preferences için bir nesne tanýmlýyorum.
    SharedPreferences.Editor editor;
    public static String URL = "http://192.168.4.1";
    //List<NameValuePair> params1 = new ArrayList<NameValuePair>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayarlarfrm);
        //lv=(ListView)findViewById(R.id.listView);
        //WebView webview = (WebView) findViewById(R.id.webview);
        //webview.getSettings().setJavaScriptEnabled(true);
        //webview.loadUrl(URL);
        tekdeger=(TextView) findViewById(R.id.textview);
        spinner=(Spinner)findViewById(R.id.spinner);
        textView2=(TextView)findViewById(R.id.textView2);
        editcihazid=(EditText) findViewById(R.id.editcihazid);
        editcihazadi=(EditText) findViewById(R.id.editcihazadi);
        editAgadi=(EditText) findViewById(R.id.editAgadi);
        editsPass=(EditText) findViewById(R.id.editsPass);
        OnayButton=(Button) findViewById(R.id.OnayButton);
        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
        String CihazID=preferences.getString("CihazID", "1001");
        editcihazid.setText(CihazID);
        String CihazAdi=preferences.getString("CihazAdi", "Kombi");
        editcihazadi.setText(CihazAdi);
        String AgAdi=preferences.getString("AgAdi", "Að Adý Gir");
        editAgadi.setText(AgAdi.trim());
        String AgSifresi=preferences.getString("AgSifresi", "Að Þifresi Gir");
        editsPass.setText(AgSifresi.trim());
        wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        wifi.startScan();
        //if(degerbul!=-1) {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", "Turulay_K1");
            wifiConfig.preSharedKey = String.format("\"%s\"", "12345678");
            //WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            int netId = wifi.addNetwork(wifiConfig);
            wifi.disconnect();
            wifi.enableNetwork(netId, true);
            wifi.reconnect();
       // }

        OnayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("CihazID", editcihazid.getText().toString());
                editor.putString("CihazAdi", editcihazadi.getText().toString());
                editor.putString("AgAdi", editAgadi.getText().toString());
                editor.putString("AgSifresi", editsPass.getText().toString());
                editor.commit();

                /*params1.add(new BasicNameValuePair("ssid", editAgadi.getText().toString()));
                params1.add(new BasicNameValuePair("pwd", editsPass.getText().toString()));*/
                /*params1.add(new BasicNameValuePair("ssid", "NetArBer7980"));
                params1.add(new BasicNameValuePair("pwd", "13052006ardaberat"));*/
                //params1.add(new BasicNameValuePair("pwmi5", "Kaydet"));
                /*Thread t1=new Thread()
                {
                    public  void run()
                    {

                        try {
                            sleep(100);
                            fetchJsonTask a = new fetchJsonTask();
                            a.execute(URL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            //finish();
                        }

                    }
                };
                t1.start();*/
                fetchJsonTask a = new fetchJsonTask();
                a.execute(URL);
                finish();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String[] dersDizisi = getResources().getStringArray(R.array.dersler);
                //textView2.setText(wifis[position] + " aðýný seçtiniz!");

                editAgadi.setText(wifis[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }
    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            wifis = new String[wifiScanList.size()];
            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).toString());
                try {
                    if(wifis[i].contains("Turulay_K1")){
                        degerbul=i;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(wifis[i].contains("SSID:")){
                    //wifis[i]=wifis[i].substring(wifis[i].indexOf("SSID:"), wifis[i].indexOf("BSSID:")-1);
                    try {
                        wifis[i]=wifis[i].substring(6, wifis[i].indexOf("BSSID:")-2);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                //myString.substring(myString.indexOf("dunya"), myString.length())
                //

            }

            //lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, wifis));
            spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, wifis));
            if(degerbul!=-1){
                //tekdeger.setText(wifis[degerbul]);
                tekdeger.setText("Cihaz Ön Ayarlarýný Yapýnýz");

            }else{
                tekdeger.setText("Kablosuz aðlarýn listesinde aranýlan að bulunamadý");
            }



        }
    }

    public static String connect(String url, String ssid1, String pwd1){
        HttpClient httpClient=new DefaultHttpClient();
        //HttpGet httpget = new HttpGet(url);
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=UTF-8");
        httppost.addHeader("User-Agent", "Mozilla/4.0");
        httppost.addHeader("Accept-Language", "tr-TR");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ssid", ssid1));
        params.add(new BasicNameValuePair("pwd", pwd1));
        params.add(new BasicNameValuePair("pwmi5", "Kaydet"));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse response;
        try {
            response=httpClient.execute(httppost);
            HttpEntity entity=response.getEntity();
            if(entity!=null){
                InputStream instream=entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        instream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return sb.toString();
            }
        } catch (Exception e) {
        }
        return null;
    }
    class fetchJsonTask extends AsyncTask<String, Integer, JSONObject> {
        public String ssid=editAgadi.getText().toString();
        public String pwd=editsPass.getText().toString();

        @Override
        protected JSONObject doInBackground(String... params) {

            try {

                String ret = connect(params[0],ssid,pwd);
                ret = ret.trim();
                JSONObject jsonObj = new JSONObject(ret);
                return jsonObj;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                //parseJson(result);
            } else {
                //TextView tv = (TextView) findViewById(R.id.textView1);
                // tv.setText("Kayýt Bulunamadý");
                //tv.setTextColor(Color.RED);
                Toast.makeText(getApplicationContext(), "Sistemden Herhangi bir bilgi gelmedi.",
                        Toast.LENGTH_LONG).show();
            }
            //WifiConfiguration wifiConfig = new WifiConfiguration();
            //wifiConfig.SSID = String.format("\"%s\"", editAgadi.getText().toString());
            //wifiConfig.preSharedKey = String.format("\"%s\"", editsPass.getText().toString());
            //WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            //int netId = wifi.addNetwork(wifiConfig);
            //wifi.disconnect();
            //wifi.enableNetwork(netId, true);
            //wifi.reconnect();

        }
    }

}
