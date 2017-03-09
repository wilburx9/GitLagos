package com.jadebyte.gitlagos.listeners;

public interface UserClickedListener {
    //Handles clicks on the gadget and  items of GadgetFragment, UserListFragment, CatsFragment and DetailsFragment
    void onUserClicked(String Object);
    void onLoadMoreClicked();
}
