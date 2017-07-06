package com.repositoryworks.datarepository.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.Contract;

import java.security.NoSuchAlgorithmException;

import butterknife.ButterKnife;

public class LoginFragment extends Fragment {

    private static final String FRAGMENT_NUMBER = "FRAGMENT_NUMBER";
    private int FragmentNumber;
    private OnFragmentInteractionListener mListener;

    private EditText mEmail;
    private EditText mPassword;
    private DBManager mDBManager;

    // Set SharedPreferences
    private SharedPreferences mPreferences;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance(int param1) {
        LoginFragment fragment = new LoginFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inject Views
        mEmail = ButterKnife.findById(view,R.id.email);
        mPassword = ButterKnife.findById(view,R.id.pass);
        Button Login = ButterKnife.findById(view,R.id.login);

        // Set SharedPreferences
        mPreferences = getActivity().getSharedPreferences(Constants.APP_ACTIVITIES,Context.MODE_PRIVATE);

        // Link the database
        mDBManager = callDBManager();
        mDBManager.databaseOpenToRead();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    try {
                        if(mDBManager.validateUser(mEmail.getText().toString(),mPassword.getText().toString())){
                            Toast.makeText(getContext(),"Logging in...",Toast.LENGTH_SHORT).show();
                            mPreferences.edit().putBoolean(Constants.IS_LOGGED_IN,true).apply();
                            Intent intent = new Intent(getActivity(), StartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();
                        }else{
                            Toast.makeText(getContext(),"Wrong Details",Toast.LENGTH_SHORT).show();
                        }
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
     * @return True if correct field entries
     */
    @Contract(pure = true)
    private boolean validateForm(){

        if(mEmail.getText().toString().isEmpty()){
            mEmail.setError(getString(R.string.blank));
            return false;
        }

        if(mPassword.getText().toString().isEmpty()){
            mPassword.setError(getString(R.string.blank));
            return false;
        }

        return true;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}