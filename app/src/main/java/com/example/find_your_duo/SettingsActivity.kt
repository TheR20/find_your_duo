package com.example.find_your_duo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

class SettingsActivity : AppCompatActivity() {


    private var mNameField: EditText? = null
    private var mPhoneField: EditText? = null
    private var mBack: Button? = null
    private var mConfirm: Button? = null
    private var mProfileImage: ImageView? = null
    private var mAuth: FirebaseAuth? = null
    private var mUserDatabase: DatabaseReference? = null
    private var userId: String? = null
    private var name: String? = null
    private var phone: String? = null
    private var profileImageUrl: String? = null
    private var userSex: String? = null
    private var resultUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mNameField = findViewById<View>(R.id.name) as EditText
        mPhoneField = findViewById<View>(R.id.phone) as EditText
        mProfileImage = findViewById<View>(R.id.profileImage) as ImageView
        mBack = findViewById<View>(R.id.back) as Button
        mConfirm = findViewById<View>(R.id.confirm) as Button
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth?.currentUser!!.uid
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
        userInfo
        mProfileImage!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        mConfirm!!.setOnClickListener { saveUserInformation() }
        mBack!!.setOnClickListener(View.OnClickListener {
            finish()
            return@OnClickListener
        })
    }

    private val userInfo: Unit
        private get() {
            mUserDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                        val map = dataSnapshot.value as Map<String, Any?>?
                        if (map!!["Name"] != null) {
                            name = map["Name"].toString()
                            mNameField!!.setText(name)
                        }
                        if (map["phone"] != null) {
                            phone = map["phone"].toString()
                            mPhoneField!!.setText(phone)
                        }
                        if (map["sexo"] != null) {
                            userSex = map["sexo"].toString()
                        }
                        Glide.with(mProfileImage!!.context).clear(mProfileImage!!)
                        if (map["profileImageUrl"] != null) {
                            profileImageUrl = map["profileImageUrl"].toString()
                            when (profileImageUrl) {
                                "default" -> Glide.with(application).load(R.mipmap.ic_launcher).into(mProfileImage!!)
                                else -> Glide.with(application).load(profileImageUrl).into(mProfileImage!!)
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun saveUserInformation() {
        name = mNameField!!.text.toString()
        phone = mPhoneField!!.text.toString()
        val userInfo: HashMap<String, String?> = HashMap<String, String?>()
        userInfo["Name"] = name
        userInfo["phone"] = phone
        mUserDatabase?.updateChildren(userInfo as Map<String, Any>)
        val a = 1
        if (resultUri != null) {
            val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(userId!!)
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()
            val uploadTask = filepath.putBytes(data)
            uploadTask.addOnFailureListener { finish() }
            uploadTask.addOnSuccessListener(OnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.storage.downloadUrl
                while (!downloadUrl.isSuccessful());
                val resultdowloadUrl: Uri? = downloadUrl.getResult()
                val userInfo: MutableMap<String, Any?> = HashMap<String, Any?>()
                Log.e("uri12",resultdowloadUrl.toString()+"This is uri of image download");
                Toast.makeText(this, resultdowloadUrl.toString(), Toast.LENGTH_SHORT).show();
                userInfo.set("profileImageUrl", resultdowloadUrl.toString())
                mUserDatabase!!.updateChildren(userInfo as Map<String, Any>)
                finish()
                return@OnSuccessListener
            })
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            resultUri = imageUri
            mProfileImage!!.setImageURI(resultUri)
        }
    }
}

