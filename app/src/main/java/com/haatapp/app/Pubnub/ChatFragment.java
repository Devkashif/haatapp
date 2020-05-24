package com.haatapp.app.Pubnub;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.haatapp.app.R;
import com.haatapp.app.helper.GlobalData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.haatapp.app.build.configure.BuildConfigure.PUBNUB_CHANNEL_NAME;
import static com.haatapp.app.build.configure.BuildConfigure.PUBNUB_PUBLISH_KEY;
import static com.haatapp.app.build.configure.BuildConfigure.PUBNUB_SUBSCRIBE_KEY;

/**
 * Created by santhosh@appoets.com on 03-10-17.
 *
 */
public class ChatFragment extends Fragment implements View.OnClickListener {

    Gson gson;
    String username;
    Context context;
    PubNub pubnub;
    String TAG = "ChatFragment";
    private ListView mChatView;
    private EditText etMessage;
    private ImageView btnSend;
    ChatMessageAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        setHasOptionsMenu(true);
        gson = new Gson();
        context=getActivity();
        if (GlobalData.profileModel != null) {
            username = GlobalData.profileModel.getName();
            mChatView = (ListView) view.findViewById(R.id.chat_view);
            mAdapter = new ChatMessageAdapter(getActivity(), new ArrayList<ChatMessage>());
            mChatView.setAdapter(mAdapter);

            etMessage = (EditText) view.findViewById(R.id.et_message);
            btnSend = (ImageView) view.findViewById(R.id.btn_send);
            btnSend.setOnClickListener(this);

            PNConfiguration pnConfiguration = new PNConfiguration();
            pnConfiguration.setPublishKey(PUBNUB_PUBLISH_KEY);
            pnConfiguration.setSubscribeKey(PUBNUB_SUBSCRIBE_KEY);
            PUBNUB_CHANNEL_NAME=GlobalData.isSelectedOrder.getId().toString();
            pubnub = new PubNub(pnConfiguration);
            pubnub.history()
                    .channel(PUBNUB_CHANNEL_NAME)
                    .count(20)
                    .async(new PNCallback<PNHistoryResult>() {
                        @Override
                        public void onResponse(PNHistoryResult result, PNStatus status) {
                            System.out.println(result + " " + status);
                            if (!status.isError() && result != null) {
                                List<PNHistoryItemResult> list = result.getMessages();
                                Collections.reverse(list);
                                for (PNHistoryItemResult object : list) {
                                    System.out.println(object.getEntry());
                                    try {
                                        String message = object.getEntry().toString();
                                        if(message.contains("nameValuePairs")){
                                            try {
                                                JSONObject jsonObject = new JSONObject(message);
                                                message = jsonObject.optJSONObject("nameValuePairs").toString();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        MyMessage messageObject = gson.fromJson(message, MyMessage.class);

                                        if (messageObject.getType().equals("user")) {
                                            addToSendMessage(messageObject.getMessage(), 0);
                                        } else {
                                            addToReceiveMessage(messageObject.getMessage(), 0);
                                        }

                                    } catch (IllegalStateException | JsonSyntaxException exception) {
                                        exception.printStackTrace();
                                    }
                                }
                            }
                        }
                    });

            pubnub.addListener(new SubscribeCallback() {
                @Override
                public void status(PubNub pubnub, PNStatus status) {

                    if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                        // This event happens when radio / connectivity is lost
                    } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {

                        // Connect event. You can do stuff like publish, and know you'll get it.
                        // Or just use the connected event to confirm you are subscribed for
                        // UI / internal notifications, etc

                        if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {

                        }
                    } else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {

                        // Happens as part of our regular operation. This event happens when
                        // radio / connectivity is lost, then regained.
                    } else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {

                        // Handle messsage decryption error. Probably client configured to
                        // encrypt messages and on live data feed it received plain text.
                    }
                }

                @Override
                public void message(PubNub pubnub, PNMessageResult message) {
                    // Handle new message stored in message.message
                    if (message.getChannel() != null) {
                        System.out.println(message.getMessage());
                        try {
                            String mess = message.getMessage().toString();
                            final MyMessage messageObject = gson.fromJson(mess, MyMessage.class);
                            if (!messageObject.getType().equals("user")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addToReceiveMessage(messageObject.getMessage(), -1);
                                    }
                                });
                            }

                            //System.out.println("nameValuePairs "+jsonObj.getJSONObject("nameValuePairs"));
                        } catch (IllegalStateException | JsonSyntaxException exception) {
                            exception.printStackTrace();
                        }

                        // Message has been received on channel group stored in
                        // message.getChannel()
                    } else {
                        // Message has been received on channel stored in
                        // message.getSubscription()
                    }

                }

                @Override
                public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                }


            });

            pubnub.subscribe().channels(Arrays.asList(PUBNUB_CHANNEL_NAME)).execute();
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        if (btnSend.getId() == view.getId()) {
            String myText = etMessage.getText().toString().trim();
            if (myText.length() != 0) {
                sendMessage(myText);
                etMessage.setText("");
                JsonObject jObj = new JsonObject();
                jObj.addProperty("type", "user");
                jObj.addProperty("message", myText);

                pubnub.publish().channel(PUBNUB_CHANNEL_NAME).message(jObj).async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        // Check whether request successfully completed or not.
                        if (!status.isError()) {
                        } else {
                        }
                    }
                });
            } else {
                Toast.makeText(context, "Please enter message", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);
        //  sendMessageToServer(message);

        // mimicOtherMessage(message);
    }


    private void addToSendMessage(String message, int pos) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        if (pos >= 0)
            mAdapter.insert(chatMessage, pos);
        else mAdapter.add(chatMessage);
    }

    private void addToReceiveMessage(String message, final int pos) {
        final ChatMessage chatMessage = new ChatMessage(message, false, false);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (pos >= 0)
                    mAdapter.insert(chatMessage, pos);
                else mAdapter.add(chatMessage);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        pubnub.subscribe().channels(Arrays.asList(PUBNUB_CHANNEL_NAME)).execute();
        Log.d(TAG, "subscribed");
    }

    @Override
    public void onPause() {
        super.onPause();
        pubnub.unsubscribe().channels(Arrays.asList(PUBNUB_CHANNEL_NAME)).execute();
        Log.d(TAG, "Un subscribed");
    }
}