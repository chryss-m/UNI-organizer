package com.chrysoula.organizer.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.chrysoula.organizer.R;
import com.chrysoula.organizer.viewmodel.organizerViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private organizerViewModel viewModel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);

        //initialize view model
        viewModel = new ViewModelProvider(this).get(organizerViewModel.class);

        //initialize ui components
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        ImageView togglePassword = findViewById(R.id.togglePasswordVisibility);
        progressBar = findViewById(R.id.progressBar);
        TextView signUpTextView = findViewById(R.id.signUpTextView);


        //check if user is already logged in
        String loggedInUser = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("logged_in_user", null);


        //if a user is logged in, fetch their id and go to main activity
        if (loggedInUser != null) {
            viewModel.getUserIdByUsername(loggedInUser).observe(this, userId -> {
                if (userId != null && userId != -1) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(this, "Failure, try login again!.", Toast.LENGTH_SHORT).show();
                }
            });
            return;  //skip login if already logged in
        }


        //initially disabled
        loginButton.setEnabled(false);


        //Move to registration screen when "Sign Up" is clicked
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        //Enable login button only when both username and password are filled
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                loginButton.setEnabled(!username.isEmpty() && !password.isEmpty());
            }
        };
        usernameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        //handle login button click
        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            loginButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            //call view model to attempt login
            viewModel.loginUser(username, password, (success, message, userId) -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    if (success) {

                        //If "Keep me logged in" is checked, store username
                        CheckBox keepLoggedInCheckBox = findViewById(R.id.keepLoggedInCheckBox);
                        if (keepLoggedInCheckBox.isChecked()) {
                            saveLoginState(username);
                        }

                        //Start MainActivity and pass user ID
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    } else {
                        //show error message if login fails
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        //show/hide password
        togglePassword.setOnClickListener(v -> {
            if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_closed);
            }
            //keep cursor at the end of the text
            passwordEditText.setSelection(passwordEditText.getText().length());
        });
    }


     // Saves the logged-in user's username in SharedPreferences for future auto-login

    private void saveLoginState(String username) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().putString("logged_in_user", username).apply();
    }
}

