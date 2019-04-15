package com.group5.homework06;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SignUpFragment extends Fragment {

    OnSignUpFragmentInteractionListener mListener;

    public SignUpFragment() {
     
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        getActivity().setTitle("Sign Up");

        final EditText firstNameEditText = view.findViewById(R.id.first_name_editText);
        final EditText lastNameEditText = view.findViewById(R.id.last_name_editText);
        final EditText emailEditText = view.findViewById(R.id.email_on_signup_editText);
        final EditText passwordEditText = view.findViewById(R.id.password_on_signup_editText);
        final EditText confirmPasswordEditText = view.findViewById(R.id.confirm_password_editText);

        view.findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("demo", "onClick: entered");

                if(firstNameEditText.length() > 0 && lastNameEditText.length() > 0
                        && emailEditText.length() > 0 && passwordEditText.length() > 5
                        && confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                    Log.d("demo", "if statement: entered");
                    mListener.signUpAttempt(emailEditText.getText().toString(), passwordEditText.getText().toString(), firstNameEditText.getText().toString(), lastNameEditText.getText().toString());
                }
            }
        });

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToLoginFragment();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignUpFragment.OnSignUpFragmentInteractionListener) {
            mListener = (SignUpFragment.OnSignUpFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSignUpFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSignUpFragmentInteractionListener {
        void signUpAttempt(String email, String password, String firstName, String lastName);
        void goToLoginFragment();
    }
}
