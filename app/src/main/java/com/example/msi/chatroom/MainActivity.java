package com.example.msi.chatroom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText txtPhoneNumber,txtCode;
    private Button btnVerify;

     private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    String verifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       FirebaseApp.initializeApp(this);


        userIsLoggedIn();

        txtPhoneNumber=findViewById(R.id.phoneNumber);
        txtCode=findViewById(R.id.code);
        btnVerify=findViewById(R.id.sendVerfication);



        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifier!=null)
                    verifyPhoneNumberWithCode(verifier,txtCode.getText().toString());
                else
                    startPhoneNumberVerifier();
            }
        });

        mCallBacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) { }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verifier=s;
                btnVerify.setText("Verify Code");

            }
        };

    }

    private void verifyPhoneNumberWithCode(String verificationId,String code){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhone(credential);

    }

    private void signInWithPhone(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    userIsLoggedIn();
                                }
                            }
                        }
                );
    }

    private void userIsLoggedIn() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            //startActivity(new Intent(MainActivity.this,MainPageActivity.class));
            finish();
            return;
        }
    }

    private void startPhoneNumberVerifier() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                txtPhoneNumber.getText().toString(),
                60, TimeUnit.SECONDS,
                this,
                mCallBacks
        );
    }
}

