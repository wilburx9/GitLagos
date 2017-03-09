package com.jadebyte.gitlagos.fragments;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jadebyte.gitlagos.R;
import com.jadebyte.gitlagos.adapters.UserAdapter;
import com.jadebyte.gitlagos.listeners.EndlessRecyclerViewScrollListener;
import com.jadebyte.gitlagos.listeners.UserClickedListener;
import com.jadebyte.gitlagos.pojos.UserItem;
import com.jadebyte.gitlagos.utils.MyVolleyError;
import com.jadebyte.gitlagos.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
public class UserListFragment extends Fragment {

    //Constants
    private final String TAG = "UserListFragment";
    private final String KEY_USER_ITEMS = "userItems";

    //Views
    @BindView(R.id.user_recycler) RecyclerView recyclerView;
    @BindView(R.id.user_pro_bar) ProgressBar mProgressBar;
    @BindView(R.id.user_info_error_root) LinearLayout errorLayout;
    @BindView(R.id.user_info_error_text) TextView errorText;
    @BindView(R.id.user_info_error_button) Button retryButton;
    @BindView(R.id.user_info_img) ImageView errorImg;


    //Fields
    private final String userUrl = "https://api.github.com/search/users?q=location:lagos+language:java&per_page=20";
    private String moreUsersUrl;
    private List<UserItem> mUserItemList;
    private UserAdapter adapter;
    private JsonObjectRequest usersRequest;
    private LinearLayoutManager mLayoutManager;
    private Unbinder mUnbinder;
    private boolean hasFailed = false;
    private String errorMessage;
    private UserClickedListener onUserClickedListener = sUserCallbacks;
    private int pageNumber = 2;
    private boolean isMoreLoading = false;

    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            hasFailed = savedInstanceState.getBoolean("hasFailed");
            errorMessage = savedInstanceState.getString("errorMessage");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_USER_ITEMS)) {
            // A successful network call has been made and parsed previously.
            mUserItemList = savedInstanceState.getParcelableArrayList(KEY_USER_ITEMS);
        } else if (hasFailed) {
            // An unsuccessful network call has been made previously. Just show the error layout
            errorText.setText(errorMessage);
            errorLayout.setVisibility(View.VISIBLE);
            mUserItemList = new ArrayList<>();
        } else {
            // No network call has been made or hasn't returned any response
            mUserItemList = new ArrayList<>();
            getUserArray(userUrl, false);
        }

        setUpWidgets();
        widgetListeners();

        return view;
    }

    private void setUpWidgets() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new UserAdapter(mUserItemList);
        recyclerView.setAdapter(adapter);
        mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity
                (), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
    }

    private void widgetListeners() {
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                getUserArray(userUrl, false);
            }
        });

        //Add the scroll listener
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener( mLayoutManager) {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                if (!isMoreLoading) {
                    moreUsersUrl = userUrl + "&page=" + getPageNumber();
                    getUserArray(moreUsersUrl, true);
                }
            }
        });

        adapter.setOnUserClickedListener(new UserClickedListener() {
            @Override
            public void onUserClicked(String userObject) {
                onUserClickedListener.onUserClicked(userObject);
            }

            @Override
            public void onLoadMoreClicked() {
                moreUsersUrl = userUrl + "&page=" + getPageNumber();
                getUserArray(moreUsersUrl, true);
            }
        });
    }

    //This will users from github
    private void getUserArray(String url, final boolean isLoadMoreUsers){
        final int initialItems = 0;
        if (isLoadMoreUsers) {
            adapter.getItemCount();
            isMoreLoading = true;
            processBottomViews(UserAdapter.Status.PRE_REQUEST);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        //Creating a json request
        usersRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Successful request
                if (isLoadMoreUsers) {
                    isMoreLoading = false;
                    processBottomViews(UserAdapter.Status.SUCCESS);
                    parseUserArray(response, true);
                } else {
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                        if (adapter.getItemCount() == 1) {
                            parseUserArray(response, false);
                        }
                    }
                    hasFailed = false;
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (isLoadMoreUsers) {
                    isMoreLoading = false;
                    if (initialItems == adapter.getItemCount()) {
                        setPageNumber(getPageNumber() - 1);
                    }
                    processBottomViews(UserAdapter.Status.FAILED);
                } else {
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                    if (adapter.getItemCount() == 1) {
                        errorMessage = MyVolleyError.errorMessage(error, getActivity());
                        errorText.setText(errorMessage);
                        errorLayout.setVisibility(View.VISIBLE);
                        hasFailed = true;
                    }
                }
            }
        }){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Response<JSONObject>  resp = super.parseNetworkResponse(response);
                long currentTime = System.currentTimeMillis();
                long cacheTime = currentTime + 60 * 60 * 1000; //keeps cache for 1 hour

                if (!resp.isSuccess()) {
                    return resp;
                }

                Cache.Entry entry = resp.cacheEntry;
                if (entry == null) {
                    entry = new Cache.Entry();
                    entry.data = response.data;
                    entry.responseHeaders = response.headers;

                }
                entry.ttl = cacheTime;
                entry.softTtl = 0;
                return Response.success(resp.result, entry);
            }
        };


        RetryPolicy policy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        usersRequest.setRetryPolicy(policy);
        usersRequest.setShouldCache(true);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(usersRequest);

    }

    private void parseUserArray(JSONObject object, boolean isLoadMoreUsers) {
        try {
            JSONArray array = object.getJSONArray("items");
            for(int i = 0; i<array.length(); i++) {
                UserItem userItem = new UserItem();
                JSONObject jsonObject = array.getJSONObject(i);
                userItem.setAvatarUrl(jsonObject.getString("avatar_url"));
                userItem.setUsername(jsonObject.getString("login"));
                userItem.setUserObject(jsonObject.toString());
                mUserItemList.add(userItem);

                if (isLoadMoreUsers) {
                    mUserItemList.addAll(userItem);
                }
            }

            if (isLoadMoreUsers) {
                int curSize = adapter.getItemCount();
                adapter.notifyItemRangeChanged(curSize, mUserItemList.size()-1);
            } else {
                adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processBottomViews(UserAdapter.Status status) {
        adapter.processBottomViews(status);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (adapter != null && adapter.getItemCount() > 1 ) {
            savedInstanceState.putParcelableArrayList(KEY_USER_ITEMS, (ArrayList<? extends Parcelable>) mUserItemList);
        }
        savedInstanceState.putBoolean("hasFailed", hasFailed);
        savedInstanceState.putInt("userPage", pageNumber);
        savedInstanceState.putString("errorMessage", errorMessage);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            pageNumber = savedInstanceState.getInt("userPage");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onUserClickedListener = (UserClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement UserClickedListener");
        }
    }


    public int getPageNumber() {
        return pageNumber++;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    private static UserClickedListener sUserCallbacks = new UserClickedListener() {
        @Override
        public void onUserClicked(String userObject) {

        }

        @Override
        public void onLoadMoreClicked() {

        }
    };

    @Override
    public void onDestroyView(){

        if (usersRequest != null && !usersRequest.isCanceled()) {
            usersRequest.cancel();
        }
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
