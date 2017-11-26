package mx.dev.franco.jobschedulertest;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by franco on 6/07/17.
 */

public class PeriodicTaskService extends JobService {
    public static final String TAG = PeriodicTaskService.class.getName();
    /**
     * Is called by the system when it is time
     * for your job to execute. If your task is short and simple,
     * feel free to implement the logic directly in onStartJob()
     * and return false when you are finished, to let the system
     * know that all work has been completed. But if you need
     * to do a more complicated task, like connecting to the network,
     * you’ll want to kick off a background thread and return true,
     * letting the system know that you have a thread still running
     * and it should hold on to your wakelock for a while longer.
     * https://medium.com/google-developers/scheduling-jobs-like-a-pro-with-jobscheduler-286ef8510129
     * @param params
     * @return true for execute the code
     */
    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG,"onStartJob");
        //here is where your code is going to be executed
        //when are met your requirements
        yourMethod(params);
        return true;
    }

    private void yourMethod(JobParameters params) {
        Log.d(TAG,"yourMethod is executing");

        // TODO: Put the code that you want to execute

        /**
         * Your code
         */


        //retrieve duration and depending of previous time
        //set, establish new delay to execute your next method.
        PersistableBundle extraInfo = params.getExtras();
        int previousDuration = extraInfo.getInt("duration");
        int newSchedulingTime = 0;

        if(previousDuration ==  0) {
            newSchedulingTime = 7000;
        }
        else if(previousDuration == 7000){
            newSchedulingTime = 9000;
        }
        else if(previousDuration == 9000) {
            newSchedulingTime = 7000;
        }
        //pass true to re schedule current task,
        //false if not. In this case finish it.
        jobFinished(params, false);


        //After finish previous task,
        //re schedule new Job
        Log.d(TAG, "yourMethod is going to execute after " + newSchedulingTime + " " + getString(R.string.seconds) + "\n\n");
        Toast.makeText(this, getString(R.string.info) + newSchedulingTime + " " +getString(R.string.seconds), Toast.LENGTH_SHORT).show();

        //Send this intent with LocalBroadcastManager
        //this ensures safe communication because
        //the intents are sent only inside our application.
        //The intent will be sent inside our app and
        //processed by any interested component in our app
        //that has registered an IntentFilter with the action MainActivity.Constants.ACTION_UPDATE_UI,
        //in our case MainActivity.
        Intent intent = new Intent();
        intent.setAction(MainActivity.Constants.ACTION_UPDATE_UI);
        intent.putExtra(MainActivity.Constants.DURATION, newSchedulingTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        Job.scheduleJob(this, newSchedulingTime);
    }

    /**
     * Is called by the system if the job is cancelled before being finished.
     * This generally happens when your job conditions are no longer being met,
     * such as when the device has been unplugged or if WiFi is no longer available.
     * So use this method for any safety checks and clean up you may need to do
     * in response to a half-finished job. Then, return true if you’d like the
     * system to reschedule the job, or false if
     * it doesn’t matter and the system will drop this job.
     * https://medium.com/google-developers/scheduling-jobs-like-a-pro-with-jobscheduler-286ef8510129
     * @param params
     * @return
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"onStopJob, " + getString(R.string.stop_previous));
        Toast.makeText(this, getString(R.string.stop_previous), Toast.LENGTH_SHORT).show();
        return false;
    }

}
