package com.chrysoula.organizer.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.chrysoula.organizer.R;
import com.chrysoula.organizer.viewmodel.organizerViewModel;

public class RegisterActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Connections to UI layout
        EditText usernameEditText = findViewById(R.id.registerUsernameEditText);
        EditText passwordEditText= findViewById(R.id.registerPasswordEditText);
        EditText confirmpasswordEditText =findViewById(R.id.confirmPasswordEditText);
        Button registerButton=findViewById(R.id.registerButton);
        Button backButton=findViewById(R.id.backButton);
        ImageView passwordToggle=findViewById(R.id.passwordToggle);
        ImageView password_confirmToggle=findViewById(R.id.password_con_Toggle);
        ProgressBar registerProgressBar = findViewById(R.id.registerProgressBar);


        registerButton.setEnabled(false);  //disabled initially

        organizerViewModel viewModel = new ViewModelProvider(this).get(organizerViewModel.class);

        //Register Button enable-logic
        TextWatcher inputWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username=usernameEditText.getText().toString().trim();
                String password=passwordEditText.getText().toString().trim();
                String confirmPassword=confirmpasswordEditText.getText().toString().trim();

                boolean enable=!username.isEmpty() && !password.isEmpty() && password.equals(confirmPassword);
                registerButton.setEnabled(enable);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        usernameEditText.addTextChangedListener(inputWatcher);
        passwordEditText.addTextChangedListener(inputWatcher);
        confirmpasswordEditText.addTextChangedListener(inputWatcher);

        //Register button clicked-logic
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmpasswordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Complete all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            registerProgressBar.setVisibility(View.VISIBLE);

            viewModel.registerUser(username, password, (success, message) -> {
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Successful Registration!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else {
                        Toast.makeText(this, "Failure: " + message, Toast.LENGTH_SHORT).show();
                    }
                    registerProgressBar.setVisibility(View.GONE);
                });
            });
        });


        backButton.setOnClickListener(v->{
            Intent intent=new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        passwordToggle.setOnClickListener(v -> {
            if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye_closed);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye_open);
            }
            passwordEditText.setSelection(passwordEditText.length());
        });

        password_confirmToggle.setOnClickListener(v -> {
            if (confirmpasswordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                confirmpasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password_confirmToggle.setImageResource(R.drawable.ic_eye_closed);
            } else {
                confirmpasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password_confirmToggle.setImageResource(R.drawable.ic_eye_open);
            }
            confirmpasswordEditText.setSelection(confirmpasswordEditText.length());
        });



    }

}
