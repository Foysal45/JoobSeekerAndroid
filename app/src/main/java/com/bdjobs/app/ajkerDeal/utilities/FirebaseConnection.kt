package com.bdjobs.app.ajkerDeal.utilities

import android.content.Context
import com.bdjobs.app.ajkerDeal.api.models.firebase_models.FirebaseCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseConnection {

    fun initFirebaseDatabase(context: Context): FirebaseApp {
        return try {

            val credential = FirebaseCredential(
                "ajkerdealapp",
                "com.ajkerdeal.app.android",
                "AIzaSyBeGUQMtyuab6ty1JgaMaPvbYX7G2jB_TY",
                "https://ajkerdealapp.firebaseio.com/",
                "gs://ajkerdealapp.appspot.com/",
                "key="
            )

            // public static final String FirebaseApikey = "AIzaSyBeGUQMtyuab6ty1JgaMaPvbYX7G2jB_TY";
            //  // Field from default config.
            //  public static final String FirebaseApplicationId = "com.ajkerdeal.app.android";
            //  // Field from default config.
            //  public static final String FirebaseDatabaseUrl = "https://ajkerdealapp.firebaseio.com/";
            //  // Field from default config.
            //  public static final String FirebaseProjectId = "ajkerdealapp";
            //  // Field from default config.
            //  public static final String FirebaseStorageUrl = "gs://ajkerdealapp.appspot.com/";
            //  // Field from default config.
            //  public static final String FirebaseWebApiKey = "key=";


            val options = FirebaseOptions.Builder()
                .setProjectId(credential.projectId)
                .setApplicationId(credential.applicationId)
                .setApiKey(credential.apikey)
                .setDatabaseUrl(credential.databaseUrl)
                .build()

            FirebaseApp.initializeApp(context, options, "Ajkerdeal Customer")
            FirebaseApp.getInstance("Ajkerdeal Customer")
        } catch (e: Exception) {
            FirebaseApp.getInstance("Ajkerdeal Customer")
        }
    }
}