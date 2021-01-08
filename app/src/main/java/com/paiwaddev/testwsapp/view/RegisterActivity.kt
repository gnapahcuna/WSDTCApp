package com.paiwaddev.testwsapp.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.paiwaddev.ahorros.ui.main.goal.UsersViewModel
import com.paiwaddev.ahorros.ui.main.goal.UsersViewModelFactory
import com.paiwaddev.testwsapp.R
import com.paiwaddev.testwsapp.data.model.Users
import com.paiwaddev.testwsapp.utils.MyApplication


class RegisterActivity : AppCompatActivity(), View.OnClickListener, AlertMessage.ItemListener {
    private val userViewModel: UsersViewModel by viewModels {
        UsersViewModelFactory(this, (application as MyApplication).repository)
    }
    private lateinit var inputUser: TextInputEditText
    private lateinit var inputPass: TextInputEditText
    private lateinit var inputFirstname: TextInputEditText
    private lateinit var inputLastname: TextInputEditText
    private lateinit var inputCardID: TextInputEditText
    private lateinit var inputPhonNumber: TextInputEditText
    private lateinit var buttonUpload: MaterialButton
    private lateinit var buttonSave: MaterialButton
    private lateinit var buttonCancel: MaterialButton
    private lateinit var cardImage: CardView
    private var REQ_CODE = 1010

    private var mImageByte: ByteArray? = null
    private lateinit var alertMessage: AlertMessage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        this.title = "Register"

        bindingView()

        buttonUpload.setOnClickListener(this)

        buttonSave.setOnClickListener(this)

        buttonCancel.setOnClickListener(this)
    }

    fun bindingView() {
        inputUser = findViewById(R.id.edt_user_regis)
        inputPass = findViewById(R.id.edt_pass_regis)
        inputFirstname = findViewById(R.id.edt_firstname_regis)
        inputLastname = findViewById(R.id.edt_lastname_regis)
        inputCardID = findViewById(R.id.edt_cardID_regis)
        inputPhonNumber = findViewById(R.id.edt_phone_regis)
        buttonUpload = findViewById(R.id.button_upload)
        buttonSave = findViewById(R.id.button_regis_save)
        buttonCancel = findViewById(R.id.button_regis_cancel)
        cardImage = findViewById(R.id.card_image)

        cardImage.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.button_upload -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, REQ_CODE)
            }

            R.id.button_regis_save -> {

                if (TextUtils.isEmpty(inputFirstname.text) || TextUtils.isEmpty(inputLastname.text)
                    || TextUtils.isEmpty(inputCardID.text) || TextUtils.isEmpty(inputUser.text)
                    || TextUtils.isEmpty(inputPass.text)
                ) {
                    userViewModel.requireInput(inputFirstname)
                    userViewModel.requireInput(inputLastname)
                    userViewModel.requireInput(inputUser)
                    userViewModel.requireInput(inputPass)
                    userViewModel.requireInput(inputCardID)

                } else {

                    userViewModel.checkInvalidData(inputCardID.text.toString(),inputPhonNumber.text.toString()).observe(this,{
                        println(it)
                        if(it){
                            alertMessage = AlertMessage(
                                this,
                                resources.getString(R.string.message_confirm),
                                "OK",
                                "Cancel"
                            )
                            alertMessage.setListener(this)
                        }else{
                           Toast.makeText(this,"รุปแบบข้อมูลไม่ถูกต้อง โปรดเช็ค card id หรือ phone number",Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

        }
    }


    companion object {
        const val EXTRA_REPLY_USER = "com.paiwaddev.testwsapp.REPLY.Username"
        const val EXTRA_REPLY_PASS = "com.paiwaddev.testwsapp.REPLY.Password"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK)
            return

        println(requestCode)
        if (requestCode == REQ_CODE) {
            cardImage.visibility = View.VISIBLE
            val uri: Uri = data?.data!!
            findViewById<ImageView>(R.id.iamgeProfile).setImageURI(uri)
            val inputStream = this@RegisterActivity.contentResolver.openInputStream(uri)
            mImageByte = inputStream?.readBytes()
            println(mImageByte)
        }
    }

    override fun onItemClicked(IsAction: Int) {
        if (IsAction == AlertMessage.POSITIVE) {
            onSave()
        }
    }


    fun onSave() {

        val user = Users(
            username = inputUser.text.toString(),
            password = inputPass.text.toString(),
            firstname = inputFirstname.text.toString(),
            lastname = inputLastname.text.toString(),
            cardId = inputCardID.text.toString(),
            phone = inputPhonNumber.text.toString(),
            image = mImageByte
        )
        userViewModel.onRegister(user)
        Toast.makeText(applicationContext, "ลงทะเบียนสำเร็จ", Toast.LENGTH_SHORT).show()

        val replyIntent = Intent()
        replyIntent.putExtra(EXTRA_REPLY_USER, inputUser.text)
        replyIntent.putExtra(EXTRA_REPLY_PASS, inputPass.text)
        setResult(Activity.RESULT_OK, replyIntent)
        finish()

    }
}