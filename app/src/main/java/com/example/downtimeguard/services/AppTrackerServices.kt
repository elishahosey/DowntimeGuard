package com.example.downtimeguard.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.downtimeguard.data.repository.AppUsageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppTrackerServices : AccessibilityService() {
    private val TAG = "AppTrackerService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var repository:AppUsageRepository
    private var CurrentPackage:String?=null
    private var CurrentStartTime: Long=0
    companion object{
        private val _isRunning = MutableLiveData<Boolean>()
        val isRunning: LiveData<Boolean> = _isRunning
    }

    override fun onCreate(){
        super.onCreate()
        repository = AppUsageRepository(applicationContext)
        _isRunning.postValue(true)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100

        this.serviceInfo = info
        Log.d(TAG, "Accessibility Service conneccted")

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let{
            if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                val packageName = event.packageName?.toString()

                if(packageName != null && packageName != CurrentPackage){
                    val timestamp = System.currentTimeMillis()

                    // Log the previous app session if there was one
                    if(CurrentPackage !=null && CurrentStartTime > 0){
                        logAppUsage(CurrentPackage!!,CurrentStartTime,timestamp)
                    }

                    //Start Tracking the new app
                    CurrentPackage = packageName
                    CurrentStartTime = timestamp

                    //Log app open immediately, even for a sec
                    logAppOpen(packageName,timestamp)
                }
            }
        }
    }

    private fun logAppUsage(packageName:String,startTime:Long,endTime:Long){
        serviceScope.launch{
            repository.logAppUsageEvent(packageName,startTime,endTime)
        }
    }

    private fun logAppOpen(packageName:String,timestamp:Long){
        serviceScope.launch {
            repository.logAppUsageEvent(packageName,timestamp,timestamp)
        }
    }

    override fun onInterrupt(){
        Log.e(TAG,"Accessibility Service Interrupted")
    }

    override fun onDestroy(){
        super.onDestroy()
        _isRunning.postValue(false)
    }
}