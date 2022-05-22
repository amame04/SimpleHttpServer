package io.github.amame04.android.simplehttpserver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.os.HandlerCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import java.util.concurrent.Executors

class SettingsActivity : AppCompatActivity() {
    companion object{
        var startFlag = false
    }

    @UiThread
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val startButton : Button = findViewById(R.id.startButton)
        val stopButton : Button = findViewById(R.id.stopButton)

        val handler = HandlerCompat.createAsync(mainLooper)
        val simpleHttpServerExecutor = SimpleHttpServerExecutor(handler)
        val executeService = Executors.newSingleThreadExecutor()

        startButton.setOnClickListener {
            startFlag = true
            executeService.submit(simpleHttpServerExecutor)
            it.visibility = View.GONE
            stopButton.visibility = View.VISIBLE
        }

        stopButton.setOnClickListener {
            startFlag = false
            it.visibility = View.GONE
            startButton.visibility = View.VISIBLE
        }

        startButton.callOnClick()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val ipAddress = this.context?.let { IPAddress().getIPAddress(it) }
            val urlView = findPreference<Preference>("url")
            urlView?.title = "URL: http://$ipAddress:8080"

            val contentResolver = context?.contentResolver
            val takeFlag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val setDirView = findPreference<Preference>("setDirectory")

            val sharedPref = activity?.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
            val savedPath = sharedPref?.getString(getString(R.string.savedDir), null)

            savedPath?.let { setDirView?.summary = it.toUri().path}

            val getContent = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()){
                it?.let{
                    contentResolver?.takePersistableUriPermission(it, takeFlag)
                    setDirView?.summary = it.path
                    with(sharedPref?.edit()) {
                        this?.putString(getString(R.string.savedDir), it.toString())
                        this?.apply()
                    }
                }
            }

            setDirView?.setOnPreferenceClickListener {
                getContent.launch("/".toUri())
                true
            }
        }
    }

    private inner class SimpleHttpServerExecutor(handler: Handler) : Runnable {
        private val _handler = handler

        @WorkerThread
        override fun run() {
            try{
                val server = SimpleHttpServer()
                server.start(applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val postExecutor = SimpleHttpServerPostExecutor()
            _handler.post(postExecutor)
        }
    }

    private inner class SimpleHttpServerPostExecutor() : Runnable{
        @UiThread
        override fun run() {
        }
    }


}
