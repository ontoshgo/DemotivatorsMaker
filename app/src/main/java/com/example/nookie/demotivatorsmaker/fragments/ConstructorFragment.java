package com.example.nookie.demotivatorsmaker.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nookie.demotivatorsmaker.interfaces.ImagePicker;
import com.example.nookie.demotivatorsmaker.interfaces.ImageSetter;
import com.example.nookie.demotivatorsmaker.R;
import com.example.nookie.demotivatorsmaker.views.ConstructorView;


public class ConstructorFragment extends Fragment implements ImagePicker, ImageSetter {

    private ImagePicker mActivityImagePicker;
    private ImageSetter mViewImageSetter;

    public ConstructorFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivityImagePicker = (ImagePicker)context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConstructorView v = (ConstructorView)view.findViewById(R.id.constructor_view);
        mViewImageSetter = (ImageSetter)v;
    }

    @Override
    public void pickImage(int sourceMode) {
        mActivityImagePicker.pickImage(sourceMode);
    }

    @Override
    public void setImage(Bitmap image) {
        mViewImageSetter.setImage(image);
    }
}