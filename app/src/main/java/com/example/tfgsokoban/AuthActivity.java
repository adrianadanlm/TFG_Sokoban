package com.example.tfgsokoban;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button signUpButton, loginButton;
    char choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

        //Registrar cuenta
        signUpButton = (Button) findViewById(R.id.playButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 'R';
                checkFields(choice);
            }
        });

        //Acceder con cuenta creada
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 'A';
                checkFields(choice);
            }
        });

    }

    //Comprobar que los campos tienen la información necesaria
    private void checkFields(char choice){
        EditText email = (EditText) findViewById(R.id.emailEditText);
        EditText pass = (EditText) findViewById(R.id.passEditText);
        String sEmail = email.getText().toString();
        String sPass = pass.getText().toString();
        if (!(sEmail.isEmpty()) && !(sPass.isEmpty())) {
            switch(choice){
                case 'R':
                    createAccount (sEmail, sPass);
                    break;
                case 'A':
                    signIn(sEmail, sPass);
                    break;
            }
        } else {
            Toast.makeText(AuthActivity.this, "Debe introducir email y clave",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount(String email, String pass) {
        //Creamos autenticacion de usuario
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Creamos el objeto usuario
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("currentLevel", 1);

                            //Creamos al usuario en la BBDD
                            db.collection("user")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(AuthActivity.this, documentReference.getId(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AuthActivity.this, "Error adding document",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            //Vamos a la Actividad Home.
                            goHome();
                        } else {
                            Toast.makeText(AuthActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //FirebaseUser user = mAuth.getCurrentUser();
                        if (task.isSuccessful()) {
                            goHome();
                        } else {
                            Toast.makeText(AuthActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goHome() {
        Intent intentGoHome = new Intent(this, HomeActivity.class);
        startActivity(intentGoHome);
    }

}