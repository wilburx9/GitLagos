package com.jadebyte.gitlagos.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jadebyte.gitlagos.R;
import com.jadebyte.gitlagos.fragments.BlankFragment;
import com.jadebyte.gitlagos.fragments.UserDetailsFragment;
import com.jadebyte.gitlagos.fragments.UserListFragment;
import com.jadebyte.gitlagos.listeners.UserClickedListener;
import com.jadebyte.gitlagos.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jadebyte.gitlagos.activities.MyFile.readFromAssets;

public class MainActivity extends AppCompatActivity implements UserClickedListener {

    //Constants
    private final String TAG = "MainActivity";

    //Views
    @BindView(R.id.user_toolbar) Toolbar toolbar;
    @BindView(R.id.user_appbar_layout) AppBarLayout appBarLayout;
    //Fields
    private boolean hasTwoPanes;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_user);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        hasTwoPanes = getResources().getBoolean(R.bool.has_two_panes);

        if (savedInstanceState == null) {
            launchUserListFragment(false, Constants.URLS.DEFAULT_URL);
            if (hasTwoPanes) {
                launchBlankFragment();
            }
        } else {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");

        }

    }


     // Start a fragment that does nothing but display
     // {@link com.jadebyte.gitlagos.R.string#details_stub } on the right pane if the device is a
     // tablet
    private void launchBlankFragment() {
        if (hasTwoPanes) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            ft.add(R.id.user_content_details, new BlankFragment());
            ft.commit();
        }
    }

    private void launchUserListFragment(boolean shouldBackStack, String userUrl) {
        Fragment fragment = new UserListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userUrl", userUrl);
        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        if (shouldBackStack) {
            ft.addToBackStack(null);
        }
        ft.replace(R.id.user_content_frame, fragment, "UserListFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_license:
                showLicenseDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Start {@link UserDetailsFragment
    public void launchDetailsFrag(@NonNull String userObject) {
        UserDetailsFragment detailsFragment = new UserDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Keys.JSON_OBJECT, userObject);
        detailsFragment.setArguments(bundle);
        int container = hasTwoPanes ? R.id.user_content_details : R.id.user_content_frame;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.add(container, detailsFragment, "UserDetailsFragment");
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }

    protected void onSaveInstanceState(Bundle outState) {
        if (mContent != null) {
            getSupportFragmentManager().putFragment(outState, "mContent", mContent);
        }
        super.onSaveInstanceState(outState);
    }

    private void showLicenseDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_license, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        TextView textView = (TextView) view.findViewById(R.id.license_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(readFromAssets(this, "license.html"), Html
                    .FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(readFromAssets(this, "license.html")));
        }
        builder.show();
    }


    @Override
    public void onUserClicked(String userObject) {
        launchDetailsFrag(userObject);
    }

    @Override
    public void onLoadMoreClicked() {
        // Do nothing
    }
}
