package com.jadebyte.gitlagos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.jadebyte.gitlagos.R;
import com.jadebyte.gitlagos.listeners.UserClickedListener;
import com.jadebyte.gitlagos.pojos.UserItem;
import com.jadebyte.gitlagos.viewholders.BottomViewHolder;
import com.jadebyte.gitlagos.viewholders.UserViewHolder;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private UserClickedListener onUserClicked;
    private List<UserItem> mUserItems;
    private int lastPosition = -1;
    private final int VIEW_USER = 0;
    private final int VIEW_PROG = 1;
    private BottomViewHolder bottomViewHolder;
    public enum Status {FAILED, SUCCESS, PRE_REQUEST}

    public UserAdapter(List<UserItem> userItems) {
        super();

        //Getting all users
        this.mUserItems = userItems;
        this.onUserClicked = null;
    }

    public void setOnUserClickedListener (UserClickedListener onItemClickedListener) {
        this.onUserClicked = onItemClickedListener;
    }

    public void processBottomViews(Status status) {
        if (bottomViewHolder != null) {
            bottomViewHolder.processViews(status);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPosition(position)) {
            return VIEW_USER;
        } else {
            return VIEW_PROG;
        }
    }

    private boolean isPosition(int position) {
        return position != getItemCount()-1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            if (viewType == VIEW_USER) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_card, parent, false);
                return new UserViewHolder(v, onUserClicked);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_bottm_layout, parent, false);
                bottomViewHolder =  new BottomViewHolder(v, onUserClicked);
                return bottomViewHolder;
            }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bindModel(mUserItems.get(position));
            setAnimation(holder.itemView, position);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        //if the item wasn't already displayed on screen. it's animated
        if (position > lastPosition) {
            TranslateAnimation animation;
            if (isEven(position)) {
                animation = new TranslateAnimation(-50, 0, 0, 0);
            } else {
                animation = new TranslateAnimation(50, 0, 0, 0);
            }

            animation.setDuration(400);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount(){
        //Return the number of items in the data set
        return mUserItems == null ? (0) : mUserItems.size()+1;
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        holder.itemView.clearAnimation();
    }

    private boolean isEven(int position) {
        return (position & 1) == 0;
    }

}
