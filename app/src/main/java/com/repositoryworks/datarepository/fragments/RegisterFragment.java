package com.repositoryworks.datarepository.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.repositoryworks.datarepository.activities.LoginActivity;
import com.repositoryworks.datarepository.activities.StartActivity;
import com.repositoryworks.datarepository.models.UserModel;
import com.repositoryworks.datarepository.utils.Constants;
import com.repositoryworks.datarepository.utils.dbaccess.DBManager;
import com.repositoryworks.datarepository.utils.fileUtils.FileUtilities;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterFragment extends Fragment {

    private static final String FRAGMENT_NUMBER = "FRAGMENT_NUMBER";
    private int FragmentNumber;
    private OnFragmentInteractionListener mListener;

    private DBManager mDBManager;
    private UserModel mUserModel;
    private EditText mUserName;
    private EditText mEmail;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPassword;
    private CircleImageView mImageChooser;
    private String mImagePath="";
    private final int FILE_REQUEST_CODE = 100;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 4;

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
        mImageChooser = ButterKnife.findById(view,R.id.add_image);

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
        mImageChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkReadPermission(getContext())){
                    requestReadPermissions();
                }else{
                    Log.i(Constants.APP_TAG,getString(R.string.log_has_permission));
                    launchFileChooser();
                }
            }
        });

        // Link the database
        mDBManager = ((LoginActivity) getActivity()).getDBManager();

        // SignUp button listeners
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    // Assign data to user model
                    mUserModel = new UserModel(mFirstName.getText().toString(),mLastName.getText().toString(),
                            mUserName.getText().toString(), mEmail.getText().toString(),
                            mPassword.getText().toString(),mImagePath);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(checkDatabase(mEmail.getText().toString(),mUserName.getText().toString())){
                                    saveUserToDatabase();

                                    // Go to StartActivity
                                    Intent intent = new Intent(getActivity(),StartActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    getActivity().finish();

                                }else{
                                    Toast.makeText(getContext(),getString(R.string.wrong_details),Toast.LENGTH_SHORT).show();
                                }
                            } catch (NoSuchAlgorithmException|IOException|ExecutionException|InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
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
     * @param editText Form field to edit email
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
     * @param password User Password
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
     * @param email User Email
     * @param user_name User Name
     */
    private boolean checkDatabase(final String email,final String user_name)
            throws NoSuchAlgorithmException, IOException, ExecutionException, InterruptedException {

        mDBManager.databaseOpenToRead();
        if(mDBManager.checkForUser(email,user_name)){
            Toast.makeText(getContext(),getString(R.string.user_exists),Toast.LENGTH_SHORT).show();
            mUserName.setError(getString(R.string.change_username));
            mEmail.setError(getString(R.string.change_email));
            return false;
        }else{
            Toast.makeText(getContext(),getString(R.string.registering),Toast.LENGTH_SHORT).show();
            mUserName.setError(null);
            mEmail.setError(null);
            return true;
        }
    }

    /**
     * Save the user to database
     *
     * @throws ExecutionException None
     * @throws InterruptedException None
     * @throws IOException None
     * @throws NoSuchAlgorithmException None
     */
    private void saveUserToDatabase() throws ExecutionException, InterruptedException, IOException, NoSuchAlgorithmException {

        mDBManager.databaseOpenToWrite();
        long id = mDBManager.createUser(mUserModel);
        Log.i(Constants.APP_TAG,getString(R.string.log_user_created)+String.valueOf(id));

        // set shared preferences
        mPreferences.edit().putBoolean(Constants.IS_LOGGED_IN,true).apply();
        mPreferences.edit().putLong(Constants.CURRENT_USER_ID,id).apply();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Show the FileChooser
     */
    private void launchFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent,getString(R.string.select_file)),FILE_REQUEST_CODE);
    }

    /**
     * Set the Image Path
     */
    private void setImagePath(Uri uri) throws IOException {
        if(!(mImagePath = FileUtilities.getFilePath(getContext(),uri))
                .equals(getString(R.string.no_result))){

            Log.i(Constants.APP_TAG,mImagePath);
            mImageChooser.setImageBitmap(FileUtilities.getImageBitmap(FileUtilities.getFileBytes(mImagePath)));
        }else{
            mImagePath="";
            Toast.makeText(getContext(),getString(R.string.image_error),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){

            case FILE_REQUEST_CODE:
                if(resultCode == LoginActivity.RESULT_OK){
                    Uri uri = data.getData();
                    Log.i(Constants.APP_TAG,uri.toString());
                    String mimeType = getContext().getContentResolver().getType(uri);

                    if(mimeType != null){
                        if(mimeType.equals("image/jpeg") || mimeType.equals("image/png")){
                            Log.i(Constants.APP_TAG,mimeType);
                            CropImage.activity(uri).start(getContext(),this);
                        }else{
                            Toast.makeText(getContext(),getString(R.string.select_image_file),Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }else {
                        Toast.makeText(getContext(),getString(R.string.internal_error),Toast.LENGTH_SHORT).show();
                        return;
                    }

                }else{
                    Toast.makeText(getContext(),getString(R.string.file_chooser_error),Toast.LENGTH_SHORT).show();
                }
            break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(resultCode == LoginActivity.RESULT_OK){
                    Uri resultUri = result.getUri();
                    Log.i(Constants.APP_TAG,resultUri.toString());
                    mImageChooser.setImageURI(resultUri);
                    mImagePath = resultUri.getPath();
                }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error = result.getError();
                    try {
                        throw error;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            break;

            default: break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Check for read external storage permission
     *
     * @return return if has permission
     */
    private boolean checkReadPermission(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request for read permissions from the user
     */
    private void requestReadPermissions(){
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:

                if(grantResults.length > 0){

                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        Log.i(Constants.APP_TAG,getString(R.string.log_got_permissions));
                        launchFileChooser();

                    }else if(grantResults[0] == PackageManager.PERMISSION_DENIED){

                        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                            new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                    alert.setTitle(getString(R.string.read_permission));
                                    alert.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alert.show();
                                }
                            }.run();

                        }else{
                            mImageChooser.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getContext(),getString(R.string.disabled_image),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    return;

                }else{
                    Toast.makeText(getContext(),getString(R.string.no_permission),Toast.LENGTH_SHORT).show();
                }
                break;

            default: break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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