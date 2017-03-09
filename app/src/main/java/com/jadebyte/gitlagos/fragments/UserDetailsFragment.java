package com.jadebyte.gitlagos.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jadebyte.gitlagos.R;
import com.jadebyte.gitlagos.utils.Constants;
import com.jadebyte.gitlagos.utils.MyGlide;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
public class UserDetailsFragment extends Fragment {
    //Constants
    private final String TAG = "UserDetailsFragment";

    //Views
    @BindView(R.id.user_avatar) ImageView avatarView;
    @BindView(R.id.user_name) TextView nameView;
    @BindView(R.id.user_url) TextView urlView;
    @BindView(R.id.share_fab) FloatingActionButton shareButton;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    //Fields
    private Unbinder unbinder;
    private String avaterUrl;
    private String name;
    private String url;


    public UserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.user_fragment_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        getBundle();
        setUpWidgets();
        widgetListeners();
        return view;
    }

    private void setUpWidgets() {
        MyGlide.load(getActivity(), avatarView, avaterUrl, progressBar);
        nameView.setText(name);
        urlView.setText(url);
    }

    private void getBundle(){
        String JSONObject = getArguments().getString(Constants.Keys.JSON_OBJECT);
        try {
            JSONObject jsonObject = new JSONObject(JSONObject);
            avaterUrl = jsonObject.getString("avatar_url");
            name = jsonObject.getString("login");
            url = jsonObject.getString("html_url");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void widgetListeners() {
        urlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, name, url));
                startActivity(intent);
            }
        });
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }

}
