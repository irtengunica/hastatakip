package com.example.okul.hastatakip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MainActivity extends ActionBarActivity {
    SharedPreferences preferences;
    public static String URL = "http://turulay.com/kombiisim42.php";//Bilgisayarýn IP adresi
    public static String URLdegerler = "http://turulay.com/hastadegerler42.php";//Bilgisayarýn IP adresi
    Boolean renkdegis=true;
    int sayac=0;
    Boolean msjdurum=true;
    String CihazID,CihazAdi;
    String durum="0";
    int degeral;
    int degeralond;
    String degeral2;
    SwipeRefreshLayout swipeLayout;
    final Context context = this;
    boolean internetBaglantisiVarMi() {

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() != null

                && conMgr.getActiveNetworkInfo().isAvailable()

                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;

        } else {

            return false;

        }

    }
    public void mesajpencerefonk(String sdegeral,final int idegeral){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.mesajfrm, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        final TextView mesajtxt=(TextView) promptView.findViewById(R.id.mesajtxt);
        mesajtxt.setText(sdegeral);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setNegativeButton("Ýptal", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        durum="0";
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swview);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                        String durum="0";
                        threadcalistir();
                        threadcalistir2();
                        /*LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.numberpickeralert, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setView(promptView);
                        AlertDialog alertD = alertDialogBuilder.create();

                        alertD.show();*/


                        // burada ise Swipe Reflesh olduðunda ne yapacaksanýz onu eklemeniz yeterlidir. Örneðin bir listeyi clear edebilir yada yeniden veri doldurabilirsiniz.
                    }
                }, 2000);
            }
        });

        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //Button ayarlarbtn=(Button) findViewById(R.id.ayarlarbtn);
        Button anlikbt=(Button) findViewById(R.id.anlikbtn);
        Button guncelle=(Button) findViewById(R.id.guncelle);
        Button cihazID = (Button) findViewById(R.id.cihazID);
        Button cihazad = (Button) findViewById(R.id.cihazad);
        final Button simdikiderece = (Button) findViewById(R.id.simdikiderece);
        Button guntarih = (Button) findViewById(R.id.guntarih);
        final Button kombidurumu=(Button) findViewById(R.id.kombidurumu);
        //final Switch sistemdurumusw=(Switch) findViewById(R.id.sistemdurumusw);
        TextView mesaj1=(TextView) findViewById(R.id.mesaj1);
        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        CihazID=preferences.getString("CihazID", "1001");
        CihazAdi=preferences.getString("CihazAdi", "Kombi");
        durum="0";

        threadcalistir();
        threadcalistir2();
        simdikiderece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durum = "0";
                //mesajpencerefonk("Anlýk Oda Sýcaklýðýnýzý verir."+simdikiderece.getText().toString()+" santigrat derecedir.",0);
                threadcalistir();
            }
        });
        kombidurumu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durum = "0";
                //mesajpencerefonk("Kombinizin Sizin Ayarladýðýnýz  Sýcaklýk Durumuna Göre Açýk/Kapalý Bilgisini verir."+kombidurumu.getText().toString(),0);
                threadcalistir();
            }
        });
        cihazID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durum = "0";
                threadcalistir();
                Intent i=new Intent(getApplicationContext(),ayarlarfrm.class);
                startActivity(i);

            }
        });
        cihazad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durum = "0";
                threadcalistir();
                Intent i=new Intent(getApplicationContext(),ayarlarfrm.class);
                startActivity(i);
            }
        });
        guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durum = "0";
                threadcalistir();
                threadcalistir2();
            }
        });

        LineChart lineChart = (LineChart) findViewById(R.id.chart);
        LineChart lineChart2 = (LineChart) findViewById(R.id.chart2);

        ArrayList<Entry> nabizd = new ArrayList<>();
        nabizd.add(new Entry(84f,1));
        nabizd.add(new Entry(80f, 2));
        nabizd.add(new Entry(80f, 3));
        nabizd.add(new Entry(80f, 4));
        nabizd.add(new Entry(84f, 5));
        nabizd.add(new Entry(85f, 6));
        ArrayList<Entry> atesd = new ArrayList<>();
        atesd.add(new Entry(37f, 1));
        atesd.add(new Entry(36.5f, 2));
        atesd.add(new Entry(36.7f, 3));
        atesd.add(new Entry(37.1f, 4));
        atesd.add(new Entry(38f, 5));
        atesd.add(new Entry(39f, 6));

        LineDataSet dataset = new LineDataSet(nabizd, "# Hasta Nabýz Grafiði");
        LineDataSet dataset2 = new LineDataSet(atesd, "# Hasta Ateþ Grafiði");
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("saat1");
        labels.add("saat2");
        labels.add("saat3");
        labels.add("saat4");
        labels.add("saat5");
        labels.add("saat6");

        LineData data = new LineData(labels, dataset);
        LineData data2 = new LineData(labels, dataset2);

        dataset.setColors(ColorTemplate.PASTEL_COLORS); //
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);
        dataset2.setColors(ColorTemplate.PASTEL_COLORS); //
        dataset2.setDrawCubic(true);
        dataset2.setDrawFilled(true);

        lineChart.setData(data);
        lineChart2.setData(data2);
        lineChart2.animateY(5000);
        lineChart.animateY(5000);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                Toast.makeText(this, "Program Ön Ayarlarý", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getApplicationContext(),ayarlarfrm.class);
                startActivity(i);
                break;
            case R.id.item2:
                Toast.makeText(this,"Tekrar Bekleriz.",Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);


            //noinspection SimplifiableIfStatement
            //if (id == R.id.action_settings) {
            //return true;
        }

        //return super.onOptionsItemSelected(item);
        return true;
    }
    public static String connect(String url,String CihazID,String durum){
        HttpClient httpClient=new DefaultHttpClient();
        //HttpGet httpget = new HttpGet(url);
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=UTF-8");
        httppost.addHeader("User-Agent", "Mozilla/4.0");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cihazID", CihazID));
        params.add(new BasicNameValuePair("durum", durum));
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

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                String ret = connect(params[0],CihazID,durum);
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
                parseJson(result);
                /*TextView mesaj1 = (TextView) findViewById(R.id.mesaj1);
                mesaj1.setText(result.toString());
                mesaj1.setTextColor(Color.RED);*/
            } else {
                /*TextView mesaj1 = (TextView) findViewById(R.id.mesaj1);
                mesaj1.setText("Kayýt Bulunamadý");
                mesaj1.setTextColor(Color.RED);*/
                Toast.makeText(getApplicationContext(), "Sistemden Herhangi bir bilgi gelmedi.",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
    class fetchJsonTask2 extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                String ret = connect(params[0],CihazID,durum);
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
                parseJson2(result);
                /*TextView mesaj1 = (TextView) findViewById(R.id.mesaj1);
                mesaj1.setText(result.toString());
                mesaj1.setTextColor(Color.RED);*/
            } else {
                /*TextView mesaj1 = (TextView) findViewById(R.id.mesaj1);
                mesaj1.setText("Kayýt Bulunamadý");
                mesaj1.setTextColor(Color.RED);*/
                Toast.makeText(getApplicationContext(), "Sistemden Herhangi bir bilgi gelmedi.",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
    public void parseJson2(JSONObject ogrenciJson) {
        //Button saatsec11txt=(Button)findViewById(R.id.saatsec11);
        String guncellemevarmi;
        float nabiz0,nabiz1,nabiz2,nabiz3,nabiz4,nabiz5,ates0,ates1,ates2,ates3,ates4,ates5;
        LineChart lineChart = (LineChart) findViewById(R.id.chart);
        LineChart lineChart2 = (LineChart) findViewById(R.id.chart2);


        System.out.println(ogrenciJson);
        try {
            nabiz0=Float.parseFloat(ogrenciJson.getString("nabiz0"));
            nabiz1=Float.parseFloat(ogrenciJson.getString("nabiz1"));
            nabiz2=Float.parseFloat(ogrenciJson.getString("nabiz2"));
            nabiz3=Float.parseFloat(ogrenciJson.getString("nabiz3"));
            nabiz4=Float.parseFloat(ogrenciJson.getString("nabiz4"));
            nabiz5=Float.parseFloat(ogrenciJson.getString("nabiz5"));
            ates0=Float.parseFloat(ogrenciJson.getString("ates0"))/10;
            ates1=Float.parseFloat(ogrenciJson.getString("ates1"))/10;
            ates2=Float.parseFloat(ogrenciJson.getString("ates2"))/10;
            ates3=Float.parseFloat(ogrenciJson.getString("ates3"))/10;
            ates4=Float.parseFloat(ogrenciJson.getString("ates4"))/10;
            ates5=Float.parseFloat(ogrenciJson.getString("ates5"))/10;
            ArrayList<Entry> nabizd = new ArrayList<>();
            nabizd.add(new Entry(nabiz0, 1));
            nabizd.add(new Entry(nabiz1, 2));
            nabizd.add(new Entry(nabiz2, 3));
            nabizd.add(new Entry(nabiz3, 4));
            nabizd.add(new Entry(nabiz4, 5));
            nabizd.add(new Entry(nabiz5, 6));
            ArrayList<Entry> atesd = new ArrayList<>();
            atesd.add(new Entry(ates0, 1));
            atesd.add(new Entry(ates1, 2));
            atesd.add(new Entry(ates2, 3));
            atesd.add(new Entry(ates3, 4));
            atesd.add(new Entry(ates4, 5));
            atesd.add(new Entry(ates5, 6));

            LineDataSet dataset = new LineDataSet(nabizd, "# Hasta Nabýz Grafiði");
            LineDataSet dataset2 = new LineDataSet(atesd, "# Hasta Ateþ Grafiði");
            ArrayList<String> labels = new ArrayList<String>();
            labels.add(ogrenciJson.getString("saat0"));
            labels.add(ogrenciJson.getString("saat1"));
            labels.add(ogrenciJson.getString("saat2"));
            labels.add(ogrenciJson.getString("saat3"));
            labels.add(ogrenciJson.getString("saat4"));
            labels.add(ogrenciJson.getString("saat5"));

            LineData data = new LineData(labels, dataset);
            LineData data2 = new LineData(labels, dataset2);

            dataset.setColors(ColorTemplate.PASTEL_COLORS); //
            dataset.setDrawCubic(true);
            dataset.setDrawFilled(true);
            dataset2.setColors(ColorTemplate.PASTEL_COLORS); //
            dataset2.setDrawCubic(true);
            dataset2.setDrawFilled(true);

            lineChart.setData(data);
            lineChart2.setData(data2);
            lineChart2.animateY(5000);
            lineChart.animateY(5000);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void parseJson(JSONObject ogrenciJson) {

        String guncellemevarmi;
        int kars1,kars2,pildurum,nrf,modunuz;

        Button cihazID = (Button) findViewById(R.id.cihazID);
        Button cihazad = (Button) findViewById(R.id.cihazad);
        Button simdikiderece = (Button) findViewById(R.id.simdikiderece);
        Button guntarih = (Button) findViewById(R.id.guntarih);
        Button kombidurumu=(Button) findViewById(R.id.kombidurumu);
        TextView ayarlanansicaklik=(TextView) findViewById(R.id.ayarsicaklik);

        TextView simdikisaat = (TextView) findViewById(R.id.simdikisaat);

        Integer kombidurums;

        float evsicakligi,ayarsicaklik;

        TextView cihazip = (TextView) findViewById(R.id.cihazip);
        TextView mesaj1 = (TextView) findViewById(R.id.mesaj1);
        Button anlikbtn=(Button) findViewById(R.id.anlikbtn);
        Button guncelle=(Button) findViewById(R.id.guncelle);
        //Switch sistemdurumusw=(Switch) findViewById(R.id.sistemdurumusw);
        //mesaj1.setText("");
        System.out.println(ogrenciJson);
        try {
            //saatsec11txt.setText(ogrenciJson.getString("saatsec1"));
            cihazID.setText("CihazID: "+ogrenciJson.getString("cihazID"));
            cihazad.setText("Cihaz Adý: "+ogrenciJson.getString("cihazad"));
            evsicakligi=Float.parseFloat(ogrenciJson.getString("simdikiderece"));
            evsicakligi=evsicakligi/10;
            if(evsicakligi<=1){
                //mesaj1.setText("Sýcaklýk sensöründe bir sorun var. Lütfen Pilleri Kontrol ediniz.");
                //mesaj1.setTextColor(Color.RED);
                mesajpencerefonk("Sýcaklýk sensöründe bir sorun var. Lütfen Pilleri Kontrol ediniz.",0);
            }
            simdikiderece.setText("Hastanýn Ateþi: " + String.valueOf(evsicakligi));

            //simdikiderece.setText("Ev Sýcaklýðý: "+ogrenciJson.getString("simdikiderece"));
            // ayarsicaklik=Float.parseFloat(ogrenciJson.getString("ayarsicaklik"));
            ayarsicaklik=Integer.valueOf(ogrenciJson.getString("nrf"));
            ayarlanansicaklik.setText("Hastanýn Nabzý: "+String.valueOf(ayarsicaklik));
            anlikbtn.setText("Hastanýn Nabzý: "+String.valueOf(ayarsicaklik));
            kombidurums=Integer.parseInt(ogrenciJson.getString("kombidurum"));
            if(kombidurums==0)
            {
                kombidurumu.setText("Cihaz Durumu: Kapalý");
            }
            else
            {
                kombidurumu.setText("Cihaz Durumu: Açýk");
            }
            simdikisaat.setText("Cihaz Saati: "+ogrenciJson.getString("simdikisaat"));
            //cihazip.setText("Kombinin Að Adresi: "+ogrenciJson.getString("ipno")+ ogrenciJson.getString("modunuz"));
            guncellemevarmi="Güncelle: " + ogrenciJson.getString("tarih");
            sayac++;
            if(guntarih.getText().toString().length()>=29) {
                //mesaj1.setText(guncellemevarmi.substring(29, 31) + guntarih.getText().toString().substring(29, 31));
                kars1 =Integer.valueOf(guntarih.getText().toString().substring(27, 29));
                kars2 =Integer.valueOf(guncellemevarmi.substring(27, 29));

                if (kars1== kars2) {
                    if (sayac >= 10) {
                        //mesaj1.setText("Kombi Cihazýnýz Sisteme Bilgi Göndermiyor! Lütfen cihazý Kontrol ediniz.");
                        //mesaj1.setTextColor(Color.RED);
                        mesajpencerefonk("Cihazýnýz Sisteme Bilgi Göndermiyor! Lütfen cihazý Kontrol ediniz.",0);
                        sayac = 1;
                    }
                }else{
                    sayac=1;
                }

            }
            if(renkdegis) {
                guntarih.setTextColor(Color.RED);
                guncelle.setTextColor(Color.RED);
                renkdegis=false;
            }else{
                guntarih.setTextColor(Color.BLUE);
                guncelle.setTextColor(Color.BLUE);
                renkdegis = true;
            }
            guntarih.setText(guncellemevarmi);
            guncelle.setText(guncellemevarmi);

            pildurum=Integer.valueOf(ogrenciJson.getString("pil"));
            nrf=Integer.valueOf(ogrenciJson.getString("nrf"));
            modunuz=Integer.valueOf(ogrenciJson.getString("modunuz"));
            //mesaj1.setText(ogrenciJson.getString("pil")+ogrenciJson.getString("nrf")+ogrenciJson.getString("modunuz")+String.valueOf(msjdurum)+String.valueOf(sayac));
            cihazip.setText(ogrenciJson.getString("ipno")+ " nolu IP adresinde  çalýþmaktadýr.");
            /*mesaj1.setText(ogrenciJson.getString("modunuz"));

            if (modunuz == 0) {
                ///fonk ekle
                if (msjdurum) {
                    mesajpencerefonk("Sistem Sizin Ayarladýðýnýz Günlük Programda Çalýþmaktadýr.", 0);
                }
                durum="0";
                cihazip.setText(ogrenciJson.getString("ipno")+ " nolu IP adresinde  çalýþmaktadýr.");
            }
            if (modunuz >= 1 && modunuz<=7 ) {
                ///fonk ekle
                if (msjdurum) {
                    mesajpencerefonk("Sistem Sizin Ayarladýðýnýz Haftalýk Programa Göre Çalýþmaktadýr.", 0);
                }
                cihazip.setText(ogrenciJson.getString("ipno")+ " nolu IP adresinde; HAFTALIK çalýþma modundadýr.");
            }

            if (modunuz == 8) {
                ///fonk ekle
                if (msjdurum) {
                    mesajpencerefonk("Sistem Sizin Ayarladýðýnýz Evde Yokum Modunda Çalýþmaktadýr.", 0);
                }
                cihazip.setText(ogrenciJson.getString("ipno")+ " nolu IP adresinde; EVDE YOKUM çalýþma modundadýr.");
            }
            if (modunuz == 9) {
                ///fonk ekle
                if (msjdurum) {
                    mesajpencerefonk("Sistem Sizin Ayarladýðýnýz Misafir Modunda Çalýþmaktadýr.", 0);
                }
                cihazip.setText(ogrenciJson.getString("ipno")+ " nolu IP adresinde; MÝSAFÝR çalýþma modundadýr.");
            }
            if (modunuz == 10) {
                ///fonk ekle
                if (msjdurum) {
                    mesajpencerefonk("Sistem Sizin Ayarladýðýnýz Hasta Var Modunda Çalýþmaktadýr.", 0);
                }
                cihazip.setText(ogrenciJson.getString("ipno")+ " nolu IP adresinde; HASTA VAR çalýþma modundadýr.");
            }
            if (modunuz == 11) {
                ///fonk ekle
                if (msjdurum) {
                    mesajpencerefonk("Sistem Sizin Kapalý Modunda Çalýþmaktadýr.", 0);
                }
                cihazip.setText(ogrenciJson.getString("ipno")+ " nolu IP adresinde; KAPALI çalýþma modundadýr.");
            }*/
            if (msjdurum) {
                /*if (nrf <= 50) {
                    ///fonk ekle
                    mesajpencerefonk("Sýcaklýk Sensörü kombiye çok uzakta. Lütfen Daha Yakýna Getiriniz", 0);
                }*/

                if (pildurum <= 30) {
                    ///fonk ekle
                    mesajpencerefonk("Sýcaklýk Sensörü Pilini Deðiþtirmeniz Gerekiyor. Pil Durumu %" + ogrenciJson.getString("pil"), 0);
                }
            }

            if(sayac%5==0){
                //sayac=1;
                msjdurum=true;
            }else{
                msjdurum=false;
            }
            /*
            if (modunuz==11){
                sistemdurumusw.setChecked(false);
            }else{
                sistemdurumusw.setChecked(true);
            }
            if(sistemdurumusw.isChecked()){
                sistemdurumusw.setText("Sitem Açýk");
            }else{
                sistemdurumusw.setText("Sistem Kapalý");
            }
            */


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public  void threadcalistir(){
        if (internetBaglantisiVarMi()) {
            //fetchJsonTask a = new fetchJsonTask();

            //a.execute(URL);
            Thread t6 = new Thread() {
                public void run() {

                    try {
                        //sleep(5000);
                        fetchJsonTask b = new fetchJsonTask();
                        Thread.sleep(1000);
                        b.execute(URL);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        //finish();
                    }

                }
                //public void finish(){

                //}


            };
            Toast.makeText(getApplicationContext(), "Güncelleniyor. Ýþlemin Tamamlanmasý Ýçin 5 Saniye Bekleyiniz.", Toast.LENGTH_SHORT).show();
            t6.start();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Baðlantýnýz yok", Toast.LENGTH_SHORT).show();
        }
    }
    public  void threadcalistir2(){
        if (internetBaglantisiVarMi()) {
            //fetchJsonTask a = new fetchJsonTask();

            //a.execute(URL);
            Thread t6 = new Thread() {
                public void run() {

                    try {
                        //sleep(5000);
                        fetchJsonTask2 b = new fetchJsonTask2();
                        Thread.sleep(1000);
                        b.execute(URLdegerler);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        //finish();
                    }

                }
                //public void finish(){

                //}


            };
            Toast.makeText(getApplicationContext(), "Güncelleniyor. Ýþlemin Tamamlanmasý Ýçin 5 Saniye Bekleyiniz.", Toast.LENGTH_SHORT).show();
            t6.start();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Baðlantýnýz yok", Toast.LENGTH_SHORT).show();
        }
    }
}
