package mx.dev.franco.jobschedulertest;

import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static mx.dev.franco.jobschedulertest.MainActivity.Constants.DURATION;

public class MainActivity extends AppCompatActivity {
    //LocalBroadcast manager make sure that our application
    //can share information with intents only inside our application,
    //besides is more efficient use this LocalBroadcastManager instead
    //the system.
    private LocalBroadcastManager mLocalBroadcastManager;
    //This object is going to receive the intents
    //from other component in our app that has sent it, in this
    //case PeriodicTaskService is the one who sends it
    private PeriodicTaskResponseReceiver mPeriodicTaskResponseReceiver;
    //If we have many actions for handling different responses,
    //is needed to create this filters
    private IntentFilter mIntentFilter;
    //text view that shows information
    private TextView mTextView;
    //This job scheduler is need it for cancel tasks
    private JobScheduler mJobScheduler;
    //Fabs to start or stop scheduled tasks
    private FloatingActionButton mFabStart;
    private FloatingActionButton mFabStop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextView = (TextView) findViewById(R.id.textview_duration);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        //create our receiver
        mPeriodicTaskResponseReceiver = new PeriodicTaskResponseReceiver();
        //set this action to this filter
        mIntentFilter = new IntentFilter(Constants.ACTION_UPDATE_UI);
        //get the job scheduler
        mJobScheduler = (JobScheduler) getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);


        mFabStart = (FloatingActionButton) findViewById(R.id.fab_start);
        mFabStop = (FloatingActionButton) findViewById(R.id.fab_stop);

        mFabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextView.setText(getString(R.string.starting_execution));
                Job.scheduleJob(MainActivity.this, 0);
                mFabStart.hide();
                mFabStop.show();
            }
        });

        mFabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJobScheduler.cancelAll();
                mFabStop.hide();
                mFabStart.show();
                Toast.makeText(MainActivity.this, getString(R.string.cancel_jobs),Toast.LENGTH_SHORT).show();
                mTextView.setText(getString(R.string.cancel_jobs));
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        //Here we register our receiver and their filters
        mLocalBroadcastManager.registerReceiver(mPeriodicTaskResponseReceiver, mIntentFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        //When our activity enters in onPause state,
        //is useful deregister receivers for saving battery
        mLocalBroadcastManager.unregisterReceiver(mPeriodicTaskResponseReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * This class handles our responses coming from PeriodicTaskService,
     * in this way we are decoupling components that should not or can not not modify UI
     * directly.
     * Is good practice decouple components that only runs code without
     * UI from components that have UI.
     */
    public class PeriodicTaskResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //get the next execution delay
            int duration = intent.getIntExtra(DURATION, 0);
            String msg = getString(R.string.info) + " " + duration + " " + getString(R.string.seconds);
            //Here we can modify its TextView value,
            //adapt to your logic
            mTextView.setText(msg);

        }
    }



    //Constants for better organization of code
    public static class Constants{
        public static final String ACTION_UPDATE_UI = BuildConfig.APPLICATION_ID + "action_update_ui";

        public static final String DURATION = "duration";
    }
}
