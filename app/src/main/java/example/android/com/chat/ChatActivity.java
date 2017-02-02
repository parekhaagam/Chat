package example.android.com.chat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    static Client client;
    static int tcpPort = 27960;
    static String ip = "192.168.43.227";
    static boolean messageRecd = false;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private Button mSendButton;
    private String mUsername = "Client";
    final List<FriendlyMessage> friendlyMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);


        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                friendlyMessages.add(new FriendlyMessage(mMessageEditText.getText().toString(), "Mobile"));
                mMessageAdapter.notifyDataSetChanged();
                //client.sendTCP(new PacketMessage().message = mMessageEditText.getText().toString());
                new ClientSend().execute(mMessageEditText.getText().toString());
                mMessageEditText.setText("");

                // Clear input box

            }
        });


        new ClientConnect().execute();

    }

    class ClientSend extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            PacketMessage p = new PacketMessage();
            p.message = strings[0];
            Log.d("client", strings[0]);
            client.sendTCP(p);

            return null;
        }
    }

    class ClientConnect extends AsyncTask<Void, String, Void> {

        @Override
        protected void onProgressUpdate(String... values) {
            //values[]
            if(!client.isConnected()){
                mMessageEditText.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
            }
            else{
                mMessageEditText.setEnabled(true);
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            mMessageAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                client = new Client();
                client.getKryo().register(PacketMessage.class);

                Log.d("Client", "Client is starting");
                client.start();
                client.connect(5000, ip, tcpPort);
                publishProgress("");
                Log.d("Client", "It is connected to server");
                client.addListener(new Listener() {

                    @Override
                    public void connected(Connection connection) {
                        //   super.connected(connection);
                       publishProgress("connected");
                    }

                    @Override
                    public void disconnected(Connection connection) {
                        // super.disconnected(connection);
                        messageRecd = true;
                        publishProgress("disconnected");

                        publishProgress("disconnected");
                    }

                    @Override
                    public void received(Connection connection, Object object) {
                        try {
                            PacketMessage recd = (PacketMessage) object;
                            System.out.println("Message from Server:" + recd.message);
                            Log.d("Client", "Message frm Client:" + recd.message);
                            friendlyMessages.add(new FriendlyMessage(recd.message, "Desktop"));
                            publishProgress("update");
                        } catch (Exception ex) {
                            Log.d("Client", "It is not of Packet Message type");
                        }

                    }
                });

                while (!messageRecd) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {

                    }

                }
            } catch (IOException ex) {
                Log.d("IOException", ex.getMessage());
                messageRecd = false;
            }
            return null;
        }
    }
}
