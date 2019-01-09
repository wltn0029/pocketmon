package com.example.viewpagerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
//import io.socket.client.IO;
//import io.socket.client.Socket;
//import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class GameListActivity extends AppCompatActivity {
    private Button start;
    private TextView output;
    private OkHttpClient client;
    private Socket mSocket;
    EditText edit;
    Map<String,String> roomlist;
    ListView list;
    boolean goGame;
    String username;
    int accountid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        goGame = false;
        list = findViewById(R.id.listview_game);
        roomlist = new HashMap<String,String>();
        Intent i = getIntent();
        username = i.getStringExtra("name");
        accountid = i.getIntExtra("userAccountID",0);


        edit = (EditText)findViewById(R.id.edit_room) ;
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
                                     public void onClick(View v) {
                                         //이곳에 버튼 클릭시 일어날 일을 적습니다.
                                         Log.d(">>>>>>>>>>>>>>>> 소켓연결",mSocket.connected()+"");
                                         String roomName = edit.getText().toString();

                                         mSocket.emit("create_room", roomName);

                                     }
                                 }
        );
        try {
            mSocket = IO.socket("http://143.248.140.251:9389");
        } catch (URISyntaxException e) {
            Log.e("socket", "토신 불가");
        }
        mSocket.on("connect",onConnect);
        mSocket.connect();
        mSocket.on("update_game_list",updateGameList);
        mSocket.emit("connecting",accountid+"");
        JSONObject data = new JSONObject();
        try {
            data.put("data", "asdfasdf");
            mSocket.emit("clientMessage", "hihi");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        mSocket.on("message", onMessageReceived);
        Log.d("실행되남","");
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parentView, View view, int position, long id){
                        TextView textview = (TextView) view.findViewById(R.id.text1);
                        String text = textview.getText().toString();
                        SocketHandler.setSocket(mSocket);
                        JSONObject json = new JSONObject();

                        goGame = true;
                        Intent i = new Intent(GameListActivity.this, GameActivity.class);
                        i.putExtra("username",username);
                        i.putExtra("accountid",accountid);
                        i.putExtra("roomName", text);
                        startActivity(i);
                    }
        }

        );
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.emit("connecting",accountid+"");
    }

    // Socket서버에 connect 된 후, 서버로부터 전달받은 'Socket.EVENT_CONNECT' Event 처리.
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // your code...
            Log.d(">>>>>>>>>>>>>>>> 소켓연결",mSocket.connected()+"");
            Log.d("등어감",mSocket.connected()+"");
            mSocket.emit("my broadcast event", "asdf");

        }
    };
    // 서버로부터 전달받은 'chat-message' Event 처리.
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            JSONObject receivedData = (JSONObject) args[0];
            Log.d("JI","asdf");
            // your code...
        }
    };

    private Emitter.Listener makeList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            JSONObject receivedData = (JSONObject) args[0];
            Log.d("JI",receivedData.toString());
            // your code...
        }
    };

    private Emitter.Listener updateGameList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
           // Toast.makeText(getApplicationContext(),"새로운 방 생성되었습니다.",Toast.LENGTH_SHORT).show();
            JSONObject receivedData = (JSONObject) args[0];
            JSONObject returnSchool;
            roomlist = new HashMap<String,String>();
            try{
                returnSchool = (JSONObject)receivedData.get("data");

                Iterator i = returnSchool.keys();
                while (i.hasNext()) {
                    String key = i.next().toString();
                    String value = returnSchool.get(key).toString();
                    roomlist.put(key,value);
                }
            }catch (JSONException e){

            }
            Message msg = handler.obtainMessage();

            handler.sendMessage(msg);
            Log.d("JI",receivedData.toString());

            // your code...
        }
    };

    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            // 원래 하려던 동작 (UI변경 작업 등)
            MapAdapter adapter = new MapAdapter(roomlist);
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    };

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(goGame=false){
            mSocket.emit("disconnecting",0+"");
            mSocket.disconnect();
            mSocket.off("new message");
        }else {
            mSocket.emit("changing", 0+"");
        }
        Log.d("나감","");
    }
}