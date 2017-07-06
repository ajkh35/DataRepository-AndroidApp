package com.repositoryworks.datarepository.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.repositoryworks.datarepository.R;
import com.repositoryworks.datarepository.activities.StartActivity;
import com.repositoryworks.datarepository.models.UserModel;
import com.repositoryworks.datarepository.utils.Constants;
import com.repositoryworks.datarepository.utils.dbaccess.DBManager;

import org.jetbrains.annotations.Contract;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

public class RegisterFragment extends Fragment {

    private static final String FRAGMENT_NUMBER = "FRAGMENT_NUMBER";
    private int FragmentNumber;
    private OnFragmentInteractionListener mListener;

    private UserModel mUserModel;
    private EditText mUserName;
    private EditText mEmail;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPassword;
    private DBManager mDBManager;

    // Set SharedPreferences
    private SharedPreferences mPreferences;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment RegisterFragment.
     */
    public static RegisterFragment newInstance(int param1) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_NUMBER, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FragmentNumber = getArguments().getInt(FRAGMENT_NUMBER);
            Log.i("Fragment number",String.valueOf(FragmentNumber));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Inject views
        Button SignUp = ButterKnife.findById(view,R.id.signup);
        mFirstName = ButterKnife.findById(view,R.id.first_name);
        mLastName = ButterKnife.findById(view,R.id.last_name);
        mUserName = ButterKnife.findById(view,R.id.user_name);
        mEmail = ButterKnife.findById(view,R.id.email);
        mPassword = ButterKnife.findById(view,R.id.pass);

        // Set SharedPreferences
        mPreferences = getActivity().getSharedPreferences(Constants.APP_ACTIVITIES,Context.MODE_PRIVATE);

        // Set the listeners to views
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                verifyEmail(mEmail);
            }
        });
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                verifyPassword(mPassword);
            }
        });
        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Link to the database
        mDBManager = callDBManager();
        mDBManager.databaseOpenToRead();

        // SignUp button listeners
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){

                    // Assign data to user model
                    mUserModel = new UserModel(mFirstName.getText().toString(),mLastName.getText().toString(),
                            mUserName.getText().toString(), mEmail.getText().toString(), mPassword.getText().toString());
                    try {
                        checkDatabase(mEmail.getText().toString(), mUserName.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    /**
     * Validate form fields
     * @return Return if all fields are correct
     */
    private boolean validateForm(){

        if(mFirstName.getText().toString().isEmpty()){
            mFirstName.setError(getString(R.string.REQUIRED_FIELD));
            return false;
        }

        if(mLastName.getText().toString().isEmpty()){
            mLastName.setError(getString(R.string.REQUIRED_FIELD));
            return false;
        }

        if(mUserName.getText().toString().isEmpty()){
            mUserName.setError(getString(R.string.REQUIRED_FIELD));
            return false;
        }

        if(mEmail.getText().toString().isEmpty() || mEmail.getError() != null){
            mEmail.setError(getString(R.string.REQUIRED_FIELD));
            return false;
        }

        if(mPassword.getText().toString().isEmpty() || mPassword.getError() != null){
            mPassword.setError(getString(R.string.REQUIRED_FIELD));
            return false;
        }

        return true;
    }

    /**
     * Validate email field
     * @param editText
     * @return return true if correct
     */
    private boolean verifyEmail(EditText editText){
        final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);
        final String EMAIL_ERROR = getString(R.string.EMAIL_ERROR);
        final String REQUIRED_MSG = getString(R.string.REQUIRED_FIELD);

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        // pattern doesn't match so returning false
        if (!EMAIL_REGEX.matcher(text).find()) {
            editText.setError(EMAIL_ERROR);
            return false;
        }

        return true;
    }

    /**
     * Validate password field
     * @param password
     * @return return true if correct
     */
    private boolean verifyPassword(EditText password){

        String TOO_SHORT = getString(R.string.FIELD_SHORT);

        String pass = password.getText().toString().trim();
        password.setError(null);

        if(pass.length() < 6){
            password.setError(TOO_SHORT);
            return false;
        }

        return true;
    }

    /**
     * Check the database for sign up form field values
     * @param email
     * @param user_name
     */
    private void checkDatabase(String email, String user_name) throws NoSuchAlgorithmException {

        if(mDBManager.checkForUser(email,user_name)){
            Toast.makeText(getContext(),"User Exists",Toast.LENGTH_SHORT).show();
            mUserName.setError("Change UserName");
            mEmail.setError("Change Email");
        }else{
            Toast.makeText(getContext(),"User does not exist",Toast.LENGTH_SHORT).show();
            mUserName.setError(null);
            mEmail.setError(null);

            // Add record to database
            mDBManager.databaseClose();
            mDBManager.databaseOpenToWrite();
            long id = mDBManager.createUser(mUserModel);
            Log.i("Record created ID",String.valueOf(id));
            mPreferences.edit().putBoolean(Constants.IS_LOGGED_IN,true).apply();

            // Go to StartActivity
            Intent intent = new Intent(getActivity(), StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }
    }

    /**
     * Method to access the database
     */
    @Contract(" -> !null")
    private DBManager callDBManager(){
        return new DBManager(getContext());
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        mDBManager.databaseClose();
        super.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}