package com.godavarisandroid.mystore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.EditAddressActivity;
import com.godavarisandroid.mystore.activities.EditProfileActivity;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.utils.UserDetails;

/**
 * Created by Excentd11 on 4/27/2018.
 */

public class ProfileInformationFragment extends BaseFragment implements View.OnClickListener {
    private View view;

    private TextView mTxtUserName, mTxtEmail, mTxtMobile, mTxtCity, mTxtArea, mTxtApartment, mTxtBlock, mTxtFlat, mTxtStreet,
            mTxtLandmark, mTxtPincode;
    private ImageView mImgProfileEdit, mImgAddressEdit;
    private Intent mIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_information, container, false);

        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setText("My Profile");

        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtUserName = view.findViewById(R.id.txtUserName);
        mTxtEmail = view.findViewById(R.id.txtEmail);
        mTxtMobile = view.findViewById(R.id.txtMobile);
        mTxtCity = view.findViewById(R.id.txtCity);
        mTxtArea = view.findViewById(R.id.txtArea);
        mTxtApartment = view.findViewById(R.id.txtApartment);
        mTxtBlock = view.findViewById(R.id.txtBlock);
        mTxtFlat = view.findViewById(R.id.txtFlat);
        mTxtStreet = view.findViewById(R.id.txtStreet);
        mTxtLandmark = view.findViewById(R.id.txtLandmark);
        mTxtPincode = view.findViewById(R.id.txtPincode);

        mTxtUserName.setText(UserDetails.getInstance(getActivity()).getName());
        mTxtEmail.setText(UserDetails.getInstance(getActivity()).getEmailId());
        mTxtMobile.setText(UserDetails.getInstance(getActivity()).getPhoneNo());
        mTxtCity.setText(UserDetails.getInstance(getActivity()).getCity());
        mTxtArea.setText(UserDetails.getInstance(getActivity()).getArea());
        mTxtApartment.setText(UserDetails.getInstance(getActivity()).getApartment());
        mTxtBlock.setText(UserDetails.getInstance(getActivity()).getBlock());
        mTxtFlat.setText(UserDetails.getInstance(getActivity()).getFlat());
        mTxtStreet.setText(UserDetails.getInstance(getActivity()).getStreet());
        mTxtLandmark.setText(UserDetails.getInstance(getActivity()).getLandmark());
        mTxtPincode.setText(UserDetails.getInstance(getActivity()).getPincode());

        mImgProfileEdit = view.findViewById(R.id.imgProfileEdit);
        mImgAddressEdit = view.findViewById(R.id.imgAddressEdit);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mImgProfileEdit.setOnClickListener(this);
        mImgAddressEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgProfileEdit:
                /*Navigating to edit profile screen*/
                mIntent = new Intent(getActivity(), EditProfileActivity.class);
                startActivityForResult(mIntent, 101);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.imgAddressEdit:
                /*Navigate to edit address screen*/
                mIntent = new Intent(getActivity(), EditAddressActivity.class);
                startActivityForResult(mIntent, 102);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK && requestCode == 101) {
            if (data != null) {
                mTxtUserName.setText(data.getStringExtra("name"));
                mTxtEmail.setText(data.getStringExtra("email"));
                mTxtMobile.setText(data.getStringExtra("phone_no"));
            }
        } else if (resultCode == getActivity().RESULT_OK && requestCode == 102) {
            if (data != null) {
                mTxtCity.setText(data.getStringExtra("city"));
                mTxtArea.setText(data.getStringExtra("area"));
                mTxtApartment.setText(data.getStringExtra("apartment"));
                mTxtBlock.setText(data.getStringExtra("block"));
                mTxtFlat.setText(data.getStringExtra("flat"));
                mTxtStreet.setText(data.getStringExtra("street"));
                mTxtLandmark.setText(data.getStringExtra("landmark"));
                mTxtPincode.setText(data.getStringExtra("pincode"));
            }
        }
    }
}
