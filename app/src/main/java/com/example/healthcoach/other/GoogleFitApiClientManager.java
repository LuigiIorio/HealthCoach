package com.example.healthcoach.other;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GoogleFitApiClientManager {

    private static final String FIELD_CLIENT_ID = "client_id";

    public static GoogleSignInClient buildGoogleSignInClient(Context context) {
        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open("client_secret.json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            String clientId = jsonObject.getJSONObject("installed").getString(FIELD_CLIENT_ID);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(clientId)
                    .requestEmail()
                    .requestScopes(new Scope("https://www.googleapis.com/auth/fitness.activity.read"))
                    .requestScopes(new Scope("https://www.googleapis.com/auth/fitness.activity.write"))
                    .requestScopes(new Scope("https://www.googleapis.com/auth/fitness.nutrition.read"))
                    .requestScopes(new Scope("https://www.googleapis.com/auth/fitness.nutrition.write"))
                    .build();

            return GoogleSignIn.getClient(context, gso);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
