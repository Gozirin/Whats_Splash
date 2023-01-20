package com.example.whatsappapp.selectcontact

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.whatsappapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class Profile : Fragment() {
    // VARIABLES  DECLARED
    private lateinit var profileNameShow: TextView
    private lateinit var profileEmailShow: TextView
    private lateinit var profileStatusShow: TextView
    private lateinit var profilePicture: CircleImageView
    private lateinit var profilePictureAdd: ImageButton
    private lateinit var profileNameEdit: TextInputLayout
    private lateinit var profileEmailEdit: TextInputLayout
    private lateinit var profileStatusEdit: TextInputLayout
    private lateinit var editName: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editStatus: TextInputEditText
    private lateinit var profileSave: Button
    private lateinit var profileUpdate: Button
    private lateinit var progressBar: ProgressBar
    val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        uploadImage(it)
    }

    // AUTHENTICATION
    private lateinit var auth: FirebaseAuth

    // CLOUD FIRESTORE
    private lateinit var fstore: FirebaseFirestore

    // REALTIME DATABSE
    private lateinit var db: DocumentReference

    // AUTH.CURRENT USER
    private lateinit var userId: String
    private lateinit var image: ByteArray

    // CLOUD STORAGE FOR FIREBASE
    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //  FIND VIEWS BY IDS
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        userId = auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance().reference.child("$userId/profile")
        profileNameShow = view.findViewById(R.id.txtProfileName)
        profileNameEdit = view.findViewById(R.id.profileName)
        editName = view.findViewById(R.id.etProfileName)

        profileEmailShow = view.findViewById(R.id.txtProfileEmail)
        profileEmailEdit = view.findViewById(R.id.profileEmail)
        editEmail = view.findViewById(R.id.etProfileEmail)

        profileStatusShow = view.findViewById(R.id.txtProfileStatus)
        profileStatusEdit = view.findViewById(R.id.profileStatus)
        editStatus = view.findViewById(R.id.etProfileStatus)

        profilePicture = view.findViewById(R.id.profilePictureImg)
        profilePictureAdd = view.findViewById(R.id.profilePictureAdd)

        profileSave = view.findViewById(R.id.btSaveProfile)
        profileUpdate = view.findViewById(R.id.btUpdateProfile)
        progressBar = view.findViewById(R.id.profileProgressBar)

        // BUTTON SET ON CLICKS
        profileUpdate.visibility = view.visibility
        db = fstore.collection("Users").document(userId)
        db.addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("Error", "Unable To Fetch Data")
            } else {
                profileNameShow.text = value?.getString("userName")
                profileEmailShow.text = value?.getString("userEmail")
                profileStatusShow.text = value?.getString("userStatus")
                Picasso.get().load(value?.getString("userProfilePhoto")).error(R.drawable.profile).into(profilePicture)
            }
        }
        // UNCOMMENT LATER
//        profileEmailShow.text = "Email is chigozp2@gmail.com"
//        profileNameShow.text = "Name is Chigozie Anyaso"
//        profileStatusShow.text = "Code Runs Well"

        profileUpdate.setOnClickListener {
            profileNameShow.visibility = View.GONE
            profileEmailShow.visibility = View.GONE
            profileStatusShow.visibility = View.GONE
            profileNameEdit.visibility = View.VISIBLE
            profileEmailEdit.visibility = View.VISIBLE
            profileStatusEdit.visibility = View.VISIBLE
            profileSave.visibility = View.VISIBLE
            profileUpdate.visibility = View.GONE
            editName.text =
                Editable.Factory.getInstance().newEditable(profileNameShow.text.toString())
            editEmail.text =
                Editable.Factory.getInstance().newEditable(profileEmailShow.text.toString())
            editStatus.text =
                Editable.Factory.getInstance().newEditable(profileStatusShow.text.toString())
        }
        profileSave.setOnClickListener {
            profileNameShow.visibility = View.VISIBLE
            profileEmailShow.visibility = View.VISIBLE
            profileStatusShow.visibility = View.VISIBLE
            profileUpdate.visibility = View.VISIBLE
            profileNameEdit.visibility = View.GONE
            profileEmailEdit.visibility = View.GONE
            profileStatusEdit.visibility = View.GONE
            profileSave.visibility = View.GONE

            val obj = mutableMapOf<String, String>()
            obj["userName"] = editName.text.toString()
            obj["userEmail"] = editEmail.text.toString()
            obj["userStatus"] = editStatus.text.toString()
            db.set(obj).addOnCompleteListener {
                Log.d("Success", "Data Successfully Updated")
            }
        }
        profilePictureAdd.setOnClickListener {
            capturePhoto()
        }
        return view
    }

    private fun capturePhoto() {
//        val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
//            uploadImage(it)
//        }
//        register.launch(null)
    }

    private fun uploadImage(it: Bitmap?) {
        val baos = ByteArrayOutputStream()
        it?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        image = baos.toByteArray()
        storageReference.putBytes(image).addOnCompleteListener {
            storageReference.downloadUrl.addOnCompleteListener {
                val obj = mutableMapOf<String, String>()
                obj["userProfilePhoto"] = it.toString()
                db.update(obj as Map<String, Any>).addOnSuccessListener {
                    Log.d("onSuccess", "ProfilePictureUploaded")
                }
            }
        }
    }
}
