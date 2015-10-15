package com.example.j.hobbitchat;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import java.net.*;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class ComActivity extends AppCompatActivity {

    ScrollView scroll;
    EditText convosText;
    EditText sendText;
    Button BTN_Envoyer;
    CheckBox CB_ip;
    String Username;
    int PORT;
    String DESTINATION;
    static final int LONG_TAMPON = 1024;
    MulticastSocket soc;
    InetAddress adrMulticast;
    public Boolean AsyncStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com);

        scroll = (ScrollView) findViewById(R.id.scrollView);
        convosText = (EditText) findViewById(R.id.convoText);
        sendText = (EditText) findViewById(R.id.sendText);
        BTN_Envoyer = (Button) findViewById(R.id.BTN_Send);
        CB_ip = (CheckBox) findViewById(R.id.CB_IP);

        Intent intent = getIntent();
        Username = intent.getStringExtra("Username");
        PORT = parseInt(intent.getStringExtra("Port"));
        DESTINATION = intent.getStringExtra("Destination");

        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiManager.MulticastLock lock = wifi.createMulticastLock("HobbitChat");
            lock.acquire();
        }

        try
        {
            adrMulticast = InetAddress.getByName(DESTINATION);
            soc = new MulticastSocket();
        }catch (Exception ex)
        {
            System.err.println("Creation du InetMulticast "+ex.getMessage());
        }

        UDP_Ecouteur ecouteur = new UDP_Ecouteur();//démmarage du Thread Asynchrome
        ecouteur.execute();

        BTN_Envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendText.getText().length()>=1 &&sendText.getText().length()<=60)//si la chaine est valid
                {
                    UDP_Envoyeur();
                    sendText.setText("");//effacer le textbox d'envoie
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Le format de la chaine est invalide",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AsyncStarted = false;
    }

    public Boolean ToujoursVivant()
    {
        return AsyncStarted;
    }

    private class UDP_Ecouteur extends AsyncTask<Void, String, Void> {
        byte tampon[] = new byte[LONG_TAMPON];
        MulticastSocket socket;
        DatagramPacket paquet;

        @Override
        protected void onPreExecute() {//Initialisation pour le thread
        super.onPreExecute();
            try {
                paquet = new DatagramPacket(tampon, 0, LONG_TAMPON);
                socket = new MulticastSocket(PORT);
                socket.joinGroup(adrMulticast);
                AsyncStarted = true;
            }catch (Exception ex)
            {
                System.err.println("pre execute ecouteur "+ex.getMessage());
            }

        }

        @Override
        protected Void doInBackground(Void... args) {
            try
            {
                while (AsyncStarted) { //continue t'en que le bool na pas été recrée en false par onPause
                    socket.receive((paquet));
                    String chaine = new String(paquet.getData(),
                            paquet.getOffset(), paquet.getLength() );
                    //Publie la chaine pour l'affichage
                    publishProgress(chaine);

                }
            }
            catch (Exception e)
            {
                System.err.println("Houston we have a problem");
                e.printStackTrace();
                System.exit(1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... valeurs) {
            super.onProgressUpdate();
            String message;
            if (CB_ip.isChecked())//Si le CheckBox Show IP est coché
            {
                message = valeurs[0].substring(0, valeurs[0].indexOf(":"))+         //prend le text avant les :
                        paquet.getSocketAddress()+                                  //prend l'adresse IP
                        ":"+valeurs[0].substring(valeurs[0].lastIndexOf(":") + 1);  //prend le text Après les :
            }
            else
            {
                message = valeurs[0];
            }
            convosText.append(message+"\n");
            scroll.fullScroll(ScrollView.FOCUS_DOWN);//scroll automatique
        }

        @Override
        protected void onPostExecute(Void resultat) {
            try {
                socket.leaveGroup(adrMulticast);//Quitter le group multiThread
            }catch (Exception ex)
            {
                System.err.println("Post execute ecouteur "+ex.getMessage());
            }
            AsyncStarted = false;
        }
    }

    public void UDP_Envoyeur() {
            new Thread( new Runnable() {
                // le code qui peut ralentir l'application est exécuté
                // dans un thread secondaire

                String Chaine = Username+" : "+sendText.getText().toString();
                public void run() {
                    try {

                        byte tampon[] = Chaine.getBytes();

                        DatagramPacket paquet =
                                new DatagramPacket(tampon, 0, tampon.length, adrMulticast, PORT);

                       MulticastSocket socket = new MulticastSocket();
                        socket.send(paquet);
                    } catch (Exception e) {

                        System.err.println("Envoyeur "+ e.getMessage());

                        e.printStackTrace();
                        //System.exit(1);
                    }
                }
            }).start();
    }
}
