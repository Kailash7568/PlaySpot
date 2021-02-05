package com.charan.playspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private boolean mVerificationInProgress = false;
    private FirebaseAuth mAuth;
    private EditText motp,mphno;
    private Button msendotp,mverifyotp;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mphno=(EditText)findViewById(R.id.phonetext);
        motp=(EditText)findViewById(R.id.otptext);
        msendotp=(Button)findViewById(R.id.sendotp);

        mverifyotp=(Button)findViewById(R.id.verifyotp);
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mVerificationInProgress=false;
                Toast.makeText(MainActivity.this,"Verification In Progress",Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(MainActivity.this,"Invalid Phone Number",Toast.LENGTH_SHORT).show();
                }
                else if(e instanceof FirebaseTooManyRequestsException){}
            }
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                //super.onCodeSent(s, forceResendingToken);
                Toast.makeText(MainActivity.this,"Verification code has been send on your number",Toast.LENGTH_SHORT).show();
                mVerificationId=s;
                mResendToken=forceResendingToken;
                mphno.setVisibility(View.GONE);
                msendotp.setVisibility(View.GONE);
                motp.setVisibility(View.VISIBLE);
                mverifyotp.setVisibility(View.VISIBLE);

            }
        };
        msendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthOptions options= PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mphno.getText().toString())
                        .setTimeout(60L,TimeUnit.SECONDS)
                        .setActivity(MainActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
                //    PhoneAuthProvider.getInstance().verifyPhoneNumber(mphno.getText().toString(),60, TimeUnit.SECONDS,MainActivity.this,mCallbacks);
            }
        });
        mverifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,motp.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            startActivity(new Intent(MainActivity.this,HomeActivity.class));
                            Toast.makeText(MainActivity.this,"Verification Done",Toast.LENGTH_SHORT).show();
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("BookingManager").child(user_id);
                            current_user_db.setValue(true);
                            DatabaseReference current_user_ph = FirebaseDatabase.getInstance().getReference().child("BookingManager").child(user_id).child("Phone");
                            current_user_ph.setValue(mphno.getText().toString());
                            DatabaseReference current_user_name = FirebaseDatabase.getInstance().getReference().child("BookingManager").child(user_id).child("Name");
                            current_user_name.setValue("Booking Manager");
                            DatabaseReference total_slots = FirebaseDatabase.getInstance().getReference().child("BookingManager").child(user_id).child("Slots");
                            total_slots.setValue(15);
                            DatabaseReference collection = FirebaseDatabase.getInstance().getReference().child("BookingManager").child(user_id).child("Total");
                            collection.setValue(0);
                        }
                        else {
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(MainActivity.this,"Invalid Verification",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}