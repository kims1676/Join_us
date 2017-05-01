package kr.co.company.join_us;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by tofot_000 on 2016-11-18.
 */

public class document extends AppCompatActivity  implements View.OnClickListener{



    Button buttoncancel;
    Button buttonchat;
    Button buttondocu;

    String number;// 글 번호 들어갈 스트링
    String docu_id;
    String docu_title;
    String docu_content;
    String docu_date;
    String kind;

    TextView docuid;
    TextView docutitle;
    TextView docucontent;//내용은 디비에서 가지고 와야함!!!
    TextView docudate;


    TextView commenid;
    TextView commendate;
    TextView commenreply;

    private ListView mainlistview2;
   // private List<global> Uploadlist;
    TextView emptyview;
    document.UploadListAdapter adapter2;

    private GpsInfo gps;
    String latitude;
    String longitude;
    private ProgressDialog pDialog;





    //private ListView doculistview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        docucontent=(TextView)findViewById(R.id.docu_content);
        docuid=(TextView)findViewById(R.id.docu_id);
        docudate=(TextView)findViewById(R.id.docu_date);
        docutitle=(TextView)findViewById(R.id.docu_title);

        buttoncancel=(Button)findViewById(R.id.buttoncancel);
        buttonchat=(Button)findViewById(R.id.buttonchat);
        buttondocu=(Button)findViewById(R.id.buttondocu);
        buttondocu.setOnClickListener(this);
        buttonchat.setOnClickListener(this);
        buttoncancel.setOnClickListener(this);

