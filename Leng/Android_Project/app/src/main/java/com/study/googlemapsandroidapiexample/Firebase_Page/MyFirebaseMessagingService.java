package com.study.googlemapsandroidapiexample.Firebase_Page;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.splash_activity;

//push알람이 왔을 때 실제로 푸쉬알람의 옵션을 설정해 만들어주는 class
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //알림 메세지가 왔을 때, 실행하는 콜백 함수
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //알림 창을 만들어서 보여주는 함수
        sendNotification(remoteMessage.getData().get("message"));
    }

    //알림form을 만들어 보여준다.
    private void sendNotification(String messageBody) {
        //보여줄 Class 를 Intent로 지정
        Intent intent = new Intent(this, splash_activity.class);

        //작업지시서로 접근 한다는 것을 알려준다
        intent.putExtra("go_order_sheet","go");

        //이전 Activity 삭제하고 새로운 class 생성
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //알림 버튼을 누르는 시점에 보여주는 컴포넌트 설정
        //바로 실행하지 않고 알림을 누르는 시점에 실행된다!
        //FLAG_ONE_SHOT -> 일회용 FLAG pendingIntent로 생성
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //TYPE_NOTIFICATION -> 알림에 사용되는 소리의 유형을 뜻함
        //getDefaultUri -> uri의 특정 기본 벨소리 반환
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //만든 옵션으로 알림을 만들어서 보여지는 곳
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //보낼 양식 지정
        Notification.Builder nBuilder = new Notification.Builder(this,"my_channel_01")
                .setContentTitle(getString(R.string.notice))    //알림영역의 제목 - 공지사항
                .setContentText(messageBody)                    //알림 영역의 내용
                .setSmallIcon(R.drawable.japangi)               //알림 영역에 보여질 아이콘
                .setAutoCancel(true)                            //알림 눌렀을때 자동으로 사라지게 할것인지
                .setTicker("Notice!!")                          //실행시 잠깐 나오는 메세지
                .setContentIntent(pendingIntent);



        //출력!!!!!
        notificationManager.notify(23 /* ID of notification */, nBuilder.build());

    }

}
