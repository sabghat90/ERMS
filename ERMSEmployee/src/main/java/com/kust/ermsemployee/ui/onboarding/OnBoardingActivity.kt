package com.kust.ermsemployee.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kust.ermsemployee.R
import com.kust.ermsemployee.databinding.ActivityOnBoardingBinding
import com.kust.ermsemployee.ui.auth.AuthActivity
import com.kust.ermsemployee.ui.onboarding.screens.FirstOnBoardingFragment
import com.kust.ermsemployee.ui.onboarding.screens.SecondOnBoardingFragment
import com.kust.ermsemployee.ui.onboarding.screens.ThirdOnBoardingFragment

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, com.kust.ermslibrary.R.color.white)

        if (onBoardingFinished()) {
            val intent = Intent(this, AuthActivity::class.java)
            this.finish()
            startActivity(intent)
        } else {
            val fragmentList = arrayListOf(
                FirstOnBoardingFragment(),
                SecondOnBoardingFragment(),
                ThirdOnBoardingFragment()
            )

            val adapter = ViewPagerAdapter(fragmentList, this.supportFragmentManager, lifecycle)

            binding.viewPager.adapter = adapter
        }
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPreferences =
            this.getSharedPreferences(
                "onBoarding",
                android.content.Context.MODE_PRIVATE
            )
        return sharedPreferences.getBoolean("Finished", false)
    }
}