package org.konata.udpsender;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private TextInputLayout usernameInput;
    private TextInputLayout passwordInput;
    private EditText username;
    private EditText password;
    private String loginUsername;
    private String loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginBtn = findViewById(R.id.loginBtn);
        usernameInput = findViewById(R.id.usernameinput);
        passwordInput = findViewById(R.id.passinput);
        username = findViewById(R.id.username);
        password = findViewById(R.id.pass);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUsername = username.getText().toString();
                loginPassword = password.getText().toString();
                boolean isUsernameValid = usernameInput.getError() == null;
                boolean isPasswordValid = passwordInput.getError() == null;

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                final String url = "https://mccs.bronya.moe/api/v1/login";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        loginBtn.setEnabled(true);
                    }
                });

                loginBtn.setEnabled(false);
                queue.add(jsonObjectRequest);
                Toast.makeText(LoginActivity.this, "addedQueue", Toast.LENGTH_LONG).show();


//                if (isUsernameValid && isPasswordValid && !loginUsername.isEmpty() && !loginPassword.isEmpty()) {
//                    if (testUser.getUsername().equals(loginUsername) && testUser.getPassword().equals(loginPassword)) {
//                        SharedPreferences sharedPreferences = getSharedPreferences("logged",MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("loggedUsername",testUser.getUsername());
//                        editor.commit();
//                        Intent intent = new Intent(MainActivity.this,UserHomeActivity.class);
//                        startActivity(intent);
//
//                    } else {
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                        builder.setTitle("Login Failed");
//                        builder.setMessage("Please check your input");
//                        final AlertDialog dialog = builder.create();
//                        dialog.show();
//                    }
//                } else {
//                    Snackbar.make(v, "Please check your input", Snackbar.LENGTH_LONG).show();
//                }

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
                Pattern hexPattern = Pattern.compile("^\\S{3,}$");
                if (!hexPattern.matcher(s.toString()).matches()) {
                    usernameInput.setError("username must more than 3 characters");
                } else {
                    usernameInput.setError(null);
                }
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
                Pattern hexPattern = Pattern.compile("^\\S{6,}$");
                if (!hexPattern.matcher(s.toString()).matches()) {
                    passwordInput.setError("password must more than 6 characters");
                } else {
                    passwordInput.setError(null);
                }
            }
        });
    }
}