package org.konata.udpsender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.konata.udpsender.util.JwtUtils;

import java.util.Date;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private TextInputLayout usernameInput;
    private TextInputLayout passwordInput;
    private EditText username;
    private EditText password;
    private String loginUsername;
    private String loginPassword;
    private static final String serverURL = "http://172.31.16.50:9999/api/v1/";
    private static final String loginUrl = serverURL.concat("login");

    // TODO: SSL Support

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getSharedPreferences("app", MODE_PRIVATE);
        long tokenExpirationDate = sharedPref.getLong("tokenExpirationDate", 0);
        Date dateNow = new Date();
        if (tokenExpirationDate != 0 && dateNow.before(new Date(tokenExpirationDate))) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.login_activity);
        loginBtn = findViewById(R.id.loginBtn);
        usernameInput = findViewById(R.id.usernameinput);
        passwordInput = findViewById(R.id.passinput);
        username = findViewById(R.id.username);
        password = findViewById(R.id.pass);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                loginUsername = username.getText().toString();
                loginPassword = password.getText().toString();
                boolean isUsernameValid = usernameInput.getError() == null;
                boolean isPasswordValid = passwordInput.getError() == null;

                if (loginUsername.isEmpty() || loginPassword.isEmpty() || !isUsernameValid || !isPasswordValid) {
                    Snackbar.make(v, "Please check your input", Snackbar.LENGTH_LONG).show();
                }

                final JSONObject reqJson = new JSONObject();
                try {
                    reqJson.put("username", loginUsername);
                    reqJson.put("password", loginPassword);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginUrl, reqJson,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("loginJWT", response.toString());
                                    Integer retcode = response.getInt("retcode");
                                    if (retcode == 0) {
                                        String token = (String) ((JSONObject) response.get("data")).get("token");
                                        Map jwtBody = JwtUtils.verifyJwt(token);
                                        Date expDate = (Date) jwtBody.get("exp");
                                        String username = (String) jwtBody.get("username");
                                        Integer role = (Integer) jwtBody.get("role");
                                        Date now = new Date();
                                        if (now.before(expDate)) {
                                            // 保存用户名、用户组、token
                                            SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("username", username);
                                            editor.putInt("role", role);
                                            editor.putLong("tokenExpirationDate", expDate.getTime());
                                            editor.commit();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Snackbar.make(v, response.getString("Server Error"), Snackbar.LENGTH_LONG).show();
                                            loginBtn.setEnabled(true);
                                        }
                                    } else {
                                        Snackbar.make(v, response.getString("message"), Snackbar.LENGTH_LONG).show();
                                        loginBtn.setEnabled(true);
                                    }
                                } catch (JSONException e) {
                                    Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    loginBtn.setEnabled(true);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(v, error.toString(), Snackbar.LENGTH_LONG).show();
                        error.printStackTrace();
                        loginBtn.setEnabled(true);
                    }
                });
                loginBtn.setEnabled(false);
                queue.add(jsonObjectRequest);
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                Pattern hexPattern = Pattern.compile("^\\S{3,}$");
//                if (!hexPattern.matcher(s.toString()).matches()) {
//                    usernameInput.setError("username must more than 3 characters");
//                } else {
//                    usernameInput.setError(null);
//                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                Pattern hexPattern = Pattern.compile("^\\S{3,}$");
//                if (!hexPattern.matcher(s.toString()).matches()) {
//                    passwordInput.setError("password must more than 6 characters");
//                } else {
//                    passwordInput.setError(null);
//                }
            }
        });
    }
}