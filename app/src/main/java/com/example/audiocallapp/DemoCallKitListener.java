package com.example.audiocallapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.haoge.easyandroid.easy.EasyExecutor;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TimeZone;

import io.agora.chat.callkit.bean.EaseUserAccount;
import io.agora.chat.callkit.general.EaseCallEndReason;
import io.agora.chat.callkit.general.EaseCallError;
import io.agora.chat.callkit.general.EaseCallType;
import io.agora.chat.callkit.listener.EaseCallGetUserAccountCallback;
import io.agora.chat.callkit.listener.EaseCallKitListener;
import io.agora.chat.callkit.listener.EaseCallKitTokenCallback;
import io.agora.cloud.EMHttpClient;
import io.agora.exceptions.ChatException;
import io.agora.util.EMLog;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class DemoCallKitListener implements EaseCallKitListener {

    private final String TAG = getClass().getSimpleName();
//    //The URL here is for the demo example, and the actual project users should obtain it from their App Server（此处url为demo示例所用，实际项目用用户应该从自己的App Server去获取）
    private String tokenUrl = "http://" + "agora-token-service-production-d091.up.railway.app" + "/token/rtc";
    private String uIdUrl = "http://" + "agora-token-service-production-d091.up.railway.app" + "/agora/channel/mapper";

 //   private UsersManager mUsersManager;
     Context mContext;
     final EasyExecutor executor;
     Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Object obj = msg.obj;
            if( obj instanceof  String) {
                Toast.makeText(mContext, msg.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public DemoCallKitListener(Context context) {
        this.mContext = context;
      //  this.mUsersManager = usersManager;
        executor = EasyExecutor.newBuilder(0)
                .build();
    }
    @Override
    public void onInviteUsers(EaseCallType callType, String[] existMembers, JSONObject ext) {

    }

    @Override
    public void onEndCallWithReason(EaseCallType callType, String channelName, EaseCallEndReason reason, long callTime) {
        EMLog.d(TAG, "onEndCallWithReason" + (callType != null ? callType.name() : " callType is null ") + " reason:" + reason + " time:" + callTime);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String callString = mContext.getString(R.string.ease_call_duration);
        callString += formatter.format(callTime);
        Message message = handler.obtainMessage();
        switch (reason) {
            case EaseCallEndReasonHangup://Hang up normally
            case EaseCallEndReasonRemoteCancel: //The other party cancels the call
                message.obj=callString;
                break;
            case EaseCallEndReasonCancel://cancel the call yourself
                break;
            case EaseCallEndReasonRefuse://request declined
                message.obj=mContext.getString(R.string.demo_call_end_reason_refuse);
                break;
            case EaseCallEndReasonBusy: //busy
                message.obj=mContext.getString(R.string.demo_call_end_reason_busy);
                break;
            case EaseCallEndReasonNoResponse://not responding
                break;
            case EaseCallEndReasonRemoteNoResponse://No response from peer
                message.obj=mContext.getString(R.string.demo_call_end_reason_busy_remote_no_response);
                break;
            case EaseCallEndReasonHandleOnOtherDeviceAgreed://other devices connected
                message.obj=mContext.getString(R.string.demo_call_end_reason_other_device_agreed);
                break;
            case EaseCallEndReasonHandleOnOtherDeviceRefused://other devices declined
                message.obj=mContext.getString(R.string.demo_call_end_reason_other_device_refused);
                break;
        }
        handler.sendMessage(message);
    }

    @Override
    public void onReceivedCall(EaseCallType callType, String fromUserId, JSONObject ext) {
        Toast.makeText(mContext, fromUserId, Toast.LENGTH_SHORT).show();
        CallSingleBaseActivity callSingleBaseActivity=new CallSingleBaseActivity();
    }

    @Override
    public void onCallError(EaseCallError type, int errorCode, String description) {
        EMLog.d(TAG, "onCallError" + type.name() + " description:" + description);
        if(type== EaseCallError.PROCESS_ERROR) {
            Toast.makeText(mContext, description+"error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInViteCallMessageSent() {

    }

    @Override
    public void onRemoteUserJoinChannel(String channelName, String userName, int uid, EaseCallGetUserAccountCallback callback) {

    }

//    @Override
//    public void onGenerateRTCToken(String userId, String channelName, EaseCallKitTokenCallback callback) {
//
//        EMLog.d(TAG, "onGenerateToken userId:" + userId + " channelName:" + channelName);
//     //   int agoraUid = mUsersManager.getCurrentUserAgoraUid();
//        StringBuilder url = new StringBuilder(tokenUrl)
//                .append("/channel/")
//                .append(channelName)
//                .append("/agorauid/")
//                .append(0)
//                .append("?")
//                .append("userAccount=")
//                .append(userId);
//
//        //get agora RTC token (get Agora RTC token)
//        getRtcToken(url.toString(), 0, callback);
//    }

//    @Override
//    public void onRemoteUserJoinChannel(String channelName, String userName, int uid, EaseCallGetUserAccountCallback callback) {
//        StringBuilder url = new StringBuilder(uIdUrl)
//                .append("?")
//                .append("channelName=")
//                .append(channelName)
//                .append("&userAccount=")
//                .append(userName);
//        getUserIdByAgoraUid(uid, url.toString(), callback);
//    }
//    private void getUserIdByAgoraUid(int uId, String url, EaseCallGetUserAccountCallback callback) {
//        executor.asyncResult(new Function1<Pair<Integer, String>, Unit>() {
//            @Override
//            public Unit invoke(Pair<Integer, String> response) {
//                if (response != null) {
//                    try {
//                        int resCode = response.first;
//                        if (resCode == 200) {
//                            String responseInfo = response.second;
//                            EaseUserAccount userAccount =null;
//                            if (responseInfo != null && responseInfo.length() > 0) {
//                                try {
//                                    JSONObject object = new JSONObject(responseInfo);
//                                    JSONObject resToken = object.getJSONObject("result");
//                                    Iterator<String> it = resToken.keys();
//                                    while (it.hasNext()) {
//                                        String uIdStr = it.next();
//                                        int uid = Integer.valueOf(uIdStr).intValue();
//                                        String username = resToken.optString(uIdStr);
//                                        if (uid == uId) {
//                                            //Obtain information such as userName, profile picture, and nickname of the current user
//                                            userAccount=new EaseUserAccount(uid, username);
//                                        }
//                                    }
//                                    callback.onUserAccount(userAccount);
//                                } catch (Exception e) {
//                                    e.getStackTrace();
//                                }
//                            } else {
//                                callback.onSetUserAccountError(response.first, response.second);
//                            }
//                        } else {
//                            callback.onSetUserAccountError(response.first, response.second);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    callback.onSetUserAccountError(100, "response is null");
//                }
//                return null;
//            }
//        }).asyncTask(notifier -> {
//            try {
//                Pair<Integer, String> response = EMHttpClient.getInstance().sendRequestWithToken(url, null, EMHttpClient.GET);
//                return response;
//            } catch (ChatException exception) {
//                exception.printStackTrace();
//            }
//            return null;
//        });
//    }



//    private void getRtcToken(String tokenUrl, int agoraUid, EaseCallKitTokenCallback callback) {
//        executor.asyncResult(new Function1<Pair<Integer, String>, Unit>() {
//                    @Override
//                    public Unit invoke(Pair<Integer, String> response) {
//                        if (response != null) {
//                            try {
//                                int resCode = response.first;
//                                if (resCode == 200) {
//                                    String responseInfo = response.second;
//                                    if (responseInfo != null && responseInfo.length() > 0) {
//                                        try {
//                                            JSONObject object = new JSONObject(responseInfo);
//                                            String token = object.getString("accessToken");
//                                            //Set your avatar nickname
//                                          //  setEaseCallKitUserInfo(ChatClient.getInstance().getCurrentUser());
//                                            callback.onSetToken(token, agoraUid);
//                                        } catch (Exception e) {
//                                            e.getStackTrace();
//                                        }
//                                    } else {
//                                        callback.onGetTokenError(response.first, response.second);
//                                    }
//                                } else {
//                                    callback.onGetTokenError(response.first, response.second);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            callback.onSetToken(null, 0);
//                        }
//                        return null;
//                    }
//                })
//                .asyncTask(notifier -> {
//                    try {
//                        Pair<Integer, String> response = EMHttpClient.getInstance().sendRequestWithToken(tokenUrl, null, EMHttpClient.GET);
//                        return response;
//                    } catch (ChatException exception) {
//                        exception.printStackTrace();
//                    }
//                    return null;
//                });
//    }
//    private void setEaseCallKitUserInfo(String userName) {
//     //   EaseUser user = mUsersManager.getUserInfo(userName);
//        EaseCallUserInfo userInfo = new EaseCallUserInfo();
//        if (user != null) {
//            userInfo.setNickName(user.getNickname());
//            userInfo.setHeadImage(user.getAvatar());
//        }
//        EaseCallKit.getInstance().getCallKitConfig().setUserInfo(userName, userInfo);
//    }
}
