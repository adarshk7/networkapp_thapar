package edu.thapar.networkapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private final static String TAG = "NetApp";
	private final static Integer PORT = 14000;
	
	private Socket sock;
	
	private Button m_sendButton;
	private Button m_connectButton;
	
	private EditText m_ipEditText;
	private EditText m_msgEditText;
	
	private LinearLayout m_receivedMessagesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        m_sendButton = (Button) findViewById(R.id.sendButton);
        m_connectButton = (Button) findViewById(R.id.connectButton);
        
        m_ipEditText = (EditText) findViewById(R.id.ipEditText);
        m_msgEditText = (EditText) findViewById(R.id.msgEditText);
        
        m_receivedMessagesLayout = (LinearLayout) findViewById(R.id.receiveMsgLayout);
        
        m_connectButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread() {
					public void run() {
						try {
							// Create Socket address for configuring socket configuration
							SocketAddress sockaddr = new InetSocketAddress(m_ipEditText.getText().toString(), PORT);
				            
				            // Create socket Object
				            sock = new Socket();
				 
				            // if timeout is reached and no response is received, it will throw socket exception
				            int timeoutMs = 2000;   // in milliseconds
				 
				            // Initiate socket connection to server
				            sock.connect(sockaddr, timeoutMs);
						} catch (UnknownHostException e) {
				       		e.printStackTrace();
				       	} catch (SocketTimeoutException e) {
				       		e.printStackTrace();
				       	} catch (IOException e) {
				       		e.printStackTrace();
				       	}
						
						
						try {
							Log.d(TAG, "Socket Details: " + sock.toString());
							
            				BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
       		     			String str;
            				while ((str = rd.readLine()) != null) {
            					final String str_new = str;
            					runOnUiThread(new Runnable() {
									public void run() {
										TextView newMsg = new TextView(getApplicationContext());
	            						newMsg.setText(str_new);
	            						
	            						newMsg.setLayoutParams(new LayoutParams (
	            								LayoutParams.MATCH_PARENT,
	            								LayoutParams.WRAP_CONTENT));
	            						m_receivedMessagesLayout.addView(newMsg);
									}
								});
            				}
        				} catch (IOException e) {
            				e.printStackTrace();
        				}
					}
				}.start();
			}
		});
        
        m_sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
        	    	// Create Buffered Writer object to write String or Byte to socket output stream
        	    	BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        	    	String command = m_msgEditText.getText().toString();
        	    	wr.write(command);

        	    	// Flushing the writer
        	    	wr.flush();
		
        		} catch (IOException e) {
        		    e.printStackTrace();
        		}
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}