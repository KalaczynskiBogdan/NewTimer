package com.example.newtimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.newtimer.home.TimerFragment
import com.example.newtimer.listeners.NavigationListener

class MainActivity : AppCompatActivity(), NavigationListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateToNextScreen()
    }
    override fun navigateToNextScreen() {
        replaceFragment(TimerFragment.newInstance(), false)
    }
    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .apply { if (addToBackStack) addToBackStack(fragment::class.simpleName) }
            .commit()
    }
}