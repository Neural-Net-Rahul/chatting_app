package com.example.chattingapp

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.chattingapp.Fragments.ChatFragment
import com.example.chattingapp.Fragments.SearchFragment
import com.example.chattingapp.Fragments.SettingsFragment
import com.example.chattingapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "" // necessary, else xml toolbar will be "Main Activity [Image] [username]"

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatFragment(),"Chats")
        viewPagerAdapter.addFragment(SearchFragment() ,"Search")
        viewPagerAdapter.addFragment(SettingsFragment() ,"Settings")

        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    internal class ViewPagerAdapter(fragmentManager:FragmentManager):FragmentPagerAdapter(fragmentManager){

        private val fragments:ArrayList<Fragment> = ArrayList()
        private val titles:ArrayList<String> = ArrayList()

        override fun getCount() : Int {
            return fragments.size
        }

        override fun getItem(position : Int) : Fragment {
            return fragments[position]
        }

        fun addFragment(fragment:Fragment,title:String){
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position : Int) : CharSequence? {
            return titles[position]
        }
    }
}