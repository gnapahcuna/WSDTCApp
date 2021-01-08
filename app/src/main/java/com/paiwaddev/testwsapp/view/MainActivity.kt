package com.paiwaddev.testwsapp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.paiwaddev.ahorros.ui.main.goal.UsersViewModel
import com.paiwaddev.ahorros.ui.main.goal.UsersViewModelFactory
import com.paiwaddev.testwsapp.R
import com.paiwaddev.testwsapp.utils.MyApplication


class MainActivity : AppCompatActivity(), View.OnClickListener, AlertMessage.ItemListener {

    private val userViewModel: UsersViewModel by viewModels {
        UsersViewModelFactory(this, (application as MyApplication).repository)
    }

    private lateinit var inputUser: TextInputEditText
    private lateinit var inputPass: TextInputEditText
    private lateinit var isRemember: CheckBox
    private lateinit var buttonLogin: MaterialButton
    private lateinit var buttonRegister: MaterialButton
    private lateinit var alertMessage: AlertMessage
    private lateinit var checkRemember: CheckBox

    private lateinit var sharepref: SharedPreferences
    private var REQ_CODE = 1011

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindingView()

        buttonLogin.setOnClickListener(this)
        buttonRegister.setOnClickListener(this)
    }

    fun bindingView() {
        inputUser = findViewById(R.id.edt_user_login)
        inputPass = findViewById(R.id.edt_pass_login)
        isRemember = findViewById(R.id.check_remember)
        buttonLogin = findViewById(R.id.button_login)
        buttonRegister = findViewById(R.id.button_regis)
        checkRemember = findViewById(R.id.check_remember)

        sharepref = getSharedPreferences("USER_REMEM", Context.MODE_PRIVATE)
        sharepref.let {
            val user = sharepref.getString("Username", "")
            val pass: String? = sharepref.getString("Password", "")
            inputUser.setText(user)
            inputPass.setText(pass)

            if (user!!.isEmpty()) {
                checkRemember.isChecked = false
            } else {
                checkRemember.isChecked = true
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){

            R.id.button_login -> {
                val username = inputUser.text.toString()
                val password = inputPass.text.toString()

                if (!TextUtils.isEmpty(inputUser.text) && !TextUtils.isEmpty(inputPass.text)) {
                    userViewModel.onUserLogin(username, password).observe(this, { user ->
                        if (user == null) {
                            showDialog()
                        } else {

                            if(checkRemember.isChecked){
                                saveRemember()
                            }else{
                                sharepref.let {
                                    sharepref.edit().clear().commit()
                                }
                            }

                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.putExtra("userID", user.UserID.toInt())
                            startActivity(intent)
                            finish()
                        }
                    })
                } else {
                    userViewModel.requireInput(inputUser)
                    userViewModel.requireInput(inputPass)
                }
            }

            R.id.button_regis -> {
                toNextPageRegister(RegisterActivity())
            }

            else ->{}
        }

    }

    fun saveRemember(){
        val editor: Editor = sharepref.edit()
        editor.putString("Username", inputUser.text.toString())
        editor.putString("Password", inputPass.text.toString())
        editor.commit()
    }

    fun toNextPageRegister(activity: Context){
        val intent = Intent(this, activity::class.java)
        startActivityForResult(intent, REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK)
            return

        if(requestCode == REQ_CODE){
            val username: String? = data?.getStringExtra(RegisterActivity.EXTRA_REPLY_USER);
            val password: String? = data?.getStringExtra(RegisterActivity.EXTRA_REPLY_PASS);

            inputUser.setText(username)
            inputPass.setText(password)
        }
    }


    fun toCallAdmin(){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:1176")
        try {
            startActivity(intent)
        }catch (ex: SecurityException){}
    }

    fun showDialog(){
        alertMessage = AlertMessage(
            this,
            resources.getString(R.string.message_fail),
            "ลงทะเบียน",
            "ติดต่อเจ้าหน้าที่"
        )
        alertMessage.setListener(this)
    }

    override fun onItemClicked(IsActions: Int) {
        if(IsActions == AlertMessage.POSITIVE) {
            toNextPageRegister(RegisterActivity())
        }else if(IsActions == AlertMessage.NAGATIVE){
            toCallAdmin()
        }
    }
}