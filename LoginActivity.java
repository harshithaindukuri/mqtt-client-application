package system.smartbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {
    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText e1 =(EditText)findViewById(R.id.clientID);
        String clientID = e1.getText().toString().trim();
        client =
                new MqttAndroidClient(getApplicationContext(), "tcp://192.168.0.11:1883",
                        clientID);
        Button b = (Button)findViewById(R.id.connect);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText user = (EditText) findViewById(R.id.username);
                final EditText pass = (EditText) findViewById(R.id.password);
                final String username = user.getText().toString().trim();
                final String password = pass.getText().toString().trim();
                MqttConnectOptions options =new MqttConnectOptions();
                options.setCleanSession(true);
                options.setKeepAliveInterval(60);
                options.setAutomaticReconnect(true);
                options.setUserName(username);
                options.setPassword(password.toCharArray());
                try {
                    client.connect(options, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {

                            DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                            disconnectedBufferOptions.setBufferEnabled(true);
                            disconnectedBufferOptions.setBufferSize(100);
                            disconnectedBufferOptions.setPersistBuffer(false);
                            disconnectedBufferOptions.setDeleteOldestMessages(false);
                            client.setBufferOpts(disconnectedBufferOptions);
                            Toast.makeText(LoginActivity.this, "connected", Toast.LENGTH_SHORT).show();
                            setContentView(R.layout.activity_menu);
                            Button b1 = (Button) findViewById(R.id.publish);
                            b1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String topic = "door";
                                    String payload = "open";
                                    byte[] encodedPayload = payload.getBytes();
                                    try {
                                        encodedPayload = payload.getBytes("UTF-8");
                                        MqttMessage message = new MqttMessage(encodedPayload);



                                        message.setRetained(true);
                                        client.publish(topic, message);
                                        Toast.makeText(LoginActivity.this, "published", Toast.LENGTH_SHORT).show();
                                    } catch (UnsupportedEncodingException | MqttException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            Button b2 = (Button) findViewById(R.id.subscribed);
                            b2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String topic = "message";
                                    int qos = 1;
                                    try {
                                        IMqttToken subToken = client.subscribe(topic, qos);
                                        subToken.setActionCallback(new IMqttActionListener() {
                                            @Override
                                            public void onSuccess(IMqttToken asyncActionToken) {
                                                client.setCallback(new MqttCallbackExtended() {
                                                    @Override
                                                    public void connectComplete(boolean b, String s) {
                                                        Log.w("mqtt", s);
                                                    }

                                                    @Override
                                                    public void connectionLost(Throwable throwable) {

                                                    }

                                                    @Override
                                                    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                                                        Log.w("Mqtt", mqttMessage.toString());
                                                        Toast.makeText(LoginActivity.this, " "+mqttMessage.toString(), Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                                                    }
                                                });

                                            }

                                            @Override
                                            public void onFailure(IMqttToken asyncActionToken,
                                                                  Throwable exception) {
                                                // The subscription could not be performed, maybe the user was not
                                                // authorized to subscribe on the specified topic e.g. using wildcards
                                                Toast.makeText(LoginActivity.this, "not authorized", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (MqttException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            Button  b3 = (Button)findViewById(R.id.previous);
                            b3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        IMqttToken disconToken = client.disconnect();
                                        disconToken.setActionCallback(new IMqttActionListener() {
                                            @Override
                                            public void onSuccess(IMqttToken asyncActionToken) {


                                                // we are now successfully disconnected
                                            }

                                            @Override
                                            public void onFailure(IMqttToken asyncActionToken,
                                                                  Throwable exception) {

                                                // something went wrong, but probably we are disconnected anyway
                                            }
                                        });
                                    } catch (MqttException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(LoginActivity.this,WelcomeActivity.class);
                                    startActivity(intent);

                                }
                            });

                        }


                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            //firewall problem or poor internet
                            Toast.makeText(LoginActivity.this, "please check your username and password", Toast.LENGTH_SHORT).show();
                            setContentView(R.layout.activity_login);

                        }
                    });


                } catch (MqttException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    public void onclickPrevious(View v)
    {
        Intent intent =new Intent(this,WelcomeActivity.class);
        setContentView(R.layout.activity_welcome);
        startActivity(intent);

    }
}
