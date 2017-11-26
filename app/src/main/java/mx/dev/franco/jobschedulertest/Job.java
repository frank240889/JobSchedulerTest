package mx.dev.franco.jobschedulertest;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Created by franco on 6/07/17.
 * This class encapsulates what you want to execute
 * and the params it needs to execute.
 *
 * NOTE: Remember that this is class extends from Service and all code runs on UI Thread.
 * so if you run intensive task make sure to run in their own Thread for avoiding
 * block UI Thread and making unresponsive
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
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);

        //set this params to retrieve when our job finish and
        //decide how many time has to wait the next execution
        PersistableBundle extraInfo = new PersistableBundle();
        extraInfo.putInt(MainActivity.Constants.DURATION,duration);
        builder.setExtras(extraInfo);

        //minimum latency means how many time has to wait to execute the code
        builder.setMinimumLatency(duration);
        //setOverrideDeadline means the limit in which your code is
        //going to execute, this to parameters together make something like
        //minimum and maximum limit to execute the code, its useful
        //because minimum setPeriodic time for Nougat are 15 minutes, so less than this time
        //is not going to work.
        //For android <= M there is no problem in use setPeriodic with less than 15 minutes
        builder.setOverrideDeadline(duration);

        //now that our builder object has the parameters set
        //is time to schedule.
        //NOTE: Remember to pass application context with getApplicationContext() instead of Activity context to avoid memory leaks!!!!!!!!!!
        JobScheduler jobScheduler = (JobScheduler) context.getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}
