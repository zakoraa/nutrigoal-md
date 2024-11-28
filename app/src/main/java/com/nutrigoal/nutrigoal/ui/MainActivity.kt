package com.nutrigoal.nutrigoal.ui

import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nutrigoal.nutrigoal.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(com.nutrigoal.nutrigoal.R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        bottomNavScrollAnimation()
    }

    private fun bottomNavScrollAnimation(){
        with(binding){
            nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > oldScrollY) {
                    bottomNavCard.animate()
                        .translationY(bottomNavCard.height.toFloat()).setInterpolator(
                            DecelerateInterpolator()
                        ).start()
                } else if (scrollY < oldScrollY) {
                    bottomNavCard.animate().translationY(0f)
                        .setInterpolator(DecelerateInterpolator()).start()
                }
            })
        }
    }
}