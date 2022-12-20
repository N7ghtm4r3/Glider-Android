package com.tecknobit.glider.helpers;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.toImport.Password;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.view.LayoutInflater.from;
import static com.tecknobit.glider.R.string.add_more;
import static com.tecknobit.glider.R.string.current_scope_edited_successfully;
import static com.tecknobit.glider.R.string.edit_the_current_scope;
import static com.tecknobit.glider.R.string.new_scope;
import static com.tecknobit.glider.R.string.new_scope_inserted_successfully;
import static com.tecknobit.glider.R.string.new_scope_is_required;
import static com.tecknobit.glider.R.string.no_scopes_for_this_password;
import static com.tecknobit.glider.R.string.recover;
import static com.tecknobit.glider.R.string.scope_hint;
import static com.tecknobit.glider.R.string.scope_must_be_filled;
import static com.tecknobit.glider.R.string.the_scope_edited;
import static com.tecknobit.glider.helpers.Utils.COLOR_PRIMARY;
import static com.tecknobit.glider.helpers.Utils.getTextFromEdit;
import static com.tecknobit.glider.helpers.Utils.hideKeyBoard;
import static com.tecknobit.glider.helpers.Utils.showSnackbar;
import static com.tecknobit.glider.ui.activities.MainActivity.MAIN_ACTIVITY;

public class PasswordsAdapter extends RecyclerView.Adapter<PasswordsAdapter.PasswordView> implements Filterable {

    private static boolean isRecoveryMode;
    private final ArrayList<Password> passwords;
    private final ArrayList<Password> filteredPasswords;

    public PasswordsAdapter(ArrayList<Password> passwords, boolean isRecoveryMode) {
        this.passwords = passwords;
        filteredPasswords = new ArrayList<>(passwords);
        PasswordsAdapter.isRecoveryMode = isRecoveryMode;
    }

    /**
     * {@inheritDoc}
     **/
    @NonNull
    @Override
    public PasswordsAdapter.PasswordView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PasswordView(from(MAIN_ACTIVITY).inflate(R.layout.password_item, parent, false));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void onBindViewHolder(@NonNull PasswordsAdapter.PasswordView holder, int position) {
        Password password = filteredPasswords.get(position);
        holder.tail.setText(password.getTail());
        ArrayList<String> scopes = new ArrayList<>(password.getScopes());
        List<String> listItems;
        if (scopes.size() > 0) {
            if (!isRecoveryMode)
                scopes.add(MAIN_ACTIVITY.getString(add_more));
            listItems = List.of(scopes.toArray(new String[0]));
        } else {
            if (!isRecoveryMode)
                listItems = List.of(MAIN_ACTIVITY.getString(add_more));
            else
                listItems = List.of(MAIN_ACTIVITY.getString(no_scopes_for_this_password));
        }
        holder.scopes.setAdapter(new ArrayAdapter<>(MAIN_ACTIVITY, android.R.layout.simple_list_item_1,
                listItems));
        if (!isRecoveryMode) {
            holder.scopes.setOnItemClickListener((parent, view, sPosition, id) -> {
                int hintId, helperTextId, errorId, successId;
                String ope;
                holder.scopesLayout.setVisibility(View.GONE);
                holder.scopeLayout.setVisibility(View.VISIBLE);
                if (sPosition == parent.getLastVisiblePosition()) {
                    hintId = new_scope;
                    helperTextId = scope_hint;
                    errorId = new_scope_is_required;
                    successId = new_scope_inserted_successfully;
                    ope = "add_more"; // TODO: 20/12/2022 TO CHANGE
                } else {
                    hintId = the_scope_edited;
                    helperTextId = edit_the_current_scope;
                    errorId = scope_must_be_filled;
                    successId = current_scope_edited_successfully;
                    ope = "edit_current"; // TODO: 20/12/2022 TO CHANGE
                }
                holder.scope.setHint(hintId);
                holder.scope.setHintTextColor(COLOR_PRIMARY);
                holder.scopeLayout.setHelperText(MAIN_ACTIVITY.getString(helperTextId));
                holder.scope.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before > 0 && start + count == 0) {
                            holder.scope.setHintTextColor(Color.TRANSPARENT);
                            holder.scopeLayout.setError(MAIN_ACTIVITY.getString(errorId));
                            holder.scopeLayout.setStartIconVisible(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0) {
                            holder.scopeLayout.setError(null);
                            holder.scopeLayout.setStartIconVisible(true);
                        }
                    }
                });
                holder.scopeLayout.setEndIconOnClickListener(v -> {
                    hideKeyBoard(v);
                    clearScopesLayout(holder);
                    // TODO: 20/12/2022 set selection on first item
                });
                holder.scopeLayout.setStartIconOnClickListener(v -> {
                    hideKeyBoard(v);
                    String newScope = getTextFromEdit(holder.scope);
                    if (!newScope.isEmpty()) {
                        // TODO: 20/12/2022 REQUEST THEN
                        showSnackbar(v, successId);
                        clearScopesLayout(holder);
                    } else
                        showSnackbar(v, errorId);
                });
            });
        } else
            holder.actionBtn.setText(recover);
        holder.password.setText(password.getPassword());
    }

    private void clearScopesLayout(PasswordsAdapter.PasswordView holder) {
        holder.scope.setText("");
        holder.scopeLayout.setError(null);
        holder.scopeLayout.setStartIconVisible(true);
        holder.scopesLayout.setVisibility(View.VISIBLE);
        holder.scopeLayout.setVisibility(View.GONE);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public int getItemCount() {
        return filteredPasswords.size();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Password> passwords = new ArrayList<>();
                for (Password password : filteredPasswords) {
                    if (password.getTail().contains(constraint) ||
                            password.getScopes().contains((String) constraint)) {
                        passwords.add(password);
                    }
                }
                filterResults.values = passwords;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPasswords.clear();
                filteredPasswords.addAll((Collection<? extends Password>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class PasswordView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final MaterialTextView tail;
        private final TextInputLayout scopesLayout;
        private final TextInputLayout scopeLayout;
        private final TextInputEditText scope;
        private final AutoCompleteTextView scopes;
        private final AutoCompleteTextView password;
        private final MaterialButton actionBtn;

        public PasswordView(@NonNull View itemView) {
            super(itemView);
            tail = itemView.findViewById(R.id.tail);
            scopesLayout = itemView.findViewById(R.id.scopesLayout);
            scopeLayout = itemView.findViewById(R.id.scopeLayout);
            scope = itemView.findViewById(R.id.scope);
            scopes = itemView.findViewById(R.id.scopes);
            password = itemView.findViewById(R.id.password);
            actionBtn = itemView.findViewById(R.id.actionBtn);
            actionBtn.setOnClickListener(this);
            itemView.findViewById(R.id.deleteBtn).setOnClickListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deleteBtn -> {
                    if (!isRecoveryMode) {
                        // TODO: 20/12/2022 REQUEST THEN
                        Utils.showSnackbar(v, "1");
                    } else {
                        // TODO: 20/12/2022 REQUEST THEN
                        Utils.showSnackbar(v, "2");
                    }
                }
                case R.id.actionBtn -> {
                    if (!isRecoveryMode) {
                        // TODO: 20/12/2022 REQUEST THEN
                        Utils.showSnackbar(v, "3");
                    } else {
                        // TODO: 20/12/2022 REQUEST THEN
                        Utils.showSnackbar(v, "4");
                    }
                }
            }
        }

    }

}
