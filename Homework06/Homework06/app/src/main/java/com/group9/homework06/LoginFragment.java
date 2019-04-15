package com.group9.homework06;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

//InClass09
//Group 9
//Rockford Stoller
//Ryan Swaim

public class LoginFragment extends Fragment {

    OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        getActivity().setTitle("Login");

        final EditText emailEditText = view.findViewById(R.id.email_editText);
        final EditText passwordEditText = view.findViewById(R.id.password_editText);

        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailEditText.length() > 0 && passwordEditText.length() > 0) {
                    mListener.loginAttempt(emailEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });

        view.findViewById(R.id.goTo_singup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToSignUpFragment();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.OnLoginFragmentInteractionListener) {
            mListener = (LoginFragment.OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument type and name
        void loginAttempt(String email, String password);
        void goToSignUpFragment();
    }
}
