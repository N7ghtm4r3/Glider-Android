package com.tecknobit.glider.ui.fragments.general;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tecknobit.glider.helpers.PasswordsAdapter;

import static com.tecknobit.glider.ui.activities.MainActivity.MAIN_ACTIVITY;

public abstract class PasswordsListFragment extends GliderFragment {

    protected RecyclerView passwords;

    protected void setPasswordsRecycler(int passwordsId) {
        passwords = viewContainer.findViewById(passwordsId);
        passwords.setLayoutManager(new LinearLayoutManager(MAIN_ACTIVITY));
        passwords.setAdapter(new PasswordsAdapter());
    }

}
