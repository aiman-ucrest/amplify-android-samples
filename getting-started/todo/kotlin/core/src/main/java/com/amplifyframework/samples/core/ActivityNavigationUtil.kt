package com.amplifyframework.samples.core

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

/**
 * Util class for the activity navigation
 */
object ActivityNavigationUtil {
    private const val TAG = "ActivityNavigationUtil"
    /**
     * Represent different activity finish mode
     */
    enum class ActivityFinishMode(val mode: String) {
        KEEP_ACTIVITY("KEEP_ACTIVITY"),
        FINISH_CURRENT("FINISH_CURRENT"),
        FINISH_ALL("FINISH_ALL")
    }
    /**
     * Navigate to new activity and also kill the current activity based on the given 'isFinish' value
     */
    @JvmStatic
    fun navigateToActivity(activity: Activity, nextActivity: Class<*>, activityFinishMode: ActivityFinishMode){
        navigateToNextActivity(activity, nextActivity, null, activityFinishMode, null, true)
    }

    /**
     * Navigate to new activity and keep given bundle in the intent
     */
    @JvmStatic
    fun navigateToActivity(activity: Activity, nextActivity: Class<*>, bundle: Bundle?, activityFinishMode: ActivityFinishMode){
        navigateToNextActivity(activity, nextActivity, bundle, activityFinishMode, null, true)
    }

    /**
     * Start an activity to do some work and expect a result from an activity
     */
    @JvmStatic
    fun openActivityForResult(activity: Activity, nextActivity: Class<*>, activityRequestCode: Int){
        navigateToNextActivity(activity, nextActivity, null, ActivityFinishMode.KEEP_ACTIVITY, activityRequestCode, true)
    }

    /**
     * Start an activity to do some work and expect a result from an activity
     */
    @JvmStatic
    fun openActivityForResult(activity: Activity, nextActivity: Class<*>, bundle: Bundle, activityRequestCode: Int){
        navigateToNextActivity(activity, nextActivity, bundle, ActivityFinishMode.KEEP_ACTIVITY, activityRequestCode, true)
    }

    /**
     * Start an activity to do some work and expect a result from an activity
     */
    @JvmStatic
    fun openActivityForResult(activity: Activity, nextActivity: Class<*>, bundle: Bundle, activityRequestCode: Int, isAnimationNeeded: Boolean){
        navigateToNextActivity(activity, nextActivity, bundle, ActivityFinishMode.KEEP_ACTIVITY, activityRequestCode, isAnimationNeeded)
    }

    /**
     * Start an activity to do some work from a fragment and expect a result to the same fragment
     */
    @JvmStatic
    fun openActivityForResult(activity: Activity, fragment: Fragment, nextActivity: Class<*>, activityRequestCode: Int){
        startActivityForResult(activity, fragment, nextActivity, null, activityRequestCode)
    }

    /**
     * Start an activity to do some work from a fragment and expect a result to the same fragment
     */
    @JvmStatic
    fun openActivityForResult(activity: Activity, fragment: Fragment, nextActivity: Class<*>,  bundle: Bundle, activityRequestCode: Int){
        startActivityForResult(activity, fragment, nextActivity, bundle, activityRequestCode)
    }

    /**
     * General navigation code
     */
    @JvmStatic
    private fun navigateToNextActivity(activity: Activity, nextActivity: Class<*>, bundle: Bundle?, activityFinishMode: ActivityFinishMode, activityRequestCode: Int?, isAnimationNeeded: Boolean){
        val intent = Intent(activity, nextActivity)
        if(bundle != null){
            intent.putExtras(bundle)
        }
        if(activityRequestCode == null) {
            activity.startActivity(intent)
        }else{
            activity.startActivityForResult(intent, activityRequestCode)
        }
        if(isAnimationNeeded)
            //activity.overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        else
            activity.overridePendingTransition(0, 0)

        when(activityFinishMode){
            ActivityFinishMode.FINISH_CURRENT -> activity.finish()
            ActivityFinishMode.FINISH_ALL -> activity.finishAffinity()
            else -> Log.d(TAG, "No need to handle mode= $activityFinishMode")
        }
    }

    /**
     * General navigation code
     */
    @JvmStatic
    private fun startActivityForResult(activity: Activity, fragment: Fragment?, nextActivity: Class<*>, bundle: Bundle?,
                                                  activityRequestCode: Int){
        val intent = Intent(activity, nextActivity)
        if(bundle != null){
            intent.putExtras(bundle)
        }
        if(fragment != null) {
            fragment.startActivityForResult(intent, activityRequestCode)
        }else{
            activity.startActivityForResult(intent, activityRequestCode)
        }
        //activity.overridePendingTransition(R.anim.fadein,R.anim.fadeout)
    }

    @JvmStatic
    fun openActivityWithAction(activity: Activity, activityAction: String, bundle: Bundle?, activityFinishMode: ActivityFinishMode){
        val sendIntent = Intent().apply {
            action = activityAction
            type = "text/plain"
        }
        if(bundle != null){
            sendIntent.putExtras(bundle)
        }
        if (sendIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(sendIntent)
        }
        //activity.overridePendingTransition(R.anim.fadein,R.anim.fadeout)
        when(activityFinishMode){
            ActivityFinishMode.FINISH_CURRENT -> activity.finish()
            ActivityFinishMode.FINISH_ALL -> activity.finishAffinity()
            else -> Log.w(TAG, "Unexpected mode= $activityFinishMode")
        }
    }
}