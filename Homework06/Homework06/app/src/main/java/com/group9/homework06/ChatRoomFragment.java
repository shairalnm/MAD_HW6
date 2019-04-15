package com.group9.homework06;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

//Homework06
//Group 9
//Rockford Stoller
//Ryan Swaim

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
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

        getActivity().setTitle("Chat Room");

        mainActivity = (MainActivity) getActivity();

        //find the recycler view and set fixed size
        RecyclerView recyclerView = view.findViewById(R.id.contacts_recyclerView);
        recyclerView.setHasFixedSize(true);

        //use a linear layout manager
        myLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(myLayoutManager);

        //set the adapter
        adapter = new MessageAdapter((ArrayList<Message>) this.getArguments().getSerializable(MESSAGE_LIST_KEY));
        recyclerView.setAdapter(adapter);

        //setting database reference addValueEventListener
        //region
        mainActivity.myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //clear the array list and repopulate it
                adapter.myData.clear();

                if(dataSnapshot.exists()) {
                    //if the data snapshot exists. meaning the location has a least one expense at start, after removal, or after adding an expense
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
        //endregion

        TextView fullNameTextView = view.findViewById(R.id.full_name_in_chat_room_textView);
        fullNameTextView.setText(mainActivity.currentUser.getDisplayName());

        final EditText messageEditText = view.findViewById(R.id.message_in_chat_room_editText);
        addImageView = view.findViewById(R.id.add_image_in_chat_room_imageView);

        //add image button
        //region
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = new Intent();
                chooseImageIntent.setType("image/*");
                chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(chooseImageIntent, PICKED_IMAGE);
            }
        });
        //endregion

        //send message button
        //region
        view.findViewById(R.id.send_message_in_chat_room_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((messageEditText.length() > 0 || downloadUrl != null) && !uploadingImage) {
                    //create message and assign values
                    Message message = new Message();
                    message.imageUrl = downloadUrl;
                    message.messageText = messageEditText.getText().toString();
                    message.dateTime = new Date();
                    message.firstName = mainActivity.currentUser.getDisplayName().substring(0, mainActivity.currentUser.getDisplayName().indexOf(' '));
                    message.lastName = mainActivity.currentUser.getDisplayName().substring(mainActivity.currentUser.getDisplayName().indexOf(' ') + 1);

                    //clear message creation fields
                    downloadUrl = null;
                    addImageView.setImageResource(R.drawable.addimage);
                    messageEditText.setText("");

                    //add message
                    mListener.addMessage(message);
                }
            }
        });
        //endregion

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

        //store image chosen
        //region
        if(resultCode == RESULT_OK && requestCode == PICKED_IMAGE) {
            //stop from sending message until image is uploaded successfully or fails.
            uploadingImage = true;

            //else returning from getting from gallery
            final Uri filePath = data.getData();

            MainActivity mainActivityContext = (MainActivity) getContext();

            //random for generating two numbers between 0 and 99 for eliminating overwrites
            Random random = new Random();
            final StorageReference imageRef = mainActivityContext.storageRef.child("images/" + filePath.getLastPathSegment() + random.nextInt(99) + random.nextInt(99));
            UploadTask uploadTask = imageRef.putFile(filePath);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("demo", "onFailure: " + filePath);
                    uploadingImage = false;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
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
        //endregion
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
        // TODO: Update argument type and name
        //void goToCreateContactFragment();
        void addMessage(Message message);
        void logoutAttempt();
    }
}
