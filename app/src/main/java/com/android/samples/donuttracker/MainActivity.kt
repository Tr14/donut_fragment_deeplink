/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.samples.donuttracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.android.samples.donuttracker.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.pushio.manager.PIOMCMessage
import com.pushio.manager.PIOMCMessageError
import com.pushio.manager.PIOMCMessageListener
import com.pushio.manager.PushIOManager
import com.pushio.manager.exception.PIOMCMessageException

/**
 * Main activity class. Not much happens here, just some basic UI setup.
 * The main logic occurs in the fragments which populate this activity.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MESSAGE FOR TESTING","Check if activity is opened") //this is for checking if app is reopen when click deeplink noti

        PushIOManager.setLogLevel(Log.VERBOSE)

        PushIOManager.getInstance(applicationContext).registerApp()

        PushIOManager.getInstance(this).isMessageCenterEnabled
        PushIOManager.getInstance(this).isMessageCenterEnabled = true


        val pushIOManager = PushIOManager.getInstance(applicationContext)
        pushIOManager.addMessageCenterUpdateListener { messageCenters -> // messageCenters - list of message centers recently updated
            for (messageCenter in messageCenters) {
                try {
                    pushIOManager.fetchMessagesForMessageCenter(
                        messageCenter,
                        object : PIOMCMessageListener {
                            override fun onSuccess(
                                messageCenter: String,
                                messages: List<PIOMCMessage>
                            ) {
                                Log.d("Message Center", messageCenter)
                            }

                            override fun onFailure(
                                messageCenter: String,
                                messageError: PIOMCMessageError
                            ) {
                                Log.d("FAILURE", "FAILURE")
                            }
                        })
                } catch (e: PIOMCMessageException) {
                    e.printStackTrace()
                }
            }
        }

        PushIOManager.getInstance(getApplicationContext()).registerUserId("12345qwerty");

        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        Notifier.init(this)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        "ERROR MESSAGE FCM",
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d("TOKEN", msg)
                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
