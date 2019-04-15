package com.group5.homework06;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static android.app.Activity.RESULT_OK;



public class ChatRoomFragment extends Fragment {

    public static int PICKED_IMAGE = 1;
    public static String MESSAGE_LIST_KEY = "message_list_key";
    private boolean uploadingImage = false;

    OnContactsFragmentInteractionListener mListener;

    private MessageAdapter adapter;
    private RecyclerView.LayoutManager myLayoutManager;
    private String downloadUrl = null;
    private ImageView addImageView;
    private MainActivity mainActivity = null;

    public ChatRoomFragment() {
       
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        final View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

        getActivity().setTitle("Chat Room");

        mainActivity = (MainActivity) getActivity();

        RecyclerView recyclerView = view.findViewById(R.id.contacts_recyclerView);
        recyclerView.setHasFixedSize(true);


        myLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(myLayoutManager);

        adapter = new MessageAdapter((ArrayList<Message>) this.getArguments().getSerializable(MESSAGE_LIST_KEY));
        recyclerView.setAdapter(adapter);

        mainActivity.myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                adapter.myData.clear();

                if(dataSnapshot.exists()) {
          
                    for (DataSnapshot expenseSnap : dataSnapshot.getChildren()) {
                        Message message = expenseSnap.getValue(Message.class);

                        adapter.myData.add(message);

                        Log.d("demo", message.toString());
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("demo", "onCancelled: databaseError "  + databaseError);
            }
        });


        TextView fullNameTextView = view.findViewById(R.id.full_name_in_chat_room_textView);
        fullNameTextView.setText(mainActivity.currentUser.getDisplayName());

        final EditText messageEditText = view.findViewById(R.id.message_in_chat_room_editText);
        addImageView = view.findViewById(R.id.add_image_in_chat_room_imageView);


        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = new Intent();
                chooseImageIntent.setType("image/*");
                chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(chooseImageIntent, PICKED_IMAGE);
            }
        });
   
        view.findViewById(R.id.send_message_in_chat_room_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((messageEditText.length() > 0 || downloadUrl != null) && !uploadingImage) {
             
                    Message message = new Message();
                    message.imageUrl = downloadUrl;
                    message.messageText = messageEditText.getText().toString();
                    message.dateTime = new Date();
                    message.firstName = mainActivity.currentUser.getDisplayName().substring(0, mainActivity.currentUser.getDisplayName().indexOf(' '));
                    message.lastName = mainActivity.currentUser.getDisplayName().substring(mainActivity.currentUser.getDisplayName().indexOf(' ') + 1);

                   
                    downloadUrl = null;
                    addImageView.setImageResource(R.drawable.addimage);
                    messageEditText.setText("");

     
                    mListener.addMessage(message);
                }
            }
        });


        view.findViewById(R.id.logout_imageView_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logoutAttempt();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("demo", "onActivityResult: " + data.getExtras());

        Log.d("demo", "requestCode: " + requestCode + " resultCode: " + resultCode + " RESULT_OK: " + RESULT_OK);

      
        if(resultCode == RESULT_OK && requestCode == PICKED_IMAGE) {
            uploadingImage = true;

            final Uri filePath = data.getData();

            MainActivity mainActivityContext = (MainActivity) getContext();

            Random random = new Random();
            final StorageReference imageRef = mainActivityContext.storageRef.child("images/" + filePath.getLastPathSegment() + random.nextInt(99) + random.nextInt(99));
            UploadTask uploadTask = imageRef.putFile(filePath);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("demo", "onFailure: " + filePath);
                    uploadingImage = false;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("demo", "onSuccess: " + imageRef.getDownloadUrl());
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            Log.d("demo", "onSuccess: " + downloadUrl);
                            Picasso.get().load(downloadUrl).placeholder(R.drawable.loading).into(addImageView);
                            uploadingImage = false;
                        }
                    });
                }
            });
        }
    }

    public void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatRoomFragment.OnContactsFragmentInteractionListener) {
            mListener = (ChatRoomFragment.OnContactsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContactsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnContactsFragmentInteractionListener {
   
        void addMessage(Message message);
        void logoutAttempt();
    }
}
