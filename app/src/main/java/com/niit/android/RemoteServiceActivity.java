package com.niit.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RemoteServiceActivity extends Activity implements View.OnClickListener {
	/** Messenger for communicating with the service. */
    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;

    Button btn;

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
            Toast.makeText(getApplicationContext(), "Bound", Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    public void sayHello() {
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msgHello = Message.obtain(null, 0, 0, 0);
        try {
            mService.send(msgHello);
            try {
                Thread.sleep(10000);
            }
            catch (InterruptedException ex) {
                return;
            }
            Message msgGoodbye = Message.obtain(null, 1, 0, 0);
            mService.send(msgGoodbye);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btn = (Button)findViewById(R.id.sayhello);
        btn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        Intent intent = new Intent();
        intent.setClassName("com.niit.android.service", "com.niit.android.service.RemoteService");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void onClick(View v)
    {
    	sayHello();
    }
}