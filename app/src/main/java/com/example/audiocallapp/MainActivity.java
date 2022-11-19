package com.example.audiocallapp;

import static io.agora.chat.callkit.general.EaseCallType.SINGLE_VOICE_CALL;

import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import io.agora.ConnectionListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatOptions;
import io.agora.chat.TextMessageBody;
import io.agora.chat.callkit.EaseCallKit;
import io.agora.chat.callkit.bean.EaseCallUserInfo;
import io.agora.chat.callkit.general.EaseCallKitConfig;
import io.agora.chat.callkit.listener.EaseCallKitListener;
import io.agora.chat.callkit.utils.EaseCallFileUtils;
import io.agora.exceptions.ChatException;

public class MainActivity extends AppCompatActivity {

    EaseCallKitListener callKitListener;
    private static final String TOKEN = "007eJxTYGiysVaI56sy33JaWvdB5oeTZWuOLzw4XeZ78y7XPbOZl01UYDBKTDM3NjYwMDcxTTMxNTW2TE2xNDcyMTI2NTFMNDExK5tfntwQyMiQ8M6RkZGBlYGRgYkBxGdgAABvKh0Y";
    private static final String APP_KEY = "61827390#1029949";
    Button callBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callBtn=findViewById(R.id.callBtn);
        initSDK();
        initListener();

        // Construct the CallKitConfig class.
        EaseCallKitConfig callKitConfig = new EaseCallKitConfig();
// Set the call out time (ms).
        callKitConfig.setCallTimeOut(30 * 1000);
// Set the Agora App ID.
        callKitConfig.setAgoraAppId("2af73300745f45539ed972423541a446");
// Set whether to enable token authentication.
           callKitConfig.setEnableRTCToken(true);

// Set the default avatar.
        callKitConfig.setDefaultHeadImage(Integer.toString(R.drawable.wallpaper));
// Set the ringtone file.
        String ringFile = EaseCallFileUtils.getModelFilePath(this,Integer.toString(R.raw.ringtone));
        callKitConfig.setRingFile(ringFile);
// Set the user information.
        Map<String, EaseCallUserInfo> userInfoMap = new HashMap<>();
        userInfoMap.put("03099840120", new EaseCallUserInfo("Hamza", null));
        userInfoMap.put("03013518700", new EaseCallUserInfo("Habibi", null));
        callKitConfig.setUserInfoMap(userInfoMap);
            EaseCallKit.getInstance().init(getApplicationContext(), callKitConfig);
// Register the activity added in the Manifest file.
        EaseCallKit.getInstance().registerVideoCallClass(CallSingleBaseActivity.class);
// Add event listeners to the AgoraChatCallKit.
        callKitListener = new DemoCallKitListener(MainActivity.this);
        EaseCallKit.getInstance().setCallKitListener(callKitListener);
        //The parameters are the username of the contact to be added and the reason for adding
        try {
            ChatClient.getInstance().contactManager().addContact("03099840120","number");
        } catch (ChatException e) {
            e.printStackTrace();
        }

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EaseCallKit.getInstance().startSingleCall(SINGLE_VOICE_CALL, "03099840120", null, CallSingleBaseActivity.class);
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initSDK() {
        ChatOptions options = new ChatOptions();
        // Gets your App Key from Agora Console.
        if(TextUtils.isEmpty(APP_KEY)) {
            Toast.makeText(MainActivity.this, "You should set your AppKey first!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Sets your App Key to options.
        options.setAppKey(APP_KEY);
        // Initializes the Agora Chat SDK.
        ChatClient.getInstance().init(this, options);
        // Makes the Agora Chat SDK debuggable.
        ChatClient.getInstance().setDebugMode(true);
        // Shows the current user.
    }
    private void initListener() {
        // Adds message event callbacks.
        ChatClient.getInstance().chatManager().addMessageListener(messages -> {
            for(ChatMessage message : messages) {
                StringBuilder builder = new StringBuilder();
                builder.append("Receive a ").append(message.getType().name())
                        .append(" message from: ").append(message.getFrom());
                if(message.getType() == ChatMessage.Type.TXT) {
                    builder.append(" content:")
                            .append(((TextMessageBody)message.getBody()).getMessage());
                }

                Log.d("listener", "initListener: "+builder);
            }
        });
        // Adds connection event callbacks.
        ChatClient.getInstance().addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnected() {
                Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnected(int error) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, "OnDisConnected"+"\n"+error, Toast.LENGTH_SHORT).show();
            }

            // This callback occurs when the token expires. When the callback is triggered, the app client must get a new token from the app server and logs in to the app again.
            @Override
            public void onTokenExpired() {
                Toast.makeText(MainActivity.this, "Token Expired", Toast.LENGTH_SHORT).show();
            }
            // This callback occurs when the token is about to expire.
            @Override
            public void onTokenWillExpire() {
                Toast.makeText(MainActivity.this, "Token is about to expire", Toast.LENGTH_SHORT).show();
            }
        });
    }

}