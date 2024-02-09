package com.example.chattingapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.example.chattingapp.Class.User
import com.example.chattingapp.Fragments.ChatFragment
import com.example.chattingapp.Fragments.SearchFragment
import com.example.chattingapp.Fragments.SettingsFragment
import com.example.chattingapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

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

        showImageAndUserName()
    }

    private fun showImageAndUserName() {
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot : DataSnapshot) {
                    if(snapshot.exists()){
                        val user = snapshot.getValue(User::class.java)
                        binding.usernameAppBarLayout.text = user!!.getUsername()
                        Picasso.get()
                            .load(user.getProfile())
                            .into(binding.profileImage)
                    }
                }

                override fun onCancelled(error : DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    /*
    1. How i changed the text color and style of Chats, Search and Settings in tab layouts
    => see tabTextAppearance in xml and styles.xml

    2. How i changed the color of three dots
    => see themes.xml -> textColorSecondary

     */

    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        when(item.itemId){
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                return true
            }
        }
        return false
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