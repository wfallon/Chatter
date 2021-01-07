package edu.upenn.cis350.chatter;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.upenn.cis350.chatter.R;

import java.net.*;
import android.content.Intent;


import static androidx.core.content.ContextCompat.startActivity;

public class LoginActivity extends AppCompatActivity {

    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loginAttempt(View v) {
        this.v = v;
        EditText usernameText = findViewById(R.id.username);
        EditText pwText = findViewById(R.id.password);
        String username = usernameText.getText().toString();
        String pw = pwText.getText().toString();

        AuthenticateTask logger = new AuthenticateTask(username, pw, this);

        logger.execute();

        usernameText.setText("");
        pwText.setText("");
    }

    public void launcher() {
        Intent display = new Intent(this, MainActivity.class);
        startActivity(display);
    }

    public void register(View v) {
        Intent registration = new Intent(this, Signup.class);
        startActivity(registration);
    }
}
