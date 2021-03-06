package com.example.pc_gaming.besustainable.Class;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.pc_gaming.besustainable.Entity.Consumer;
import com.example.pc_gaming.besustainable.Interface.CustomRequest;
import com.example.pc_gaming.besustainable.R;
import com.example.pc_gaming.besustainable.services.ServiceLocation;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    EditText etPassword, etEmail;
    Button btnLogin, btnCancel;
    String email = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancel = findViewById(R.id.btnCancel);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(!etEmail.getText().toString().equals("") || !etPassword.getText().toString().equals("")){

                    email = etEmail.getText().toString();
                    password = etPassword.getText().toString();

                    loadDataUser();




                }else
                    Toasty.warning(getApplicationContext(), "Fill all Fields! Please :)", Toast.LENGTH_SHORT, true).show();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Come Back to Home
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

            }
        });


    }

    public void loadDataUser(){

        // Request for the Login Consumer
        String url = getString(R.string.ip) + "/beSustainable/login.php";

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONArray jsonArray = response.getJSONArray("consumer");

                    // Control Errors
                    if(jsonArray.length() == 1){

                        if(jsonArray.getJSONObject(0).getString("ErrorMessage").equals("PasswordErrorMessage"))
                            Toasty.error(getApplicationContext(), "Password Error!", Toast.LENGTH_SHORT, true).show();
                        else
                            Toasty.error(getApplicationContext(), "Email Error!", Toast.LENGTH_SHORT, true).show();

                    // Else Extract Info of the Consumer
                    }else{

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Consumer consumer = new Consumer();
                        consumer.setIdConsumer(jsonObject.getInt("idconsumer"));
                        consumer.setIdCity(jsonObject.getInt("idcity"));
                        consumer.setCityName(jsonObject.getString("cityname"));
                        consumer.setNick(jsonObject.getString("nick"));
                        consumer.setDescription(jsonObject.getString("description"));
                        consumer.setEmail(jsonObject.getString("email"));
                        consumer.setBirthday(Date.valueOf(jsonObject.getString("birthday")));
                        consumer.setGender(jsonObject.getString("gender").toLowerCase());
                        consumer.setNewsletter(jsonObject.getBoolean("newsletter"));
                        // No it's possible save an image in a SharedPreferences file.
                        //consumer.setImg(jsonObject.getString("img"));

                        // Save the consumer in the SharedPreferences
                        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        Gson gson = new Gson();

                        //Cast to Json
                        String json = gson.toJson(consumer);
                        prefsEditor.putString("Consumer", json);
                        prefsEditor.commit();

                        // Start MainActivity
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(getApplicationContext(), "Exception " + e.getMessage(), Toast.LENGTH_SHORT, true).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toasty.error(getApplicationContext(), "Login Error ", Toast.LENGTH_SHORT, true).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(customRequest);


    }

    @Override public void onBackPressed() {

        // No Action when press Back
        //return null;

    }
}
