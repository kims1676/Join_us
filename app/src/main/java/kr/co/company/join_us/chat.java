package kr.co.company.join_us;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by tofot_000 on 2016-11-26.
 */

public class chat extends Activity implements OnMapReadyCallback{


    static final LatLng SUWON = new LatLng(37.24, 127.18);
    private GoogleMap googleMap;

    String number;
    String kind;
    String location; // 왠지 있어야 할것같음, 여기다가 위치 저장해뒀다 다 뿌려야 하지 안을까??

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            number = bundle.getString("num");//이제 이번호를 가지고 디비로 보내서 여기에 맞는 댓글하고 글의 내용을 가지고 와야함!! 두개 따로따로!!
            kind = bundle.getString("종목");
            Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
        }

        MapFragment mapFragment =(MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        Marker suwon = googleMap.addMarker(new MarkerOptions().position(SUWON)
                .title("Suwon"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SUWON));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }


    public void onBackPressed(){
        //목록 전 페이지 인텐트에서 받아 와야 함 그래야지 여기서 다시 돌아갈때 쓸수 있음
        Intent intent=new Intent(chat.this,document.class);
        intent.putExtra("kind",kind);
        intent.putExtra("번호",number);
        startActivity(intent);
        finish();
    }



}
