package com.example.notea;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Loading extends Activity {
    private EditText emailSignup;
    private EditText passwordSignup;
    private EditText nameSignup;
    private EditText emailLogin;
    private EditText passwordLogin;

    AccountDatabase accountDatabase = new AccountDatabase(this);

    RelativeLayout loginPopup;
    RelativeLayout signupPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        RelativeLayout startup = findViewById(R.id.startup);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startup.setVisibility(View.GONE);
            }
        }, 1500);

        loginPopup = findViewById(R.id.login_form);
        signupPopup = findViewById(R.id.register_form);
        openPopup();
    }

    private void openPopup(){
        loginPopup.setVisibility(View.VISIBLE);
    }

    public void popSignup(View v){
        loginPopup.setVisibility(View.GONE);
        signupPopup.setVisibility(View.VISIBLE);
    }

    public void closeSignup(View v){
        loginPopup.setVisibility(View.VISIBLE);
        signupPopup.setVisibility(View.GONE);
    }
    boolean accExists;
    public void register(View v){
        registerAcc(v);

        if (!accExists){
            Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show();
            loginPopup.setVisibility(View.VISIBLE);
            signupPopup.setVisibility(View.GONE);
        }
    }
    private void registerAcc(View v) {
        boolean inputError = false;
        accExists=true;
        emailSignup = (EditText) findViewById(R.id.emailRegister);
        passwordSignup = (EditText) findViewById(R.id.passwordRegister);
        nameSignup = (EditText) findViewById(R.id.nameRegister);
        if (TextUtils.isEmpty(nameSignup.getText())){
            nameSignup.setError("Name is required.");
            inputError = true;
        }

        if (TextUtils.isEmpty(emailSignup.getText())){
            emailSignup.setError("Email is required.");
            inputError = true;
        }

        if (TextUtils.isEmpty(passwordSignup.getText())){
            passwordSignup.setError("Password is required.");
            inputError = true;
        }

        if (inputError) {
            Toast.makeText(v.getContext(), "Registration error!", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = emailSignup.getText().toString();

        SQLiteDatabase db = accountDatabase.getWritableDatabase();

        String selection = AccountDatabase.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query("users", null, selection, selectionArgs, null, null, null);

        accExists = (cursor != null) && (cursor.getCount() > 0);

        // Close the cursor and database connection
        if (cursor != null) {
            cursor.close();
        }

        if (accExists){
            emailSignup.setError("Email already exists");
            Toast.makeText(v.getContext(), "Email already taken!", Toast.LENGTH_SHORT).show();
            db.close();
            return;
        }

        ContentValues values = new ContentValues();

        values.put(AccountDatabase.COLUMN_EMAIL, emailSignup.getText().toString());
        values.put(AccountDatabase.COLUMN_PASSWORD, passwordSignup.getText().toString());
        values.put(AccountDatabase.COLUMN_NAME, nameSignup.getText().toString());

        db.insert("users", null, values);

        db.close();

        accountDatabase.createUserNotesTable(emailSignup.getText().toString());
    }

    public void login(View v) {
        boolean loginError = false;
        emailLogin = (EditText) findViewById(R.id.emailLogin);
        passwordLogin = (EditText) findViewById(R.id.passwordLogin);

        String email = emailLogin.getText().toString();
        String pw = passwordLogin.getText().toString();

        if (email.isEmpty()){
            emailLogin.setError("Email is required");
            loginError = true;
        }

        if (pw.isEmpty()){
            passwordLogin.setError("Password is required");
            loginError = true;
        }

        if (loginError) {
            Toast.makeText(v.getContext(), "Login error!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isValidAccount = validate(email, pw);

        if (isValidAccount){
            Intent intent = new Intent(this, MainActivity.class);
            accountDatabase.setEmail(email);
            accountDatabase.getInfo();
            startActivity(intent);
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validate(String email, String password){
        SQLiteDatabase db = accountDatabase.getReadableDatabase();

        String selection = AccountDatabase.COLUMN_EMAIL + " = ? AND " + AccountDatabase.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query("users", null, selection, selectionArgs, null, null, null);

        boolean isValidUser = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return isValidUser;
    }
}
