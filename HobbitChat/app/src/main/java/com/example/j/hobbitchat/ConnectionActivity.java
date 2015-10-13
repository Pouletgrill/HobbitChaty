package com.example.j.hobbitchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import static java.lang.Integer.parseInt;

public class ConnectionActivity extends AppCompatActivity {

    Button Enter;
    Button Effacer;
    EditText TB_Name;
    RadioButton RB_Comte;
    RadioButton RB_Mordor;
    RadioButton RB_Isengard;
    EditText TB_Port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        Enter = (Button) findViewById(R.id.BTN_Enter);
        Effacer = (Button) findViewById(R.id.BTN_Effacer);
        TB_Name = (EditText) findViewById(R.id.ET_Username);
        RB_Comte = (RadioButton) findViewById(R.id.RB_Comte);
        RB_Mordor = (RadioButton) findViewById(R.id.RB_Mordor);
        RB_Isengard = (RadioButton) findViewById(R.id.RB_Isengard);
        TB_Port = (EditText) findViewById(R.id.ET_Port);

        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Envoyer(v);

            }
        });
        Effacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TB_Name.setText("");
                TB_Port.setText("");

            }
        });
    }

    public void Envoyer(View v)
    {
        if(TB_Name.length() <= 0)
        {
            Toast message = Toast.makeText( ConnectionActivity.this,
                    getResources().getText(R.string.T_Invalide_Name), Toast.LENGTH_SHORT);
            message.show();
        }
        else if(parseInt(TB_Port.getText().toString()) < 1024 || parseInt(TB_Port.getText().toString()) > 65535)
        {
            Toast message = Toast.makeText( ConnectionActivity.this,
                    getResources().getText(R.string.T_Invalide_Port), Toast.LENGTH_SHORT);
            message.show();
        }
        else
        {
            Intent intent = new Intent(this, ComActivity.class);

            intent.putExtra("Username", TB_Name.getText().toString());
            intent.putExtra("Port", TB_Port.getText().toString());

            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
