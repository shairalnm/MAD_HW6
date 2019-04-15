package com.group5.homework06;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener, SignUpFragment.OnSignUpFragmentInteractionListener,
        ChatRoomFragment.OnContactsFragmentInteractionListener {

    ArrayList<Message> messages = new ArrayList<>();
    public FirebaseUser currentUser = null;
    private FirebaseAuth mAuth;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageRef = firebaseStorage.getReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("messages");
    ChatRoomFragment chatRoomFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
           
            chatRoomFragment = new ChatRoomFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ChatRoomFragment.MESSAGE_LIST_KEY, messages);
            chatRoomFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, chatRoomFragment, "chat_room_fragment")
                    .commit();
    
        } else {

            LoginFragment loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, loginFragment, "login_fragment")
                    .commit();
        }
    }

    @Override
    public void loginAttempt(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("demo", "signInWithEmail: success");
                            currentUser = mAuth.getCurrentUser();

                            chatRoomFragment = new ChatRoomFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ChatRoomFragment.MESSAGE_LIST_KEY, messages);
                            chatRoomFragment.setArguments(bundle);

                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, chatRoomFragment, "chat_room_fragment")
                                    .commit();
                        } else {
                            Log.w("demo", "signInWithEmail: failure", task.getException());
                            Toast.makeText(MainActivity.this, "Login was not successful.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void goToSignUpFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignUpFragment(), "sign_up_fragment")
                .commit();
    }

    @Override
    public void signUpAttempt(String email, String password, final String firstName, final String lastName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User has been created", Toast.LENGTH_LONG).show();
                            currentUser = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(firstName + " " + lastName)
                                    .build();

                            currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("demo", "User profile updated.");
                                        chatRoomFragment = new ChatRoomFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(ChatRoomFragment.MESSAGE_LIST_KEY, messages);
                                        chatRoomFragment.setArguments(bundle);

                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, chatRoomFragment, "chat_room_fragment")
                                                .commit();
                                    }
                                }
                            });
                        } else {
                            Log.w("demo", "createUserWithEmail: failure", task.getException());
                            Toast.makeText(MainActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void goToLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, loginFragment, "login_fragment")
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void logoutAttempt() {
        mAuth.signOut();

        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, loginFragment, "login_fragment")
                .commit();
    }

    @Override
    public void addMessage(Message message) {
        messages.add(message);
        chatRoomFragment.notifyAdapter();
        myRef.setValue(messages);
    }



}