package io.github.amame04.android.simplehttpserver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import java.io.File

class LogFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = context?.filesDir?.path + "/log/log"
        val logView = view.findViewById<TextView>(R.id.logView)
        val scrView = view.findViewById<ScrollView>(R.id.scrollView)
        try {
            logView.text = File(path).bufferedReader().use { it.readText() }
        } catch (e : Exception){
            e.printStackTrace()
        }
        scrView.post { run { scrView.fullScroll(View.FOCUS_DOWN) } }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false)
    }
}