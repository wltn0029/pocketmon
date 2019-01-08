package com.example.viewpagerdemo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

public class GameActivity extends AppCompatActivity {
    Socket mSocket = SocketHandler.getSocket();
    String username;
    int accountid;
    String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent i = getIntent();
        username = i.getStringExtra("username");
        accountid = i.getIntExtra("accountid",0);
        roomName = i.getStringExtra("roomName");
        Toast.makeText(getApplicationContext(),username + " : " + accountid,Toast.LENGTH_SHORT).show();
        JSONObject json = new JSONObject();
        try{
            json.put("username", username);
            json.put("roomName", roomName);
        }catch(Exception e){
        }
        mSocket.on("enter_new_user", enterNewUser);
        mSocket.emit("entering_game",json.toString());
    }

    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            // 원래 하려던 동작 (UI변경 작업 등)
//            Toast.makeText(getApplicationContext(),"Welcome "+msg.getData().getString("username")+"!!",Toast.LENGTH_SHORT).show();
        }
    };

    private Emitter.Listener enterNewUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            String receivedData = args[0].toString();
            Log.d("JI","asdf");
            // your code...
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("name",receivedData);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        JSONObject json = new JSONObject();
        try{
            json.put("username", username);
            json.put("roomName", roomName);
        }catch(Exception e){
        }
//        mSocket.emit("exiting_game",json.toString());
//        mSocket.disconnect();
        mSocket.off("new message");

        Log.d("나감","");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        JSONObject json = new JSONObject();
        try{
            json.put("username", username);
            json.put("roomName", roomName);
        }catch(Exception e){
        }
        mSocket.emit("exiting_game",json.toString());
    }

    //TODO: 201901080 게임 화면 띄우기



}
