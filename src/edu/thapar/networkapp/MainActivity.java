/*
 *  ANDROID BASED NETWORK APPLICATION
 * ------------------------------------ 
 * By Adarsh Krishnan 101006007
 * &  Ankit Goyal     101006017
 * 
 * For final year minor project UEC 891
 * 
 * github: /adarshk7/networkapp_thapar
 * 
 */


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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private final static String TAG = "NetApp";
	private final static Integer PORT = 14000;// We will use TCP port 14000
	
	private Socket sock;                      // Socket is global for global access
	
	private Button m_sendButton;
	private Button m_connectButton;
	
	private EditText m_ipEditText;
	private EditText m_msgEditText;
	
	private InputMethodManager m_imm;
	
	private LinearLayout m_receivedMessagesLayout;
	
	/* Method: create_and_add_textview_to_linearlayout
	 * -----------------------------------------------
	 * @param s: String to add in textview
	 * @param l: linearlayout to add textview in
	 * 
	 * Will add a textview with string s in linear layour l
	 * 
	 */
	private void create_and_add_textview_to_linearlayout(String s, LinearLayout l) {
		TextView t = new TextView(getApplicationContext());
		t.setText(s);
		
		t.setLayoutParams(new LayoutParams (
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		l.addView(t);
	}
	
	/* Method: initialize_ui_components
	 * --------------------------------
	 * Will initialize all UI component
	 * for this activity.
	 * 
	 */
	private void initialize_ui_components() {
		m_sendButton = (Button) findViewById(R.id.sendButton);
        m_connectButton = (Button) findViewById(R.id.connectButton);
        
        m_ipEditText = (EditText) findViewById(R.id.ipEditText);
        m_msgEditText = (EditText) findViewById(R.id.msgEditText);
        
        m_receivedMessagesLayout = (LinearLayout) findViewById(R.id.receiveMsgLayout);
	
        m_imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	}
	
	/* Method: set_ui_listeners
	 * ------------------------
	 * Sets UI listeners.
	 * 
	 */
	private void set_ui_listeners() {
		m_connectButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// Giving focus to message editview
				// and hiding keyboard input.
				m_ipEditText.clearFocus();
				m_msgEditText.requestFocus();
				m_imm.hideSoftInputFromWindow(m_msgEditText.getWindowToken(), 0);
				
				new Thread() {
					public void run() {
						try {
							// Create Socket address for configuring socket configuration
							SocketAddress sockaddr = new InetSocketAddress(
									m_ipEditText.getText().toString(), 
									PORT);
				            
				            // Create & assign socket Object
				            sock = new Socket();
				 
				            // if timeout is reached and no response is received, it will throw socket exception
				            int timeoutMs = 2000;   // in milliseconds
				 
				            // Initiate socket connection to server
				            sock.connect(sockaddr, timeoutMs);
						} catch (UnknownHostException e) {
							create_and_add_textview_to_linearlayout(
									"ERROR: Host Unknown", 
									m_receivedMessagesLayout);
				       		e.printStackTrace();
				       	} catch (SocketTimeoutException e) {
				       		create_and_add_textview_to_linearlayout(
									"ERROR: Socket Timeout, retry", 
									m_receivedMessagesLayout);
				       		e.printStackTrace();
				       	} catch (IOException e) {
				       		create_and_add_textview_to_linearlayout(
									"ERROR: IO error", 
									m_receivedMessagesLayout);
				       		e.printStackTrace();
				       	}
						Log.d(TAG, "Socket Details: " + sock.toString());
						
						// Try catch block for receiving data
						try {
            				BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
       		     			String str;
            				while ((str = rd.readLine()) != null) {
            					final String str_new = "Received: " + str;
            					runOnUiThread(new Runnable() {
									public void run() {
										create_and_add_textview_to_linearlayout(
												str_new, 
												m_receivedMessagesLayout);
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
				// Try catch for sending data
				try {
        	    	// Create Buffered Writer object to write String or Byte to socket output stream
        	    	BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        	    	String sendstring = m_msgEditText.getText().toString();
        	    	wr.write(sendstring);
        	    	
        	    	create_and_add_textview_to_linearlayout(
							"Sent: " + sendstring, 
							m_receivedMessagesLayout);
        	    	Log.d(TAG, "User has sent: " + sendstring);
        	    	
        	    	// Hiding key soft keyboard and clearing message box.
        	    	m_imm.hideSoftInputFromWindow(m_msgEditText.getWindowToken(), 0);
        	    	m_msgEditText.setText("");
        	    	
        	    	// Flushing the writer, data will be sent now.
        	    	wr.flush();
		
        		} catch (IOException e) {
        		    e.printStackTrace();
        		}
			}
		});
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initialize_ui_components();
        
        set_ui_listeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}