        emptyview = (TextView) findViewById(R.id.docu_empty);
        mainlistview2 = (ListView) findViewById(R.id.commentlist);



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            number = bundle.getString("번호");//이제 이번호를 가지고 디비로 보내서 여기에 맞는 댓글하고 글의 내용을 가지고 와야함!! 두개 따로따로!!
            kind = bundle.getString("kind");
           /* Toast.makeText(this, number, Toast.LENGTH_SHORT).show();*/


        }

        new document.JSONTask().execute("http://202.30.23.51/~sap16t10/document.php");//댓글 가지고 오기?
        //new document.JSONTask2().execute("http://202.30.23.51/~sap16t10/reply.php");//글 전체 가지고 오기! , 하나로 다 끝내기 했는데 왜이러냐

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onBackPressed(){
        //목록 전 페이지 인텐트에서 받아 와야 함 그래야지 여기서 다시 돌아갈때 쓸수 있음
        Intent intent=new Intent(document.this,board_pan.class);
        intent.putExtra("종목",kind);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttondocu:
                // Toast.makeText(this, "클릭됨됨됨", Toast.LENGTH_SHORT).show();
                Intent intent33 = new Intent(document.this, reply.class);
                intent33.putExtra("num", number);
                intent33.putExtra("종목", kind);
                startActivity(intent33);
                finish();

                break;
            case R.id.buttonchat:
                Intent intent44 = new Intent(document.this, chat.class);
                intent44.putExtra("num", number);
                intent44.putExtra("종목", kind);
                startActivity(intent44);
                finish();

                break;

            case R.id.buttoncancel:// 참여하기 버튼임 디비에 위도,경도,id 저장 시켜야함 이 번호의 글 번호를 가지고!!! 글번호는 number 임 나머지는

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                0);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }




                String id = GlobalApplication.getString(getApplicationContext(), "userid").toString();//이 아이디가 디비에 저장되어야할 아이디임
                //위치도 찍어보자 !!
                Log.d("abc","\nididid:"+id);
                Log.d("abc","\nnumber:"+number);
                gps = new GpsInfo(document.this);
                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {

                    Log.d("abc","lati"+gps.getLatitude());
                    Log.d("abc","long"+gps.getLongitude());

                    double latitude1 = gps.getLatitude();
                    double longitude1 = gps.getLongitude();

                    latitude=String.valueOf(latitude1);
                    longitude=String.valueOf(longitude1);

                    Log.d("abc","latitude:"+latitude);
                    Log.d("abc","longitude"+longitude);

//                    Toast.makeText(gps, "당신의 위치 - \n위도: " + gps.getLatitude() + "\n경도: " + gps.getLongitude(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),
                            "당신의 위치 - \n위도: " + gps.getLatitude() + "\n경도: " + gps.getLongitude(),
                            Toast.LENGTH_LONG).show();
                    //사용한것인데 왜 찍혀서 나오는것은 0,0 인가??? 결국 리턴값이 null 이라는 것인덷????????????

                }

                else
                {
                    // GPS 를 사용할수 없으므로 여기로 넘어오진 않음!!
                    gps.showSettingsAlert();
                }
                // 여기서 디비에 id  number longitude latitude
                locationStore(id,number,longitude,latitude);

                    break;
                }

    }

    public class JSONTask extends AsyncTask<String, String, List<global>> {

        @Override
        protected List<global> doInBackground(String... params) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("num", number));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(parameter, "UTF-8"));
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try{
                HttpResponse response = httpclient.execute(httppost);

                if (response != null) {
                    InputStream is = response.getEntity().getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuffer buffer = new StringBuffer();


                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                        //Toast.makeText(document.this, buffer.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("abc","buffer="+buffer.toString());
                    }
                    //결국 가지고 온걸 buffer에 다 넣은건데

                    String finalJson = buffer.toString();//버퍼에 다 들어온것을 finalJson에 넣은건데 오브게트 5개 인데 마지막꺼는 다시 배열로 넣어야하는데..

                    JSONObject parentObject = new JSONObject(finalJson);
                    docu_id = parentObject.get("id").toString();
                    docu_title = parentObject.get("title").toString();
                    docu_content = parentObject.get("content").toString();
                    docu_date = parentObject.get("time").toString();

                    Log.d("abc","docu_id:"+docu_id);
                    Log.d("abc","docu_title:"+docu_title);
                    Log.d("abc","docu_content:"+docu_content);
                    Log.d("abc","docu_date:"+docu_date);





                    //JSONArray parentArray = parentObject.getJSONArray("document");//document로 해야댐*/ get방식 그냥 result로 보냈는데 이건 어케 해야하는거지??



                    JSONArray sonArray = parentObject.getJSONArray("comments");
                    Log.d("abc","\ncomment"+sonArray.toString());

                    List<global> uploadList = new ArrayList<>();


                    Gson gson = new Gson();


                    for (int i = 0; i < sonArray.length(); i++) {
                        JSONObject finalObject = sonArray.getJSONObject(i);

                        global menulist = gson.fromJson(finalObject.toString(), global.class);

                        uploadList.add(menulist);
                    }
                    return uploadList;

                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(final List<global> result) {
            super.onPostExecute(result);
            //adapter.notifyDataSetChanged();

            if(result!=null) {

                docuid.setText(docu_id);
                docutitle.setText(docu_title);
                docucontent.setText(docu_content);
                docudate.setText(docu_date);

                adapter2 = new document.UploadListAdapter(getApplicationContext(), R.layout.comment, result);
                mainlistview2.setAdapter(adapter2);

            } else {

                mainlistview2.setEmptyView(emptyview);
            }
        }
    }

    //@TargetApi(Build.VERSION_CODES.CUPCAKE)

    public class UploadListAdapter extends ArrayAdapter {
        private List<global> Uploadlist;
        private int resource;
        private LayoutInflater inflater;


        public UploadListAdapter(Context context, int resource, List<global> objects) {
            super(context, resource, objects);
            Uploadlist = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            ViewHolder2 holder2=null;

            if(convertView==null) {
                holder2 = new document.ViewHolder2();

                convertView = inflater.inflate(resource, null);

                holder2.commen_id = (TextView) convertView.findViewById(R.id.commen_id);
                holder2.commen_reply = (TextView) convertView.findViewById(R.id.commen_reply);
                holder2.commen_time = (TextView) convertView.findViewById(R.id.commen_date);

                convertView.setTag(holder2);
            }
            else{
                holder2=(ViewHolder2)convertView.getTag();
            }

            holder2.commen_id.setText(Uploadlist.get(position).getId());
            holder2.commen_reply.setText(Uploadlist.get(position).getReply());
            holder2.commen_time.setText(Uploadlist.get(position).getTime());

            return convertView;
        }
    }
    class ViewHolder2{

        private TextView commen_id;
        private TextView commen_reply;
        private TextView commen_time;

    }

    private void locationStore(final String id, final String number,final String latitude,final String longitude) {
        String tag_string_req = "storing_location";

        pDialog.setMessage("Ready...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, "http://202.30.23.51/~sap16t10/index.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다. 환영합니다!", Toast.LENGTH_SHORT).show();

                        String regMsg = jObj.getString("reg_msg");
                        Toast.makeText(getApplicationContext(),
                                regMsg, Toast.LENGTH_LONG).show();

                        // Launch login activity


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "location");
                params.put("id", id);
                params.put("number", number);
                params.put("latitude",latitude);
                params.put("longitude",longitude);

                return params;
            }
        };

        // Adding request to request queue
        GlobalApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}