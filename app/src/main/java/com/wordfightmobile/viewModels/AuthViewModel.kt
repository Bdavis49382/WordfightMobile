package com.wordfightmobile.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.wordfightmobile.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore
    var uid: String? by mutableStateOf(null)

    suspend fun logout(credentialManager: CredentialManager) {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        auth.signOut()
        uid = null
    }

    fun login(coroutineScope: CoroutineScope, credentialManager : CredentialManager, context: Context, after: () -> Unit = {}) {
        val googleIdOption = GetSignInWithGoogleOption.Builder(
            context.getString(R.string.default_web_client_id)
        )
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                handleSignIn(result, after)
            } catch (e: GetCredentialException) {
                Log.e("AUTH","Error logging in: ${e.message}")
            }
        }
    }

    fun checkLogin(): Boolean {
        try {
            if (auth.currentUser != null) {
                val user = auth.currentUser
                uid = user?.uid
                return true
            }
            return false
        } catch (err: FirebaseAuthInvalidUserException) {
            return false
        }
    }
    fun handleSignIn(result: GetCredentialResponse, after: () -> Unit) {
        val credential = result.credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken, after)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("AUTH", "Couldn't parse google token: ${e.message}")
                    }
                }
            }
        }
    }
    fun firebaseAuthWithGoogle(idToken: String,after: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.collection("users").document(auth.uid.toString()).get().addOnCompleteListener { result ->
                        if (result.isSuccessful && result.result.data == null) {
                            Log.d("user","no users found for that id")
                            db.collection("users").document(auth.uid.toString()).set(mapOf("name" to auth.currentUser?.displayName))
                        }
                    }
                    uid = task.result.user?.uid
                    after()
                }
            }
    }
}