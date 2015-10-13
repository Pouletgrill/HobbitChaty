package com.example.j.hobbitchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.net.*;

import java.io.IOException;

public class ComActivity extends AppCompatActivity {

    EditText convosText;
    EditText sendText;
    Button BTN_Envoyer;

    static final String DESTINATION = "127.0.0.1";
    static final int LONG_TAMPON = 1024;
    static final int PORT = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com);


        Intent intent = getIntent();

        String Username_ = intent.getStringExtra("Username");
        String Port_ = intent.getStringExtra("Port");
        convosText = (EditText) findViewById(R.id.convoText);
        sendText = (EditText) findViewById(R.id.sendText);
        BTN_Envoyer = (Button) findViewById(R.id.BTN_Send);


        UDP_Ecouteur ecouteur = new UDP_Ecouteur();
        ecouteur.execute();

        BTN_Envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (sendText.getText().length() > 0) {
                //UDP_Envoyeur ;
                //}
            }
        });
    }

    private class UDP_Ecouteur extends AsyncTask<Void, Void, Void> {
        byte tampon[] = new byte[LONG_TAMPON];
        DatagramSocket socket;
        DatagramPacket paquet;

        @Override
        protected void onPreExecute() {
        super.onPreExecute();
            Toast.makeText(getApplicationContext(),
                    "Début du traitement asynchrone",
                    Toast.LENGTH_SHORT).show();
            try
            {
                paquet = new DatagramPacket(tampon, 0, LONG_TAMPON);
                socket = new DatagramSocket(PORT);
            }
            catch (Exception e)
            {
                System.err.println("Houston we have a problem");
                e.printStackTrace();
                System.exit(1);
            }
        }

        @Override
        protected Void doInBackground(Void... args) {
            try {


                while (true) {
                    socket.receive(paquet);

                    String chaine = new String(paquet.getData(),
                            paquet.getOffset(), paquet.getLength() );

                    System.out.println("Message: " + chaine);
                    System.out.println("Recu de " + paquet.getSocketAddress());
                    System.out.println();
                }
            } catch( Exception e) {
                System.err.println("Houston we have a problem");
                e.printStackTrace();
                System.exit(1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... valeurs) {
            super.onProgressUpdate();

        }

        @Override
        protected void onPostExecute(Void resultat) {

        }
    }

    public void UDP_Envoyeur() {
        new Thread( new Runnable() {
            // le code qui peut ralentir l'application est exécuté
            // dans un thread secondaire
            String Chaine = "Text123";
            public void run() {
                try {
                    //Toast.makeText(getApplicationContext(),"111",Toast.LENGTH_LONG).show();
                    byte tampon[] = Chaine.getBytes();

                    InetAddress adresse = InetAddress.getByName(DESTINATION);

                    DatagramPacket paquet =
                            new DatagramPacket(tampon, 0, tampon.length, adresse, PORT);

                    DatagramSocket socket = new DatagramSocket();
                    socket.send(paquet);
                } catch (Exception e) {

                    System.err.println(e.getMessage());

                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }).start();
    }
}
