package com.tecknobit.glider.helpers;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.glider.R;

import java.util.List;

import static android.view.LayoutInflater.from;
import static com.tecknobit.glider.ui.activities.MainActivity.MAIN_ACTIVITY;

public class PasswordsAdapter extends RecyclerView.Adapter<PasswordsAdapter.PasswordView> {

    @NonNull
    @Override
    public PasswordsAdapter.PasswordView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PasswordView(from(MAIN_ACTIVITY).inflate(R.layout.password_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordsAdapter.PasswordView holder, int position) {
        holder.tail.setText("Youtube");
        holder.scopes.setAdapter(new ArrayAdapter<>(MAIN_ACTIVITY, android.R.layout.simple_list_item_1,
                List.of("prova", "toutrwe")));
        holder.password.setText("gagagvijwgenp+zmbons");
    }

    @Override
    public int getItemCount() {
        return 10;
    }


    public static class PasswordView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final MaterialTextView tail;
        private final AutoCompleteTextView scopes;
        private final AutoCompleteTextView password;

        public PasswordView(@NonNull View itemView) {
            super(itemView);
            tail = itemView.findViewById(R.id.tail);
            scopes = itemView.findViewById(R.id.scopes);
            password = itemView.findViewById(R.id.password);
            for (int button : new int[]{R.id.deleteBtn, R.id.actionBtn})
                itemView.findViewById(button).setOnClickListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deleteBtn -> {
                    Utils.showSnackbar(v, "1");
                }
                case R.id.actionBtn -> {
                    Utils.showSnackbar(v, "2");
                }
            }
        }

    }

}
