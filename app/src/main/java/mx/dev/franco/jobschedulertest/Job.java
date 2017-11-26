package mx.dev.franco.jobschedulertest;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Created by franco on 25/11/17.
 * This class encapsulates what you want to execute
 * and the params it needs to execute.
 */

public class Job {
    /**
     * Here we schedule our task and constraints to execute when are met,
     * in this case a periodic task.
     * @param context
     */
    public static void scheduleJob(Context context, int duration){
        //Component we want to execute
        //we need to pass context and name of our PeriodicTaskService class
        //that will execute the task
        ComponentName serviceComponent = new ComponentName(context.getApplicationContext(), PeriodicTaskService.class);
        //the component is passed to the builder of job,
        //this builder object are the requirements
        //needed to execute our task, in this case, we need
        //that our task executes every duration seconds
        JobInfo.Builder builder = new JobInfo.Builder(/*Id of our job*/0, serviceComponent);

        //set this params to retrieve when our job finish and
        //decide how many time has to wait the next execution
        PersistableBundle extraInfo = new PersistableBundle();
        extraInfo.putInt(MainActivity.Constants.DURATION,duration);
        builder.setExtras(extraInfo);

        //minimum latency means the min limit in milliseconds JobScheduler has to wait to execute the code
        builder.setMinimumLatency(duration);
        //setOverrideDeadline means the max limit in milliseconds in which your code is
        //going to execute. Using setMinimumLatency with setOverrideDeadline together,
        //make something like minimum and maximum limit to execute the code, its useful
        //because setPeriodic time for Nougat are 15 minutes, so less than this time
        //is not going to work.
        //For android <= M there is no problem in use setPeriodic (instead of two before), with less than 15 minutes
        builder.setOverrideDeadline(duration);

        //Using setMinimumLatency with setOverrideDeadline together,
        //it makes something like minimum and maximum limit to execute the code, is useful
        //because minimum time for setPeriodic in Nougat are 15 minutes, so if you require
        //to execute your task in lower range of time is not going to work.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //minimum latency means the min limit in milliseconds JobScheduler has to wait to execute the code
            builder.setMinimumLatency(duration);
            //setOverrideDeadline means the max limit in milliseconds in which your code is
            //going to execute.
            builder.setOverrideDeadline(duration);
        }
        //For android <= M there is no problem in use setPeriodic (instead of two before), with less than 15 minutes
        else {
            builder.setPeriodic(duration);
        }


        //now that our builder object has the parameters set
        //is time to schedule it.
        //NOTE: Remember to pass application context with getApplicationContext() instead of Activity context to avoid memory leaks!!!!!!!!!!
        JobScheduler jobScheduler = (JobScheduler) context.getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}
