package com.example.viewpagerdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;


import static java.lang.Thread.sleep;


public class GameActivity extends AppCompatActivity {
    Socket mSocket = SocketHandler.getSocket();
    String username;
    int accountid;
    //user's image
    public static ImageView heart;
    int otherid;
    String roomName;
    public static MyView mv;
    Context mContext;
    //user's color
    int userColor;
    int user2Color;

    public static float heartX;
    public static float heartY;
    public static float smileX;
    public static float smileY;
    public static ArrayList<Block> Board;
    String direction;
    Timer timer;
    public static int curXuser=0;
    public static int curYuser=0;
    public static int curXother=23;
    public static int curYother=23;
    public static int initXuser;
    public static int initYuser;
    public static int initXother;
    public static int initYother;
    boolean isStart = false;
    List<Integer> infoArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        userColor = Color.BLUE;
        user2Color = Color.RED;
        Board = new ArrayList<Block>();
        timer = new Timer();
        //initialize board before game starts
        for(int j=0;j<23;j++)
            for(int i=0;i<23;i++)
                Board.add(new Block(i,j));
        mContext = this;
        heart = (ImageView)findViewById(R.id.icon);
        mv = (MyView)findViewById(R.id.myView);
        Intent i = getIntent();
        username = i.getStringExtra("username");
        accountid = i.getIntExtra("accountid",0);
        roomName = i.getStringExtra("roomName");
        Toast.makeText(getApplicationContext(),username + " : " + accountid,Toast.LENGTH_SHORT).show();
        JSONObject json = new JSONObject();
        try{
            json.put("id",accountid);
            json.put("username", username);
            json.put("roomName", roomName);
        }catch(Exception e){
        }
        mSocket.on("enter_new_user", enterNewUser);
        mSocket.emit("entering_game",json.toString());
        isStart = true;
        mSocket.on("array",getArray);
        mSocket.on("dead",checkDead);
        final long timeInterval = 100000;
        Thread thread = new Thread(runnable);
        thread.start();
        //drawThread.start();
    }


    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            // 원래 하려던 동작 (UI변경 작업 등)
//            Toast.makeText(getApplicationContext(),"Welcome "+msg.getData().getString("username")+"!!",Toast.LENGTH_SHORT).show();
            mv.invalidate();
        }
    };



    private Emitter.Listener enterNewUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            String receivedData = args[0].toString();
            Log.d("JI","asdf");
//             your code...
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("name",receivedData);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    private Emitter.Listener checkDead = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.disconnect();
            Toast.makeText(mContext,"You are DEAD!!",Toast.LENGTH_SHORT).show();

        }
    };

    private int absolute(int x){
        if(x<0) return -x;
        else return x;
    }

    private Emitter.Listener getArray = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String getData = args[0].toString();

            // JSONObject m = new JSONObject()
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(getData);
            JsonArray jsonArray = element.getAsJsonObject().get("array").getAsJsonArray();
            JsonObject jsonObjectX = element.getAsJsonObject().get("Xs").getAsJsonObject();
            JsonObject jsonObjectY = element.getAsJsonObject().get("Ys").getAsJsonObject();
            int index;
            if(jsonObjectX.size()==1){
                otherid = 0;
                curXuser=jsonObjectX.get(String.valueOf(accountid)).getAsInt();
                Log.d(">>>>>>>>>",String.valueOf(curXuser));
                curYuser=jsonObjectY.get(String.valueOf(accountid)).getAsInt();
                if(isStart) {
                    initXuser = curXuser;
                    initYuser = curYuser;
                    isStart =  false;
                }
                infoArray = new ArrayList<Integer>();
                for(JsonElement jsonElement:jsonArray){
                    infoArray.add(Integer.valueOf(jsonElement.toString()));
                }
                index=0;
                for(int i:infoArray){
                    if(absolute(i)==accountid){
                        Board.get(index).setColor(userColor);
                        Board.get(index).setFilled(true);
                    }
                    else{
                        Board.get(index).setFilled(false);
                    }
                    index++;
                }
            }

            else{
                for(Object key:jsonObjectX.keySet()){
                    int keyInt = Integer.valueOf(key.toString());
                    Log.d(">>>>>>>>>>",String.valueOf(keyInt));
                    //first check whether other user is in the same game or not
                    if(keyInt != accountid){
                        otherid = keyInt;
                    }
                }
                curXuser=jsonObjectX.get(String.valueOf(accountid)).getAsInt();
                curXother=jsonObjectX.get(String.valueOf(otherid)).getAsInt();
                curYuser=jsonObjectY.get(String.valueOf(accountid)).getAsInt();
                curYother=jsonObjectY.get(String .valueOf(otherid)).getAsInt();
                infoArray = new ArrayList<Integer>();
                for(JsonElement jsonElement:jsonArray){
                    infoArray.add(Integer.valueOf(jsonElement.toString()));
                }
                index=0;
                for(int i:infoArray){
                    if(i==accountid || i==-accountid){
                        Board.get(i).setColor(userColor);
                        Board.get(i).setFilled(true);
                    }
                    else if(i==otherid||i==-otherid){
                        Board.get(i).setColor(user2Color);
                        Board.get(i).setFilled(true);
                    }
                    else{
                        Board.get(i).setFilled(false);
                    }
                    index++;
                }
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("name",data.toString());
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
        mSocket.emit("exiting_game",json.toString());
        mSocket.disconnect();
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

    /**
     *      try{
     *             json.put("username", username);
     *             json.put("roomName", roomName);
     *         }catch(Exception e){
     *         }
     *         mSocket.on("enter_new_user", enterNewUser);
     *         mSocket.emit("userContext",json.toString());
     */
    //TODO: 201901080 게임 화면 띄우기
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while(true){
                JSONObject json = new JSONObject();
                try{
                    json.put("userId", accountid);
                    json.put("roomName", roomName);
                    json.put("direction",MyView.direction);
                }catch(Exception e){
                }
                Log.d(">>>>>>>>>>>>>>","isConnected");
                mSocket.emit("userContext",json.toString());
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Thread drawThread = new Thread(){
        @Override
        public void run() {
            try {
                synchronized (this) {
                    wait(5000);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mv.invalidate();
                        }
                    });

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    };
}
