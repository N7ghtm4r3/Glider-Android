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
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.toImport.Password;
import com.tecknobit.glider.helpers.toImport.Password.Status;

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

/**
 * The {@link PasswordsAdapter} is the adapter for the passwords lists
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Adapter
 * @see Filterable
 **/
public class PasswordsAdapter extends Adapter<PasswordsAdapter.PasswordView> implements Filterable {

    /**
     * {@code isRecoveryMode} whether the list of the {@link #passwords} is in a
     * {@link Status#DELETED} status and can be recovered or is in an {@link Status#ACTIVE} status
     **/
    private static boolean isRecoveryMode;
    /**
     * {@code passwords} list of {@link Password} to manage
     **/
    private final ArrayList<Password> passwords;
    /**
     * {@code filteredPasswords} list of {@link Password} to manage
     * @apiNote this list is used as main list and is used in {@link #getFilter()} and
     * {@link #resetPasswordsList()} methods
     **/
    private ArrayList<Password> filteredPasswords;

    /**
     * Constructor to init {@link Password} object
     *
     * @param passwords: list of {@link Password} to manage
     * @param isRecoveryMode: whether the list of the {@link #passwords} is in a
     * {@link Status#DELETED} status and can be recovered or is in an {@link Status#ACTIVE} status
     * **/
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
    @SuppressLint("NotifyDataSetChanged")
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
                    holder.scopes.setText(holder.scopes.getAdapter().getItem(0).toString(),
                            false);
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

    /**
     * Method to clear the {@link PasswordView#scopesLayout} and set it in a initial {@code UI}
     * state to be reused
     *
     * @param holder: view holder where the {@link PasswordView#scopesLayout} has being used
     */
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
                ArrayList<Password> filteredPasswords = new ArrayList<>();
                for (Password password : passwords)
                    if (password.getTail().contains(constraint) || isInScopes(password, constraint))
                        filteredPasswords.add(password);
                filterResults.values = filteredPasswords;
                return filterResults;
            }

            private boolean isInScopes(Password password, CharSequence scope) {
                for (String lScope : password.getScopes())
                    if (lScope.contains(scope))
                        return true;
                return false;
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

    /**
     * Method to reset the {@link #filteredPasswords} and filled it with the {@link #passwords}
     * full list
     */
    @SuppressLint("NotifyDataSetChanged")
    public void resetPasswordsList() {
        filteredPasswords = new ArrayList<>(passwords);
        notifyDataSetChanged();
    }

    /**
     * Method to get {@link #passwords} instance <br>
     * Any params required
     *
     * @return {@link #passwords} instance as {@link Collection} of {@link Password}
     **/
    public Collection<Password> getCurrentPasswords() {
        return passwords;
    }

    /**
     * The {@link PasswordView} is the view adapter for the {@link PasswordsAdapter}
     *
     * @author Tecknobit - N7ghtm4r3
     * @see RecyclerView.ViewHolder
     * @see View.OnClickListener
     **/
    public static class PasswordView extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * {@code tail} view
         **/
        private final MaterialTextView tail;

        /**
         * {@code scopesLayout} view for the scopes layout
         **/
        private final TextInputLayout scopesLayout;

        /**
         * {@code scopeLayout} view for the scope layout
         **/
        private final TextInputLayout scopeLayout;

        /**
         * {@code scope} view that can be filled with a scope
         **/
        private final TextInputEditText scope;

        /**
         * {@code scopes} view for the scopes list
         **/
        private final AutoCompleteTextView scopes;

        /**
         * {@code password} view
         **/
        private final AutoCompleteTextView password;

        /**
         * {@code actionBtn} the button that can {@code "copy"} or {@code "recover"} a password
         **/
        private final MaterialButton actionBtn;

        /**
         * Constructor to init {@link PasswordView} object
         *
         * @param itemView: container view
         * **/
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


        /**
         * {@inheritDoc}
         * **/
        @Override
        @SuppressLint("NonConstantResourceId")
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deleteBtn -> {
                    if (!isRecoveryMode) {
                        // TODO: 20/12/2022 REQUEST THEN
                        Utils.showSnackbar(v, "DELETE");
                    } else {
                        // TODO: 20/12/2022 REQUEST THEN
                        Utils.showSnackbar(v, "PERMANENTLY DELETED");
                    }
                }
                case R.id.actionBtn -> {
                    if (!isRecoveryMode) {
                        // TODO: 20/12/2022 REQUEST THEN
                        Utils.showSnackbar(v, "COPIED");
                    } else {
                        // TODO: 20/12/2022 REQUEST THEN
                        Utils.showSnackbar(v, "RECOVERED");
                    }
                }
            }
        }

    }

}
