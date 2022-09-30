package com.study.googlemapsandroidapiexample.Main_Page;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.study.googlemapsandroidapiexample.DB_conn;
import com.study.googlemapsandroidapiexample.Login_Page.Login_page_Activity;
import com.study.googlemapsandroidapiexample.Login_Page.Share_login_info;
import com.study.googlemapsandroidapiexample.Main_Page.CalendarDialog.Create_AlertDialog;
import com.study.googlemapsandroidapiexample.Main_Page.Order_sheet_item_list.Order_sheet_alert;
import com.study.googlemapsandroidapiexample.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

//로케이션리스너 생성 클래스
class Locationlistener implements LocationListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap           gmap, minimap;                  //구글 지도

    //나의 정보들4개, 다음 자판기까지의 거리
    private TextView            now_addr, now_speed,
                                vd_val  , next_vd_name,
                                next_vending;

    private Context             context;                        //MainActivity this
    private Get_set_package     get_set_package;                //내가 만든 getset패키지 함수!
    private Marker              closestMarker;                  //가장 가까운 마커(다음 가야할 자판기), 가장 가까운 자판기 temp
    private Menu                navi_menu;                      //drawerlayout의 menu
    private ListView            sc_lv;                          //오른쪽 밑에 표시되는 제품의 listview

    private ArrayList<Marker>   originMarkerlist;               //현재 표시되어있는 자판기들 배열
    private ArrayList<Marker>   vending_stack;                  //현재 롱클릭으로 가야할 자판기들이 저장되는 배열
    private ArrayList<Marker>   mini_stack;                     //현재 미닌맵의 롱클릭으로 가야할 자판기들이 저장되는 배열

    private String              before_snippet = "";            //db접속을 최소화 하기위한 String

    public  String              now_vd_id      = "";            //쇼트컷에 쓰이는 현재 자판기의 id

    private Location            lastlocation;                   //최초 한번만 update되는 함수가 호출되어
                                                                //생기는 에러를 방지하기위한 변수

    public  LatLng              japan_location;                 //일본버전에서 자신의 위치가 되는 변수

    //-----한번만 실행되게 하기위한 Boolean 변수들-----

    //국가 버전 정하기 / true = 일본 false = 한국
    private boolean             now_nation                              = true;

    //GPS추적 버튼 클릭시
    private boolean             location_button                         = false;

    //가장 가까운 자판기 추적 버튼 클릭 시
    private boolean             closest_vendingmachine_tracking_button  = false;

    //맨 처음에 카메라를 현재 위치로 잡았다면 이후는 잡지 않는다
    private boolean             camera_move_check                       = false;

    //미니맵에서 가장 가까운 자판기를 한번 표시 해줬으면 이후 중복 함수 실행 x
    private boolean             play_check                              = false;
    //-----------------------------------------------

    //아무 자판기도 찾지 못하였을경우 로딩창을 생성 하기위한 변수
    private boolean             refresh                                 = false;

    //서버 주소
    private String              url;

    //생성자
    public Locationlistener(Context context, ArrayList<Marker> originMarkerlist, ArrayList<Marker> vending_stack, ArrayList<Marker> mini_stack, GoogleMap gmap, GoogleMap minimap, ListView sc_lv, TextView next_vending, Get_set_package get_set_package, Menu navi_menu, String url) {

        this.originMarkerlist   = originMarkerlist;
        this.mini_stack         = mini_stack;
        this.gmap               = gmap;
        this.minimap            = minimap;
        this.sc_lv              = sc_lv;
        this.next_vending       = next_vending;
        this.context            = context;
        this.get_set_package    = get_set_package;
        this.navi_menu          = navi_menu;
        this.vending_stack      = vending_stack;
        this.url                = url;

        //나의 정보들이 출력될 공간들
        now_addr                = (TextView)((MainActivity) context).findViewById(R.id.now_addr);
        now_speed               = (TextView)((MainActivity) context).findViewById(R.id.now_speed);
        vd_val                  = (TextView)((MainActivity) context).findViewById(R.id.vd_val);
        next_vd_name            = (TextView)((MainActivity) context).findViewById(R.id.next_vd_name);

        //폰트 설정
        now_addr    .setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/YoonGothic750.ttf"));
        now_speed   .setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/YoonGothic750.ttf"));
        vd_val      .setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/YoonGothic750.ttf"));
        next_vd_name.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/YoonGothic750.ttf"));

        //일본버전의 나의 위치 지정하기
        japan_location = new LatLng(35.690695, 139.703262);
        //get_set클래스에서도 위치 초기화
        get_set_package.set_japan_location(japan_location);

        //맵이 전부 로딩된 이후 롱클릭 이벤트 활성화
        gmap.setOnMapLongClickListener(this);
        minimap.setOnMapLongClickListener(this);
    }

    //이전 snippet을 수정하는 함수
    public void setBefore_snippet(String before_snippet) {
        this.before_snippet = before_snippet;
    }

    //가장 가까운 자판기 초기화
    public void setClosestMarker() {
        closestMarker = null;
    }

    //마지막 현재위치 초기화
    public void setLastlocation() {
        lastlocation = null;
    }

    //프로그래머가 지정한 일정 시간 or 간 meter만큼 반복해서 실행되는 함수
    @Override
    public void onLocationChanged(Location location) {
        Log.e("<><>",context.getApplicationContext().getResources().getConfiguration().getLocales().get(0).getLanguage()+"");

        //다음 함수 호출까지 마커가 생성되지 않았다면 로딩 화면을띄워준다(마커가 생성될 때까지 반복)
        //첫번째 함수호출 시 에는 무시한다
        if (refresh == false) {
            refresh = true;
        }

        //두번째 함수 호출시 로딩화면 출력
        else if (refresh == true && originMarkerlist.size() < 2) {
            ((MainActivity) context).loading();
            next_vd_name.setText("");
            refresh = false;
        }

        Double lattitude;
        Double longitude;
        Double altitude;

        //일본 버전 일경우 일본 위치를 가져온다.
        if(now_nation){
            //위도 경도 고도
            lattitude = japan_location.latitude;
            longitude = japan_location.longitude;
            altitude = location.getAltitude();
        }

        //한국 버전 인 경우 실제 자신의 위치를 가져온다.
        else {
            //위도 경도 고도
            lattitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
        }

        //status에 저장될 msg
        String msg       = "";
        String addr      = "";

        //get_set_package가 null이아니면 + 일본 버전이라면
        if (get_set_package != null && now_nation) {
            //지정 일본 지명 가져오기
            addr = get_set_package.getAddress(japan_location, now_nation);
        }

        //get_set_package가 null이아니면 + 한국 버전이라면
        else if (get_set_package != null && !now_nation) {
            //현재 지명 가져오기
            addr = get_set_package.getAddress(new LatLng(lattitude, longitude), now_nation);
        }

        //예외처리 (팅김 방지)
        try {

            //스택 수량이 0이상인경우에만 실행을 한다(예외처리)
            if(vending_stack.size() > 0) {

                //스택 자판기가 특정 m안에 자판기가 없는경우
                if (vending_stack.size() < 1 ||
                        get_set_package.getmeter(originMarkerlist.get(0).getPosition(), vending_stack.get(0).getPosition()) > 443) {

                    //특정 m밖에 있다면 미니맵을 없앤다
                    if ((((Activity) context).findViewById(R.id.minimap_side)).getVisibility() == View.VISIBLE) {
                        ((MainActivity) context).findViewById(R.id.minimap_side).setVisibility(View.GONE);
                        ((MainActivity) context).findViewById(R.id.minimap_layout).setVisibility(View.GONE);
                    }

                }

                //스택 자판기가 특정 m안에 있는 경우
                else {
                    //화면이 비활성화 되어있으면 활성화 시킨다
                    if ((((Activity) context).findViewById(R.id.minimap_side)).getVisibility() == View.GONE &&
                            get_set_package.getmeter(originMarkerlist.get(0).getPosition(), vending_stack.get(0).getPosition()) > 0.1) {
                        ((MainActivity) context).findViewById(R.id.minimap_side).setVisibility(View.VISIBLE);
                        ((MainActivity) context).findViewById(R.id.minimap_layout).setVisibility(View.VISIBLE);
                    }

                }

            }

        }catch (Exception e){

        }

        //가장 가까운 자판기 추적 버튼 활성화시
        if (closest_vendingmachine_tracking_button) {

            //롱클릭 배열에 원소가 하나라도 있으면 전부 비운다
            if (vending_stack.size() > 0) {

                //롱클릭으로 지정한 마커들을 지운다
                for (int i = 0; i < vending_stack.size(); i++) {
                    vending_stack.get(i).remove();
                }

                //롱클릭 배열의 원소들을 비운다
                vending_stack.clear();

            }

            //현재위치와 가장 가까운 마커를 찾는 과정
            for (int i = 1; i < originMarkerlist.size(); i++) {

                //값이 하나도 안들어 가 있을경우 초기화 시켜준다.
                if (closestMarker == null) {
                    closestMarker = originMarkerlist.get(i);
                }

                //값이 들어가 있는경우 거리 비교
                else {
                    LatLng my_latlng = new LatLng(lattitude, longitude);
                    double sec_meter = get_set_package.getmeter(my_latlng, closestMarker.getPosition());
                    double fir_meter = get_set_package.getmeter(my_latlng, originMarkerlist.get(i).getPosition());

                    //거리가 더 짧은게 올 경우 가장 가까운 마커를 최신화 시켜준다.
                    if (sec_meter > fir_meter) {
                        closestMarker = originMarkerlist.get(i);
                    }
                }
            }

            //만약 보충할 자판기가 있다면 마커를 그린다
            if (originMarkerlist.size() > 1) {

                //다음 가야할 마커 그리기
                get_set_package.drawMarkers(closestMarker.getPosition(), closestMarker.getTitle(), closestMarker.getSnippet(), -1, false);

                //미니맵도 그린다
                get_set_package.draw_minimap_marker(closestMarker.getPosition(), closestMarker.getTitle(), closestMarker.getSnippet(), -1, false);

                //길 경로를 표시 해준다
                get_set_package.dir_fuc.call_Function(originMarkerlist.get(0).getPosition(), closestMarker.getPosition());

                //추적된 자판기가 특정 m안에 자판기가 있는경우
                if (vending_stack.size() > 0 &&
                        get_set_package.getmeter(originMarkerlist.get(0).getPosition(), closestMarker.getPosition()) < 443) {

                    //미니맵을 보이게 한다. -> 왼쪽 위 레이아웃
                    if ((((Activity) context).findViewById(R.id.minimap_side)).getVisibility() == View.GONE) {
                        ((MainActivity) context).findViewById(R.id.minimap_side).setVisibility(View.VISIBLE);
                        ((MainActivity) context).findViewById(R.id.minimap_layout).setVisibility(View.VISIBLE);
                    }

                    //보충완료,닫기버튼을 보이게 한다.(강제형변환으로 액티비티를 접근한다. ->이게 최선이에요 ㅠㅠ)
                    //순서대로 : 열기버튼,sortcutlayout,확인버튼,닫기버튼
                    if ((((Activity) context).findViewById(R.id.open_button)) .getVisibility() == View.GONE) {
                        (((Activity) context).findViewById(R.id.sc_layout))   .setVisibility(View.VISIBLE);
                        (((Activity) context).findViewById(R.id.ok_button))   .setVisibility(View.VISIBLE);
                        (((Activity) context).findViewById(R.id.close_button)).setVisibility(View.VISIBLE);
                    }

                    //다음가야할 정보 출력공간을 보이게 한다. -> 오른쪽 맨위 layout
                    if ((((Activity) context).findViewById(R.id.next_vending_layout)).getVisibility() == View.GONE) {
                        (((Activity) context).findViewById(R.id.next_vending_layout)).setVisibility(View.VISIBLE);
                    }


                }else{

                    //미니맵을 보이게 한다. -> 왼쪽 위 레이아웃
                    if ((((Activity) context).findViewById(R.id.minimap_side)).getVisibility() == View.VISIBLE) {
                        ((MainActivity) context).findViewById(R.id.minimap_side).setVisibility(View.GONE);
                        ((MainActivity) context).findViewById(R.id.minimap_layout).setVisibility(View.GONE);
                    }

                }


            }

            //다음 가야할 장소에대해 execute로 DB정보를 가져온다
            //매Update마다 가져오는건 부담이 크기때문에 계속 동일한 장소를 가리킬시, 한번만 가져오도록한다
            //또한 마커가 하나이상,가까운 마커가 있을때만 받아오도록한다(예외처리)
            if (closestMarker != null) {

                if (!before_snippet.equals(closestMarker.getSnippet()) && originMarkerlist.size() > 1) {

                    try {
                        //db 접속(try/catch 필수)
                        DB_conn db_conn_obj = new DB_conn(context, sc_lv, "short_cut", url);

                        //db에 접속하여 short_cut 생성
                        db_conn_obj.execute("get_vending_info", closestMarker.getSnippet());

                        //현재 자판기의 id를 저장한다
                        now_vd_id =closestMarker.getSnippet();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //현재 가져온 정보를 저장한다
                    before_snippet = closestMarker.getSnippet();

                    //보충완료,닫기버튼을 보이게 한다.(강제형변환으로 액티비티를 접근한다. ->이게 최선이에요 ㅠㅠ)
                    //순서대로 : 열기버튼,sortcutlayout,확인버튼,닫기버튼
                    if ((((Activity) context).findViewById(R.id.open_button)) .getVisibility() == View.GONE) {
                        (((Activity) context).findViewById(R.id.sc_layout))   .setVisibility(View.VISIBLE);
                        (((Activity) context).findViewById(R.id.ok_button))   .setVisibility(View.VISIBLE);
                        (((Activity) context).findViewById(R.id.close_button)).setVisibility(View.VISIBLE);
                    }

                    //다음가야할 정보 출력공간을 보이게 한다. -> 오른쪽 맨위 layout
                    if ((((Activity) context).findViewById(R.id.next_vending_layout)).getVisibility() == View.GONE) {
                        (((Activity) context).findViewById(R.id.next_vending_layout)).setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        //롱클릭으로 접근한다면 쇼트컷/최단 거리를 표시해준다
        else if (vending_stack.size() > 0 && originMarkerlist.size() > 1) {
            try {

                //db 접속(try/catch 필수)
                DB_conn db_conn_obj = new DB_conn(context, sc_lv, "short_cut", url);

                //db에 접속하여 short_cut 생성
                db_conn_obj.execute("get_vending_info", vending_stack.get(0).getSnippet());

                //현재 자판기의 id를 저장한다
                now_vd_id =vending_stack.get(0).getSnippet();

            } catch (Exception e) {
                e.printStackTrace();
            }

            //현재 가져온 정보를 저장한다
            before_snippet = vending_stack.get(0).getSnippet();

            //보충완료,닫기버튼을 보이게 한다.(강제형변환으로 액티비티를 접근한다. ->이게 최선이에요 ㅠㅠ)
            //순서대로 : 열기버튼,sortcutlayout,확인버튼,닫기버튼
            if ((((Activity) context).findViewById(R.id.open_button)) .getVisibility() == View.GONE) {
                (((Activity) context).findViewById(R.id.sc_layout))   .setVisibility(View.VISIBLE);
                (((Activity) context).findViewById(R.id.ok_button))   .setVisibility(View.VISIBLE);
                (((Activity) context).findViewById(R.id.close_button)).setVisibility(View.VISIBLE);
            }

            //다음가야할 정보 출력공간을 보이게 한다. -> 오른쪽 맨위 layout
            if ((((Activity) context).findViewById(R.id.next_vending_layout)).getVisibility() == View.GONE) {
                (((Activity) context).findViewById(R.id.next_vending_layout)).setVisibility(View.VISIBLE);
            }
        }

        //일본 버전 일경우
        if(now_nation){
            //0번째는 사용자의 위치! -> 아무것도 안들었다면 현재 위치를 넣어준다!
            if (originMarkerlist.size() == 0) {
                //0번째 arraylist에 배열 추가
                //0번째에는 항상 사용자의 위치가 들어간다
                //마커 옵션 함수를 만들고, 반환하여 구글맵에 마커를 그린다.
                originMarkerlist.add(0, gmap.addMarker(get_set_package.getMarkerOption(japan_location, addr, false)));
            }

            //0번째 위치를 갱신한다.
            else {
                //get으로 0번째의 마커를 지우고, set으로 새로운 위치에 마커를 생성한다.
                originMarkerlist.get(0).remove();
                originMarkerlist.set(0, gmap.addMarker(get_set_package.getMarkerOption(japan_location, addr, false)));
            }

            //위치 추적 활성화 버튼을 눌렀다면 추적 활성화
            if (location_button) {
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(japan_location, 14));
            }

            //미니맵은 항상 추적한다
            if (minimap != null) {
                minimap.moveCamera(CameraUpdateFactory.newLatLngZoom(japan_location, 15));
            }
        }

        //한국 버전 일경우
        else {
            //0번째는 사용자의 위치! -> 아무것도 안들었다면 현재 위치를 넣어준다!
            if (originMarkerlist.size() == 0) {
                //0번째 arraylist에 배열 추가
                //0번째에는 항상 사용자의 위치가 들어간다
                //마커 옵션 함수를 만들고, 반환하여 구글맵에 마커를 그린다.
                originMarkerlist.add(0, gmap.addMarker(get_set_package.getMarkerOption(new LatLng(lattitude, longitude), addr, true)));
            }

            //0번째 위치를 갱신한다.
            else {
                //get으로 0번째의 마커를 지우고, set으로 새로운 위치에 마커를 생성한다.
                originMarkerlist.get(0).remove();
                originMarkerlist.set(0, gmap.addMarker(get_set_package.getMarkerOption(new LatLng(lattitude, longitude), addr, true)));
            }

            //자신의 위치에 그려지는 마크는 없앤다.
            originMarkerlist.get(0).setVisible(false);

            //위치 추적 활성화 버튼을 눌렀다면 추적 활성화
            if (location_button) {
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
            }

            //미니맵은 항상 추적한다
            if (minimap != null) {
                minimap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            }
        }

        //처음 받아오는 결과값으로는 연산이 불가능(이전꺼와 현재꺼 필요)
        //그러므로 최초 1번 받아오는 값은 저장만 하게 한다.
        //if문은 예외처리
        if (lastlocation != null && get_set_package != null && location != null) {

            //이전미터와 현재미터의 거리를 가져오기
            double d                = get_set_package.getmeter(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()));

            //현재 시간과 이전시간의 시간 차 가져오기
            double resultTime       = location.getTime() - lastlocation.getTime();

            double kmPerHour        = (d / resultTime) * 3600; //km/s -> km/h 변환 과정(1초->1시 = 3600)

            //현재 날짜 구하기
            long nowSecond          = location.getTime();
            Date date               = new Date(nowSecond);

            //데이터 포멧 설정
            SimpleDateFormat sdf    = new SimpleDateFormat(context.getString(R.string.data_format));
            String nowDate          = sdf.format(date);

            //0km/s근접 소수점일 경우 0으로 내림(E-7이런 식으로 나오는거 방지)
            if (kmPerHour < 0.1)
                kmPerHour = 0;

            //double형 -> String형 변환 + 소수점 한자리까지만 표현
            String SkmPerHour = String.format("%.1f", kmPerHour);   //현재 km/h
            //에러 예외처리
            if (SkmPerHour.equals("NaN")) {
                SkmPerHour = "0.0";
            }

            //msg ="좌표: [ " + lattitude + ":" + longitude+"]\n"; //현재 위치의 좌표 출력 //주석 제거시 표현
            //msg+="고도: "+String.format("%.1f",altitude)+"m\n"; //고도의 세계표준을 한국표준으로 바꾸는걸 모르겟엉.. 쓸꺼면 걍써 잘 되니까...

            //출력하고자하는 문자열
            String chnage_str = context.getString(R.string.now_addr) + "\n"  + addr;

            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            SpannableStringBuilder sp = new SpannableStringBuilder(chnage_str);

            //앞 글자는 크게 출력한다
            sp.setSpan(new AbsoluteSizeSpan(25),             0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            now_addr.setText(sp);

            //출력하고자하는 문자열
            chnage_str = context.getString(R.string.now_speed)+"\n" +  SkmPerHour + "km/h";

            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            sp = new SpannableStringBuilder(chnage_str);

            //앞 글자는 크게 출력한다
            sp.setSpan(new AbsoluteSizeSpan(25),             0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            now_speed.setText(sp);

            //출력하고자하는 문자열
            chnage_str = context.getString(R.string.total_vd)+"\n" +  (originMarkerlist.size()-1)+ context.getString(R.string.val);

            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            sp = new SpannableStringBuilder(chnage_str);

            //앞 글자는 크게 출력한다
            sp.setSpan(new AbsoluteSizeSpan(25),             0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            vd_val.setText(sp);

            //다음 가야할 자판기가 있다면 정보를 출력한다
            if(vending_stack.size() > 0){

                //출력하고자하는 문자열
                chnage_str = context.getString(R.string.next_vd)+"\n"+vending_stack.get(0).getTitle();

                //하나의 textView에 글자색, 크기를 다르게 하는 함수
                sp = new SpannableStringBuilder(chnage_str);

                //앞 글자는 크게 출력한다
                sp.setSpan(new AbsoluteSizeSpan(25),             0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                next_vd_name.setText(sp);

            }

            //추가된 자판기가 하나이상 + 다음가야할 자판기가 있을경우 다음 자판기 거리 표시
            if (originMarkerlist.size() > 1 && closestMarker != null) {
                double next_meter = get_set_package.getmeter(originMarkerlist.get(0).getPosition(), closestMarker.getPosition());

                //1km 이상의 거리일 경우
                if ((int) next_meter >= 1000) {
                    //1000m가 넘어 갈시, km로만 표시한다
                    //현재 m의 올림으로 1000m미만으로 떨어질경우, 다시 m로 표시된다

                    int km = (int) Math.ceil(next_meter / 1000);

                    next_vending.setText("NEXT:" + km + "km");
                }

                //1km미만의 거리 일경우 m로 표시하며 소수점 1자리까지 표시한다.
                else {
                    next_vending.setText("NEXT:" + String.format("%.1f", next_meter) + "m");
                }
            }

            //롱클릭 자판기가 1대이상 있을경우
            else if (vending_stack.size() > 0) {
                //다음 거리를 구한다
                double next_meter = get_set_package.getmeter(originMarkerlist.get(0).getPosition(), vending_stack.get(0).getPosition());

                //1km 이상의 거리일 경우
                if ((int) next_meter >= 1000) {
                    //1000m가 넘어 갈시, km로만 표시한다
                    //현재 m의 올림으로 1000m미만으로 떨어질경우, 다시 m로 표시된다

                    int km = (int) Math.ceil(next_meter / 1000);

                    next_vending.setText("NEXT:" + km + "km");
                }

                //1km미만의 거리 일경우 m로 표시하며 소수점 1자리까지 표시한다.
                else {
                    next_vending.setText("NEXT:" + String.format("%.1f", next_meter) + "m");
                }
            }

        }

        // 생성자 같은 역할(최초 한번만 실행)
        else {
            //한번만 실행하게 한다
            if (!camera_move_check) {
                //최초 카메라 위치 잡기 ->자신의 위치가 갱신 된 이후 최초로 한번만 자신의 위치를 찾아서 카메라를 이동시킨다.
                //일본의 경우 일본으로, 한국의경우 한국으로 조정한다
                if(now_nation){
                    gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(japan_location, 13));
                }else {
                    gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                    minimap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                }
                camera_move_check = true;
            }
        }

        lastlocation = location;
    }

    //---------------------------------GPS 상태 관련 콜백 함수--------------------------------------
    //GPS 상태가 바뀔때 호출됨
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    //GPS가 켜져있으면 실행됨!
    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(context, "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        //GPS가 꺼져 있으면 설정 화면으로 가기
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
        Toast.makeText(context, "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    //------------------------------------------------------------------------------------

    //지구는 둥글구나아~
    private double toRad(Double d) {
        return d * Math.PI / 180;
    }

    //현재 위치 추적 버튼을 누를시,
    public void location_button() {
        location_button = (location_button) ? false : true;
    }

    //현재 국가 바꾸기
    public void change_Nation_Version(){
        now_nation = !now_nation;

        //바꾼 국가가 일본 인 경우
        if(now_nation){
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(japan_location, 14));
        }

        //바꾼 국가가 한국 인 경우
        else{
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()), 14));
        }
    }

    //가장 가까운 자판기 추적 버튼을 누를시,
    public void closest_vendingmachine_tracking_button() {
        //자판기가 없을경우 에러 방지를 위해 작성(자판기는 최소 1개이상)
        if (originMarkerlist.size() > 1) {
            closest_vendingmachine_tracking_button = (closest_vendingmachine_tracking_button) ? false : true;
        } else {
            closest_vendingmachine_tracking_button = false;
        }
    }

    //마커를 길게 클릭 할경우 발생하는 콜백 함수
    //사용자가 직접 가고 싶은 위치를 지정 할 수 있게 한다.
    //클릭한 위치로부터 반경x만큼 지정하여 그반경에 마커가 있는지 확인한다.
    @Override
    public void onMapLongClick(LatLng latLng) {

        //첫번째 반복문에서 클릭을 찾지 못하였을 경우 범위를 조금더 넓혀서 찾게 하기 위함
        boolean abs_check;

        //범위를 1~5까지 준다(작은 수부터 미세하게 검색하며 찾아낸다)
        for (int i = 1; i < 270; i++) {

            //찾고자하는 실수값 이 저장되는 변수
            double parse_num;

            //2자리가 아닌경우 0.0000X
            if (i < 10) {
                parse_num = Double.parseDouble("0.0000" + i);
            }

            //2자리 인경우 0.000XX
            else if (i < 100) {
                parse_num = Double.parseDouble("0.000" + i);
            }

            //3자리 인 경우 0.00XXX
            else {
                parse_num = Double.parseDouble("0.00" + i);
            }

            //true: 검색된 자판기가 없음 false:검색된 자판기가 있음
            abs_check = marker_check(latLng, parse_num);

            //검색된 자판기가 있는경우 반복문을 나간다
            if (!abs_check) {
                break;
            }

        }

    }

    //범위에 따라 탐색 범위가 틀려진다
    private boolean marker_check(LatLng latLng, double serch_size) {

        //첫번째 반복문에서 클릭을 찾지 못하였을 경우 범위를 조금더 넓혀서 찾게 하기 위함
        boolean abs_check = true;

        //현재 보충해야될 자판기의 갯수만큼 반복한다
        for (int i = 1; i < originMarkerlist.size(); i++) {

            //반경에 마커가 있는경우 DB를통해 그마커의 상세정보들을 가져온다.
            //실수를 많이 줄경우, 주변 자판기와 겹칠우려가 있으므로 너무 많이 주지않으며 클릭시, 적당한 반경을 탐색하게 해야한다.
            if (Math.abs(originMarkerlist.get(i).getPosition().latitude - latLng.latitude) < serch_size &&
                    Math.abs(originMarkerlist.get(i).getPosition().longitude - latLng.longitude) < serch_size) {

                //클릭한 자판기의 지명을 출력한다.
                Toast.makeText(context, originMarkerlist.get(i).getTitle() + "", Toast.LENGTH_SHORT).show();

                try {

                    //첫번째 롱클릭인 경우 바로 다음가야할 자판기이므로 그에따른 처리를한다
                    if (vending_stack.size() == 0) {
                        //db 접속 try/catch 필수
                        DB_conn db_conn_obj = new DB_conn(context, sc_lv, "short_cut", url);

                        //서버에서 값을 가져와 shortcut을 만들어준다
                        db_conn_obj.execute("get_vending_info", originMarkerlist.get(i).getSnippet());

                        //현재 자판기의 id를 저장한다
                        now_vd_id = originMarkerlist.get(i).getSnippet();

                        //오른쪽 밑 레이아웃이 현재 안보이고 있다면 보이게 한다
                        if (((Activity) context).findViewById(R.id.sc_layout).getVisibility() == View.GONE) {
                            //보충완료,닫기버튼을 보이게 한다.(강제형변환으로 액티비티를 접근한다.)
                            //순서대로 : 오른쪽 밑 레이아웃. 갱신버튼, 닫기버튼
                            (((Activity) context).findViewById(R.id.sc_layout)).setVisibility(View.VISIBLE);
                            (((Activity) context).findViewById(R.id.ok_button)).setVisibility(View.VISIBLE);
                            (((Activity) context).findViewById(R.id.close_button)).setVisibility(View.VISIBLE);
                        }

                        //다음가야할 정보 출력공간을 보이게 한다.
                        //오른쪽 위 레이아웃
                        if ((((Activity) context).findViewById(R.id.next_vending_layout)).getVisibility() == View.GONE) {
                            (((Activity) context).findViewById(R.id.next_vending_layout)).setVisibility(View.VISIBLE);
                        }

                        //롱 클릭된 자판기가 특정 m안에 자판기가 있는경우
                        if (vending_stack.size() > 0 ||
                                get_set_package.getmeter(originMarkerlist.get(0).getPosition(), originMarkerlist.get(i).getPosition()) < 443) {

                            //미니맵을 보이게 한다. -> 왼쪽 위 레이아웃
                            if ((((Activity) context).findViewById(R.id.minimap_side)).getVisibility() == View.GONE) {
                                ((MainActivity) context).findViewById(R.id.minimap_side).setVisibility(View.VISIBLE);
                                ((MainActivity) context).findViewById(R.id.minimap_layout).setVisibility(View.VISIBLE);
                            }

                        }

                        //다음 가야할 자판기를 최신화 시킨다.(short cut)
                        before_snippet = originMarkerlist.get(i).getSnippet();

                        //다음 자판기까지의 거리를 로딩으로 바꾼다
                        next_vending.setText("Loading..");

                        //다음 가야할 자판기를 하이라이팅한다(맵에 마커 추가)
                        get_set_package.drawMarkers(originMarkerlist.get(i).getPosition(), originMarkerlist.get(i).getTitle(), originMarkerlist.get(i).getSnippet(), -1, false);
                        get_set_package.draw_minimap_marker(originMarkerlist.get(i).getPosition(), originMarkerlist.get(i).getTitle(), originMarkerlist.get(i).getSnippet(), 1, false);

                        //다음 가야할 자판기의 마커를 최신화 시킨다 -> 직선상의 거리를 표시해주는 기능을 함
                        closestMarker = originMarkerlist.get(i);

                        //길 경로 그려주기
                        get_set_package.dir_fuc.call_Function(get_set_package.japan_location ,get_set_package.get_vending_stack().get(0).getPosition());
                    }

                    //만약 롱클릭으로 빨간색 하이라이트를 클릭한다면 지정된 자판기들은 전부 해제한다
                    else if (originMarkerlist.get(i).getPosition().equals(vending_stack.get(0).getPosition())) {

                        //지도에서 마커 지우기
                        for(int j = 0; j<vending_stack.size(); j++){
                            vending_stack.get(j).remove();
                        }

                        //미니맵 에서 마커 지우기
                        for(int j = 0; j<mini_stack.size(); j++){
                            mini_stack.get(j).remove();
                        }

                        //길 경로들을 지운다
                        get_set_package.dir_fuc.main_polyline.remove();
                        get_set_package.dir_fuc.mini_polyline.remove();

                        //배열 비우기
                        vending_stack.clear();
                        mini_stack   .clear();

                        //미니맵 비우기
                        minimap.clear();
                        minimap.addMarker(get_set_package.getMarkerOption(japan_location, "no data", false));

                        //오른쪽 밑 레이아웃을 안보이게 한다
                        if (((Activity) context).findViewById(R.id.sc_layout).getVisibility() == View.VISIBLE) {
                            (((Activity) context).findViewById(R.id.sc_layout)).setVisibility(View.GONE);
                        }

                        //다음가야할 정보 출력공간을 안보이게 한다 ->오른쪽 위 레이아웃
                        if ((((Activity) context).findViewById(R.id.next_vending_layout)).getVisibility() == View.VISIBLE) {
                            (((Activity) context).findViewById(R.id.next_vending_layout)).setVisibility(View.GONE);
                        }

                        //미니맵을 안보이게 한다. -> 왼쪽 위 레이아웃
                        if ((((Activity) context).findViewById(R.id.minimap_side)).getVisibility() == View.VISIBLE) {
                            ((MainActivity) context).findViewById(R.id.minimap_side).setVisibility(View.GONE);
                            ((MainActivity) context).findViewById(R.id.minimap_layout).setVisibility(View.GONE);
                        }

                        //다음 가야할 자판기 텍스트 초기화
                        next_vd_name.setText(context.getString(R.string.select_next_vd));

                    }

                    //바로 다음 가야할 자판기가 아니므로 그에따른 처리를 한다
                    else {

                        //롱클릭 중복 체크 변수 생성
                        Boolean overlap_check = true;

                        //한번이상 클릭 한 곳에 한번더 클릭한다면 false로 바꾼다
                        for (int z = 0; z < vending_stack.size(); z++) {
                            if (originMarkerlist.get(i).getPosition().equals(vending_stack.get(z).getPosition())) {
                                overlap_check = false;
                            }
                        }

                        //중복이 아닌 경우에(새로운 마커 클릭시) 구글맵에 그린다
                        if (overlap_check) {
                            //다음 가야할 자판기를 하이라이팅한다(맵에 마커 추가)
                            get_set_package.drawMarkers(originMarkerlist.get(i).getPosition(), originMarkerlist.get(i).getTitle(), originMarkerlist.get(i).getSnippet(), -2, false);
                            get_set_package.draw_minimap_marker(originMarkerlist.get(i).getPosition(), originMarkerlist.get(i).getTitle(), originMarkerlist.get(i).getSnippet(), 2, false);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //다음 자판기 추적을 켜져있을경우 끈다.
                closest_vendingmachine_tracking_button = false;

                //추적을 실제로 껏을 경우, menu바의 추적 활성화도 비활성화로 바꾼다.
                navi_menu.findItem(R.id.Closest_V_machine_tracking).setChecked(false);

                //자판기를 찾았기 때문에 false값을 넘겨준다
                abs_check = false;

                //자판기를 찾았기 때문에 반복문을 종료한다
                break;
            }

        }

        //탐색의 유무를 반환한다(찾음:false 못찾음: true)
        return abs_check;

    }

}

//마커를 클릭했을때, 이벤트들을 모아놓은 클래스(마커 클릭, 클릭시 출력되는 view 설정)
class Mark_click_event implements GoogleMap.OnMarkerClickListener {
    private Context             context;            //MainActivity this
    private DB_conn             db_conn_obj;        //db접속 변수
    private ArrayList<Marker>   originMarkerlist;   //마커들이 저장되어있는 배열
    private String              user_login_id;      //유저 로그인 아이디
    private Handler             handler;            //핸둘러!
    private String              url;                //서버 주소

    //생성자
    public Mark_click_event(Context context, GoogleMap googleMap, ArrayList<Marker> originMarkerlist, String user_login_id, Handler handler, String url) {
        this.context            = context;
        this.originMarkerlist   = originMarkerlist;
        this.user_login_id      = user_login_id;
        this.handler            = handler;
        this.url                = url;

        //마커 클릭을 활성화 시킨다
        googleMap.setOnMarkerClickListener(this);
    }

    //OnMarkerClickListener 오버라이딩
    //마커를 클릭했을시 이벤트 -> custom_alertDialog를 만들어서 띄워준다
    @Override
    public boolean onMarkerClick(Marker marker) {

        //자신의 위치를 클릭하였을 경우에는 실행하지 않는다
        if (!marker.getTitle().equals("my_location")) {

            //핸들러 생성
            Runnable task = new Runnable() {
                @Override
                public void run() {

                    //로딩 화면생성
                    handler.sendEmptyMessage(3);
                    try {
                        //1.5초 지연
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //로딩화면 제거
                    handler.sendEmptyMessage(4);
                }
            };

            //쓰레드 생성
            Thread thread = new Thread(task);


            try {

                //쓰레드 실행
                thread.start();
                //db접속 try/catch 필수
                db_conn_obj = new DB_conn(context, marker.getTitle(), user_login_id, "alert_dialog", url);

                //서버의 결과값으로 custom alert dailog를 만들어 띄운다
                db_conn_obj.execute("get_vending_info", marker.getSnippet());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //반환형이 false일 경우 기존의 클릭 이벤트도 같이 발생된다.
        return true;
    }
}


//MainActivity
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //-----------------------------------------레이아웃-----------------------------------------
    private DrawerLayout        drawerLayout;
    private ConstraintLayout    info_layout;

    //-------------------------------------화면에 출력되는 애--------------------------------------
    private TextView            user_name_tv, user_email_tv, user_id_tv_hide, next_vending;
    private ListView            sc_lv;

    //이미지 버튼들(우측의 이미지버튼)
    private ImageButton         ok_button, open_button, close_button, info_swap_button;
    private Get_set_package     get_set_package;            //공통 get set함수를 모아클래스로 만들어 사용한다.
    private DB_conn             db_conn_obj;                //서버의 DB에 접속 할때마다 이 객체를 사용한다.
    private Menu                navi_menu;                  //네비게이션바의 모든 객체들을 조종할 수 있다.
    private GoogleMap           gmap, minimap;              //하나의 googlemaps으로 모든 클래스에서 참조하여 쓴다

    //--------------------------------------------변수--------------------------------------------
    private String[]            user_info;                  //현재 로그인 되어 있는 유저의 정보(id,name..)들을 가져온다
    private long                fir_time, sec_time;         //두번 뒤로가기를 눌렀을때 종료되도록 해주는 변수 둘
    private Boolean             drawer_check        = true; //슬라이드를 열고 닫게 할 수 있다.

    //보충해야할 자판기들이 저장되는 배열
    private ArrayList<Marker>   originMarkerlist    = new ArrayList<Marker>();

    //현재 롱클릭으로 가야할 자판기들이 저장되는 배열
    private ArrayList<Marker>   vending_stack       = new ArrayList<Marker>();
    private ArrayList<Marker>   mini_stack          = new ArrayList<Marker>();

    //주소는 능동적으로 바뀔 수 있다!
    private String              image_path          =
            "http://ec2-13-125-134-167.ap-northeast-2.compute.amazonaws.com/images/supplementer/";

    //서버 주소
    private String              url;


    //--------------------------------------------이외--------------------------------------------
    private Locationlistener    listener;                   //사용자 지정 트리거 마다 실행되는 함수
    private Share_login_info    share_login_info_obj;       //로그인 상태 저장 객체
    private ImageView           loading_iv;                 //로딩 화면 iv
    private ImageView           loading_iv_map;             //로딩화면 map iv
    private Handler             handler;                    //로딩Thread handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //쓰레드 핸들러(로딩화면을 보이거나 지운다)
        //1,2 오른쪽 로딩창 3.4 왼쪽 로딩창
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    //login_iv에 애니메이션을 넣어 놓는다.(로딩화면 애니메이션)
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
                    loading_iv.setAnimation(animation);
                    loading_iv.setVisibility(View.VISIBLE);
                } else if (msg.what == 2) {
                    //실행중인 애니메이션을 전부 지운다
                    loading_iv.clearAnimation();
                    loading_iv.setVisibility(View.GONE);
                } else if (msg.what == 3) {
                    //login_iv에 애니메이션을 넣어 놓는다.(로딩화면 애니메이션)
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
                    loading_iv_map.setAnimation(animation);
                    loading_iv_map.setVisibility(View.VISIBLE);
                } else if (msg.what == 4) {
                    //실행중인 애니메이션을 전부 지운다
                    loading_iv_map.clearAnimation();
                    loading_iv_map.setVisibility(View.GONE);
                }
            }
        };
        //-------------------------------인터넷연결 확인/관리 부분-------------------------------
        //인터넷 연결 체크 // 실시간으로 연결되는지 검사한다
        //연결될경우 실시간으로 알려준다.
        //만약 네트워크 변경이 일어난다면(네트워크에 새롭게 접속을 한다면)
        //(네트워크->다른 네크워크 or 없음 -> 네트워크) 접속시 앱 재시작
        Internet_conn_check receiver = new Internet_conn_check(this);
        IntentFilter        filter   = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        //최초 네트워크 확인 + GPS On/Off확인 -> 연결되어있지 않다면 아무거도 보여주지 않는다
        if (!connect_check()) {
            return;
        }

        //------------------------------레이아웃 셋팅-------------------------------------------------

        //activity의 context정보 변수에 담기
        Window win = getWindow();

        // 첫번째 layout을 추가한다.
        win.setContentView(R.layout.main_page_activity_main);

        //드래그 했을때 보여지는 레이아웃 설정
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            //슬라이드를 시작했을 경우(열었을때 -> 닫았을때는 실행x)
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (drawer_check) {
                    drawer_check = false;
                }
            }

            //DrawLayout이 열렸을때 불리는 함수
            @Override
            public void onDrawerOpened(View drawerView) {
            }

            //DrawLayout을 닫는 행위를 할 경우
            //보이지않게 한 상태바를 다시 보이게 한다.
            @Override
            public void onDrawerClosed(View drawerView) {
                drawer_check = true;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        //-----------------------------레이아웃 셋팅 끝--------------------------------------------

        //저장소들 id 가져오기!!!!!!
        info_layout         = (ConstraintLayout)    findViewById(R.id.info_layout);
        next_vending        = (TextView)            findViewById(R.id.next_vending);
        sc_lv               = (ListView)            findViewById(R.id.sc_lv);
        ok_button           = (ImageButton)         findViewById(R.id.ok_button);
        close_button        = (ImageButton)         findViewById(R.id.close_button);
        open_button         = (ImageButton)         findViewById(R.id.open_button);
        info_swap_button    = (ImageButton)         findViewById(R.id.info_swap_button);
        loading_iv          = (ImageView)           findViewById(R.id.loading_iv);
        loading_iv_map      = (ImageView)           findViewById(R.id.loading_iv_map);


        //네비게이션뷰 가져오기(왼쪽에서 오른쪽으로 슬라이드 할시, 나오는 창)
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //로그인 한 결과값을 가져온다(사용자 정보)
        Intent data = getIntent();

        //정상적으로 값을 가져왔다면
        if (data.getStringExtra("url") != null) {
            //유저 정보를 가져온다
            String str = data.getStringExtra("url");
            url = str;
        }

        //정상적으로 값을 가져왔다면
        if (data.getStringExtra("user_info") != null) {

            //유저 정보를 가져온다
            String str = data.getStringExtra("user_info");
            //[0]=login_id [1]=name [2]=email [3]=imgsrc
            user_info = str.split("/br/");

            //푸쉬메세지에서 보내는 값이 있는지 확인한다
            String check       = data.getStringExtra("go_order_sheet");

            //푸쉬메세지로 접근시 작업지시서를 바로 출력해준다
            if(check != null){
                //핸들러 생성
                Runnable task = new Runnable() {
                    @Override
                    public void run() {

                        //로딩 화면생성
                        handler.sendEmptyMessage(3);
                        try {
                            //1초 지연
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //로딩화면 제거
                        handler.sendEmptyMessage(4);
                    }
                };

                //쓰레드 생성
                Thread thread = new Thread(task);

                //쓰레드 실행
                thread.start();

                //custom alert 보여주기 클래스 생성(작업지시서) / 함수 실행
                Order_sheet_alert order_sheet_alert = new Order_sheet_alert(MainActivity.this, user_info[0], url);
                order_sheet_alert.create_table(1,0);
            }


            //네비게이션의 헤더부분 가져오기
            View hView = navigationView.getHeaderView(0);

            //보충기사 사진 업데이트
            Picasso.with(this)
                    .load(image_path + user_info[0] + ".png")
                    //사진이 없을시 여기에 지정한 애로 출력됨
                    .placeholder(R.drawable.face_ex)
                    .into((CircleImageView) hView.findViewById(R.id.user_img_iv));

            //id부분 가져오기(사용자에게는 안보이게 = unique값으로 유지)
            user_id_tv_hide = (TextView) hView.findViewById(R.id.user_id_tv_hide);
            user_id_tv_hide.setText(user_info[0]);

            //이름부분 가져오기
            user_name_tv = (TextView) hView.findViewById(R.id.user_name_tv);
            user_name_tv.setText(user_info[1]);

            //이메일부분 가져오기
            user_email_tv = (TextView) hView.findViewById(R.id.user_email_tv);
            user_email_tv.setText(user_info[2]);
        }

        //네비게이션뷰의 메뉴들을 가져온다
        navi_menu = navigationView.getMenu();

        //메뉴의 버튼들 틀릭 시,
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //실제로 옵션들을 눌렀을 때 ClickListener같은 놈
                int id = menuItem.getItemId();
                switch (id) {

                    //국가 버전 정하기
                    case R.id.Nation_Version:

                        //일본 -> 한국 버전으로 바꾸기
                       if(menuItem.getTitle().equals("Japan Version")){
                           menuItem.setTitle("Korea Version");
                           listener.change_Nation_Version();
                        }

                        //한국 -> 일본버전 바꾸기
                       else{
                           menuItem.setTitle("Japan Version");
                           listener.change_Nation_Version();
                       }

                        break;

                    //Tracking 버튼 활성화시 현재위치 계속 추적
                    case R.id.GPS_tracking:

                        if (gmap != null && listener != null) {
                            //토글 같은 느낌 켜져있으면 끄고 꺼져있으면 킨다
                            listener.location_button();
                        }

                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        break;

                    //자판기 Tracking 버튼 활성화시, 주기적으로 가장 가까운 자판기 추적
                    case R.id.Closest_V_machine_tracking:

                        if (gmap != null && listener != null) {
                            listener.closest_vendingmachine_tracking_button();
                            Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        }

                        break;

                    //작업지시서 보기
                    case R.id.order_list:

                        //핸들러 생성
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {

                                //로딩 화면생성
                                handler.sendEmptyMessage(3);
                                try {
                                    //1초 지연
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //로딩화면 제거
                                handler.sendEmptyMessage(4);
                            }
                        };

                        //쓰레드 생성
                        Thread thread = new Thread(task);

                        //쓰레드 실행
                        thread.start();

                        //custom alert 보여주기 클래스 생성(작업지시서) / 함수 실행
                        Order_sheet_alert order_sheet_alert = new Order_sheet_alert(MainActivity.this, user_info[0], url);
                        order_sheet_alert.create_table(1,0);

                        break;

                    //작업지시서 달력으로 검색하여 보기
                    case R.id.serch_order_list:

                        //핸들러 생성
                        Runnable task2 = new Runnable() {
                            @Override
                            public void run() {

                                //로딩 화면생성
                                handler.sendEmptyMessage(3);
                                try {
                                    //1초 지연
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //로딩화면 제거
                                handler.sendEmptyMessage(4);
                            }
                        };

                        //쓰레드 생성
                        Thread thread2 = new Thread(task2);

                        //쓰레드 실행
                        thread2.start();

                        Create_AlertDialog create_alertDialog = new Create_AlertDialog(MainActivity.this, user_info[0], url);
                        create_alertDialog.callFunction();
                        break;

                    //작업지시서 최신화 하기
                    case R.id.refrash_order_list:

                        //마커 최신화 / 갱신
                        draw_marker();

                        break;

                    //클릭시 로그아웃 후 로그인 화면으로 전환
                    case R.id.Logout:

                        //로그아웃 버튼 클릭시,
                        //현재 저장된 정보들을 전부 초기화하며 다시 메인페이지로 돌아간다.
                        share_login_info_obj = new Share_login_info(MainActivity.this);
                        share_login_info_obj.remove_all();

                        //로그인 화면으로 돌아간다
                        Intent intent = new Intent(MainActivity.this, Login_page_Activity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

                //Settings들만 활성화/비활성화 기능 사용
                if (id == R.id.GPS_tracking || id == R.id.Closest_V_machine_tracking) {

                    //체크가 해제되어 있으면 눌러진상태로, 눌러진상태면 해제 시킨다
                    if (menuItem.isChecked() == true) {
                        menuItem.setChecked(false);
                    } else {
                        menuItem.setChecked(true);
                    }

                }

                //나머지는 눌러도 비활성화 되도록 한다.
                else {
                    menuItem.setChecked(false);
                }

                //누르고나서 열어놓은 창을 닫는다
                drawerLayout.closeDrawers();
                return true;
            }
        });

        //우측 레이아웃을 보이게 하거나 안보이게 한다
        info_swap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //어떤 애니메이션을 설정할건지 정하는 변수
                Animation animation;

                //레이아웃을 안보이게 한다
                if(info_layout.getVisibility() == View.VISIBLE){

                    //어떤 애니메이션을 보여줄건지 설정(화면 밖으로 나가는 애니메이션)
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out);

                    //버튼과 레이아웃에 애니메이션 적용
                    info_layout.startAnimation(animation);
                    info_swap_button.startAnimation(animation);

                    //레이아웃은 이후 아예 보이지 않게한다
                    info_layout.setVisibility(View.GONE);

                    //버튼이미지를 여는 이미지로 바꾼다
                    info_swap_button.setImageResource(R.drawable.open);
                }

                //레이아웃을 보이게 한다
                else{

                    //어떤 애니메이션을 보여줄건지 설정(화면 안으로 들어오는 애니메이션)
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in);

                    //레이아웃을 먼저 보이게 한다
                    info_layout.setVisibility(View.VISIBLE);

                    //버튼과 레이아웃에 애니메이션을 적용한다
                    info_layout.startAnimation(animation);
                    info_swap_button.startAnimation(animation);

                    //버튼 이미지를 닫는 이미지로 바꾼다
                    info_swap_button.setImageResource(R.drawable.close);
                }
            }
        });



        //자판기(작업지시서) 갱신 버튼 클릭시,
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //마커 최신화 / 갱신
                draw_marker();

            }
        });

        //강제갱신(보충완료) -> 강제로 자판기를 갱신한다
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //findViewById(R.id.sc_layout).setVisibility(View.GONE);
                //open_button.setVisibility(View.VISIBLE);

                //현재 자판기 마커를 없애고(보충완료) 마커를 최신화한다
                try {
                    DB_conn db_conn = new DB_conn(MainActivity.this, url);
                    db_conn.execute("insert_vending", listener.now_vd_id.toString(), user_info[0].toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //마커 최신화(갱신)
                draw_marker();

            }
        });

        //쇼트컷 열기, 오른쪽 밑의 레이아웃이 보여진다
        open_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.sc_layout).setVisibility(View.VISIBLE);
                open_button.setVisibility(View.GONE);
            }
        });

        //구글 맵 그리기 OnMapReadyCallBack -> 정상 호출시, onMapReady함수 호출
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //최초 한번만 그리기

        //미니맵 그리기 OnMapReadyCallBack -> 정상 호출시, onMapReady함수 호출
        SupportMapFragment minimapFragment2 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.minimap);
        minimapFragment2.getMapAsync(this); //최초 한번만 그리기


    }

    //최초 네트워크 확인 함수
    public boolean connect_check() {

        //현재 사용중인 네트워크를 가져온다.
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activityNetwork = cm.getActiveNetworkInfo();

        //네트워크에 연결 되어있는지 확인
        if (activityNetwork != null) {

            //wifi로 연결 중 일시,
            if (activityNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //Toast.makeText(this, "You are now connected to wifi", Toast.LENGTH_SHORT).show();
            }

            //데이터(LTE/3G)로 연결 중일시
            else if (activityNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // Toast.makeText(this, "You are now connected to data(LTE/3G)", Toast.LENGTH_SHORT).show();
            }

        }

        //네트워크가 연결 되어있지 않다면
        else {
            //비연결시 어플 다음작업 실행x -> 종료
            Toast.makeText(this, "Please connect with wifi or data(LTE/3G)", Toast.LENGTH_SHORT).show();
            return false;
        }

        //GPS 체크후 비활성화시 권한 요청
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {

            //GPS가 꺼져있을경우
            Toast.makeText(this, "GPS is off, please check your GPS!!", Toast.LENGTH_SHORT).show();

            //GPS켜는 권한 사용자에게 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);

        }

        //네트워크가 모든 조건을 만족하고 연결되어 있다면 true를 반환
        return true;
    }

    //OnMapReadyCallback 오버라이딩 // 구글 맵 로딩하기
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //메인 지도 설정
        if (gmap == null) {
            //기존 맵 객체 가져오기
            gmap = googleMap;

            //오른쪽위에 나오는 gps버튼,나침반 제거
            gmap.getUiSettings().setMyLocationButtonEnabled(false);
            gmap.getUiSettings().setCompassEnabled(false);

            try {
                //내위치계층을 활성화 시킨다.
                gmap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Toast.makeText(this, "not found my location.", Toast.LENGTH_SHORT).show();
            }

        }

        //미니맵 지도 설정
        else if (gmap != null && minimap == null) {
            //기존 맵 객체 가져오기
            minimap = googleMap;

            //오른쪽위에 나오는 gps버튼,나침반 제거
            minimap.getUiSettings().setMyLocationButtonEnabled(false);
            minimap.getUiSettings().setCompassEnabled(false);

            try {
                //내위치계층을 활성화 시킨다.
                minimap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Toast.makeText(this, "not found my location.", Toast.LENGTH_SHORT).show();
            }

            //---------------------------------메인 맵 설정-------------------------------------------------------
            //Get_set_package 클래스 생성
            get_set_package = new Get_set_package(this, gmap, minimap, originMarkerlist,vending_stack, mini_stack);

            //마커클릭 이벤트 클래스 생성
            new Mark_click_event(this, gmap, originMarkerlist, user_info[0], handler, url);
            new Mark_click_event(this, minimap, originMarkerlist, user_info[0], handler, url);

            get_set_package.set_dir_fuc(new Directions_Functions(get_set_package));

            //매초 혹은 미터 마다 갱신될 class 생성
            listener = new Locationlistener(this, originMarkerlist, vending_stack, mini_stack, gmap, minimap, sc_lv, next_vending, get_set_package, navi_menu, url);

            //마커 최신화 / 갱신
            draw_marker();
            //---------------------------------메인 맵 설정-------------------------------------------------------

            //GPS가 켜져있다면 이 함수 실행
            initLocationManager();

        } else {
            Toast.makeText(this, "Re:", Toast.LENGTH_SHORT).show();
        }

        if (gmap != null && minimap != null) {
            //최초 카메라 위치 잡기 -> 일본 기준
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.690695, 139.703262), 14));
            minimap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.690695, 139.703262), 15));
        }

    }

    //자판기 갱신시, 생길수 있는 로딩시간에 호출되는 함수
    public void loading() {
        Runnable task;

        //핸들러 생성
        task = new Runnable() {
            @Override
            public void run() {

                //로딩 화면생성
                handler.sendEmptyMessage(1);
                try {
                    //5초 지연
                    Thread.sleep(6500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //로딩화면 제거
                handler.sendEmptyMessage(2);
            }
        };

        //쓰레드 생성
        Thread thread = new Thread(task);

        //쓰레드 실행
        thread.start();
    }

    //마커 최신화 / 갱신 -> 새로고침, 자판기 갱신, 보충완료 버튼 클릭시
    public void draw_marker() {
        //----------------------------------로딩화면 생성----------------------------------------
        Runnable task;
        //핸들러 생성
        task = new Runnable() {
            @Override
            public void run() {

                handler.sendEmptyMessage(1);
                try {
                    //2초 지연
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //로딩화면 제거
                handler.sendEmptyMessage(2);
            }
        };

        //쓰레드 생성
        Thread thread = new Thread(task);

        //쓰레드 실행
        thread.start();
//---------------------------------------------------------------------------------------

        //기존 마커들 지우기
        if (gmap != null) {
            gmap.clear();
            minimap.clear();
            minimap.addMarker(get_set_package.getMarkerOption(listener.japan_location, "no data", false));
        }

        //기존 마커들 저장소 지우기
        if (get_set_package.getOriginMarkerlist() != null || get_set_package.getOriginMarkerlist().size() != 0) {
            get_set_package.getOriginMarkerlist().clear();
        }

        //다음 가야할 위치 가져와 삭제
        Marker marker = get_set_package.getNow_Marker();
        if (marker != null) {
            marker.remove();
        }

        //오른쪽 밑 레이아웃 지우기
        if(findViewById(R.id.sc_layout).getVisibility() == View.VISIBLE) {
            findViewById(R.id.sc_layout).setVisibility(View.GONE);
        }


        //다음가야할 거리 레이아웃 숨기기
        if(findViewById(R.id.next_vending_layout).getVisibility() == View.VISIBLE) {
            findViewById(R.id.next_vending_layout).setVisibility(View.GONE);
        }

        //리스너가 있다면
        if (listener != null) {
            //다음 가야할 자판기의 정보 초기화
            listener.setBefore_snippet("");

            //가장 가까운 자판기 정보 초기화
            listener.setClosestMarker();

            //이전의 자신의 위치 조기화
            listener.setLastlocation();
        }

        try {

            //현재 날짜 구하는 함수 포멧은 ex) 2018-04-25 로 문자열로 변환되어 출력됨
            SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd", Locale.KOREA);
            String str_date     = df.format(new Date());

            //db접속 try/catch 필수
            db_conn_obj = new DB_conn(this, get_set_package, vending_stack, mini_stack, url);

            //마커들을 새로 그린다 ->user의 login_id를 기준으로
            //db_conn_obj.execute("get_markers", user_info[0], str_date);

            //특정 날짜를 기준으로 마커를 그리고 싶으면 얘를 쓴다
            db_conn_obj.execute("get_markers", user_info[0], "2018-08-14");
            //db_conn_obj.execute("get_markers", user_info[0], "2018-06-27");



        } catch (Exception e) {
            Log.e(">>>>>>>>>>>>>>>>>>>>>>>", e.toString());

        }
    }

    //GPS가 켜져 있을 때, 최초 한번 실행
    private void initLocationManager() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            //업데이트시 x초마다 실행! + GPS를 이용할껀지 네트웤을 이용할껀지 자동 선택
            //3.26 수정 / NetworkProvider을 받아야 실내에서도 시연 가능 하기때문에 이것만으로 바꿈
            //Update함수는 SetInterval같은 존재, 조건마다 계속 onLocationChanged 함수를 불러옴

            //실외에 사용시 if문 주석 제거후 사용하면 GPS_PROVIDER를 인식하여 조금더 정확하게 사용 가능하다!
            //  if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER) == true) {
            //만들어놓은 locationlistener를 바탕으로 실행 // 프로바이터설정/최소시간/최소거리/로케이션 리스너
            //    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
            // } else {

            //시연 할때는 내부에서 함으로 NETWORK_PROVIDER를 이용한다
            //GPS_PROVIDE를 쓰고 싶을 경우 밑에 주석을 풀고 위에 주석을 단다
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);

            //  }

        } catch (SecurityException e) {
            Toast.makeText(this, "No Permission.", Toast.LENGTH_SHORT).show();
        }
    }

    //상단바의 Option클릭 시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            //왼쪽 상단에 버튼을 클릭시(사용자 지정 id 아님) -> 내장되어있는 버튼(드래그시 발생하는 이벤트)
            case android.R.id.home:
                //Drawlayout을 왼쪽으로 오른쪽으로 여는 행위
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //뒤로가기 두번 클릭시 나가지는 이벤트
    @Override
    public void onBackPressed() {
        sec_time = System.currentTimeMillis();
        if (sec_time - fir_time < 2000) {
            super.onBackPressed();
            finishAffinity();
        }
        Toast.makeText(this, getString(R.string.back_double), Toast.LENGTH_SHORT).show();
        fir_time = System.currentTimeMillis();
    }
}

