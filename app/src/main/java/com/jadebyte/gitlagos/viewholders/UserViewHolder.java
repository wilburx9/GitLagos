

/**
 * Created by William Wilbur on 1/1/17.
 */
package com.jadebyte.gitlagos.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jadebyte.gitlagos.R;
import com.jadebyte.gitlagos.listeners.UserClickedListener;
import com.jadebyte.gitlagos.pojos.UserItem;
import com.jadebyte.gitlagos.utils.MyGlide;

import butterknife.BindView;
import butterknife.ButterKnife;public class UserViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.user_avatar) ImageView userAvatar;
    @BindView(R.id.user_name) TextView userName;
    @BindView(R.id.item_root) LinearLayout rootLayout;
    
    private UserClickedListener clickedListener;
    
    public UserViewHolder(final View View,UserClickedListener clickedListener) {
        super(View);
        ButterKnife.bind(this, View);
        this.clickedListener = clickedListener;
    }

    public void bindModel(final UserItem userItem) {
        MyGlide.load(itemView.getContext(), userAvatar, userItem.getAvatarUrl(), null);

        userName.setText(userItem.getUsername());
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedListener.onUserClicked(userItem.getUserObject());
            }
        });
    }
}
