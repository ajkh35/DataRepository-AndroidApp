package com.repositoryworks.datarepository.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.repositoryworks.datarepository.R;
import com.repositoryworks.datarepository.activities.LoginActivity;
import com.repositoryworks.datarepository.activities.StartActivity;
import com.repositoryworks.datarepository.fragmentAdapters.SettingsAdapter;
import com.repositoryworks.datarepository.models.UserModel;
import com.repositoryworks.datarepository.utils.Constants;
import com.repositoryworks.datarepository.utils.dbaccess.DBManager;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private static final String FRAGMENT_NUMBER = "FRAGMENT_NUMBER";
    private static final String USER_MODEL = "USER_MODEL";

    private int FragmentNumber;
    private UserModel mUserModel;

    private OnFragmentInteractionListener mListener;

    private ListView mList;
    private SettingsAdapter mAdapter;
    private DBManager mDBManager;
    private static final int FILE_REQUEST_CODE = 100;

    // Update alert dialog instance variables
    private Dialog mDialog;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mUserName;
    private CircleImageView mProfilePic;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance(int param1, UserModel param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_NUMBER, param1);
        args.putParcelable(USER_MODEL,param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FragmentNumber = getArguments().getInt(FRAGMENT_NUMBER);
            mUserModel = getArguments().getParcelable(USER_MODEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mList = ButterKnife.findById(view,R.id.settings_list);
        mAdapter = new SettingsAdapter(getContext());
        mList.setAdapter(mAdapter);
        mDBManager = ((StartActivity) getActivity()).getDBManager();

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SwitchCompat switchCompat = ButterKnife.findById(parent,R.id.notification_switch);
                performLayoutClickAction(switchCompat,position);
            }
        });
        return view;
    }

    /**
     * Perform click event action
     * @param position Item position
     */
    private void performLayoutClickAction(SwitchCompat switchCompat,int position){
        switch(position){
            case 0:
                // Notifications
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            Toast.makeText(getContext(),"Checked",Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getContext(),"Unchecked",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case 1:
                // Update account details
                viewUpdateDialog();
                break;
            case 2:
                // Delete account
                viewDeleteAlert();
                break;
            case 3:
                // About
                Toast.makeText(getContext(),"Toast about",Toast.LENGTH_SHORT).show();
                break;
            case 4:
                // Help
                Toast.makeText(getContext(),"Toast help",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Delete the user
     */
    public void deleteUser(){
        mDBManager.deleteUserByID(StartActivity.sSharedPreferences.getLong(Constants.CURRENT_USER_ID,-1));
    }

    /**
     * Delete user alert
     */
    private void viewDeleteAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getString(R.string.delete_user));
        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete from database
                deleteUser();

                // Clear preferences
                StartActivity.sSharedPreferences.edit().clear().apply();

                dialog.dismiss();
            }
        });
        alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show
        alert.create();
        alert.show();
    }

    /**
     * Update user details
     */
    private void viewUpdateDialog(){
        mDialog = new Dialog(getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.user_update_dialog,null);
        mDialog.setContentView(view);

        mFirstName = (EditText) mDialog.findViewById(R.id.edit_first_name);
        mLastName = (EditText) mDialog.findViewById(R.id.edit_last_name);
        mUserName = (EditText) mDialog.findViewById(R.id.edit_user_name);
        ImageView cancel = (ImageView) mDialog.findViewById(R.id.update_cancel);
        mProfilePic = (CircleImageView) mDialog.findViewById(R.id.update_profile_pic);
        Button update = (Button) mDialog.findViewById(R.id.update_user);

        loadUpdateAlertViews(mUserModel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFileChooser();
            }
        });
        update.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                mUserModel.setFirstName(mFirstName.getText().toString());
                mUserModel.setLastName(mLastName.getText().toString());
                mUserModel.setUserName(mUserName.getText().toString());
                try {
                    mDBManager.databaseOpenToWrite();
                    if(mDBManager.updateUserByID(mUserModel,
                            StartActivity.sSharedPreferences.getLong(Constants.CURRENT_USER_ID,-1))){
                        Toast.makeText(getContext(),getString(R.string.update_success),Toast.LENGTH_SHORT).show();
                        Log.i(Constants.APP_TAG,getString(R.string.update_success));
                        ((StartActivity) getActivity()).setProfilePic();
                        Constants.USER_JUST_UPDATED = true;
                        mDialog.dismiss();
                    }else{
                        Toast.makeText(getContext(),getString(R.string.update_failed),Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mDialog.show();
    }

    /**
     * Load up views for update alert
     * @param model Current UserModel object
     */
    private void loadUpdateAlertViews(UserModel model){
        mFirstName.setText(model.getFirstName());
        mLastName.setText(model.getLastName());
        mUserName.setText(model.getUserName());
        mProfilePic.setImageBitmap(model.getImageBitmap());
    }

    /**
     * Launch the image chooser
     */
    private void launchFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent,getString(R.string.select_file)),FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case FILE_REQUEST_CODE:
                if(resultCode == StartActivity.RESULT_OK){
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
                    }else{
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
                    Log.i(Constants.APP_TAG,resultUri.getPath());
                    mProfilePic.setImageURI(resultUri);
                    mUserModel.setProfilePic(resultUri.getPath());
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