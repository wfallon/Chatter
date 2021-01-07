package edu.upenn.cis350.chatter;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        System.out.println("Go");
    }

    public void registerAttempt(View v) {
        EditText usernameText = findViewById(R.id.username);
        EditText passwordText = findViewById(R.id.pw);
        EditText interest1 = findViewById((R.id.interest_1));
        EditText interest2 = findViewById(R.id.interest_2);
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String interestOne = interest1.getText().toString();
        String interestTwo = interest2.getText().toString();
        new RegisterTask(this).execute(username, password, interestOne, interestTwo);
    }

    public void launcher() {
        Intent display = new Intent(this, MainActivity.class);
        startActivity(display);
    }

}
