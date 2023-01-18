package com.tecknobit.glider.helpers.adapters;

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
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.apimanager.annotations.android.ResId;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.local.Utils;
import com.tecknobit.glider.helpers.toImport.records.Password;
import com.tecknobit.glider.helpers.toImport.records.Password.Status;

import java.util.ArrayList;
import java.util.Arrays;
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
import static com.tecknobit.glider.helpers.local.Utils.COLOR_PRIMARY;
import static com.tecknobit.glider.helpers.local.Utils.getTextFromEdit;
import static com.tecknobit.glider.helpers.local.Utils.hideKeyboard;
import static com.tecknobit.glider.helpers.local.Utils.showSnackbar;
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
    private ArrayList<Password> passwords;

    /**
     * {@code filteredPasswords} list of {@link Password} to manage
     *
     * @apiNote this list is used as main list and is used in {@link #getFilter()} and
     * {@link #resetPasswordsList()} methods
     **/
    private ArrayList<Password> filteredPasswords;

    /**
     * Constructor to init {@link PasswordsAdapter} object
     *
     * @param passwords:      list of {@link Password} to manage
     * @param isRecoveryMode: whether the list of the {@link #passwords} is in a
     *                        {@link Status#DELETED} status and can be recovered or is in an {@link Status#ACTIVE} status
     **/
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
                if (sPosition == parent.getLastVisiblePosition()) {
                    hintId = new_scope;
                    helperTextId = scope_hint;
                    errorId = new_scope_is_required;
                    successId = new_scope_inserted_successfully;
                    ope = "add_more"; // TODO: 20/12/2022 TO CHANGE
                    holder.scopeLayout.setVisibility(View.VISIBLE);
                } else {
                    hintId = the_scope_edited;
                    helperTextId = edit_the_current_scope;
                    errorId = scope_must_be_filled;
                    successId = current_scope_edited_successfully;
                    ope = "edit_current"; // TODO: 20/12/2022 TO CHANGE
                    holder.scopeActions.setVisibility(View.VISIBLE);
                    holder.scopeValue.setText(holder.scopes.getText());
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
                    hideKeyboard(v);
                    clearScopesLayout(holder);
                    holder.scopes.setText(holder.scopes.getAdapter().getItem(0).toString(),
                            false);
                });
                holder.scopeLayout.setStartIconOnClickListener(v -> {
                    hideKeyboard(v);
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
                String query = constraint.toString();
                if (!query.contains(",")) {
                    for (Password password : passwords)
                        if (password.getTail().contains(constraint) || isInScopes(password, constraint))
                            filteredPasswords.add(password);
                } else {
                    String[] mScopes = query.replaceAll(" ", "").split(",");
                    Arrays.sort(mScopes);
                    for (Password password : passwords)
                        if (Arrays.toString(mScopes).equals(password.getScopesSorted().toString()))
                            filteredPasswords.add(password);
                }
                filterResults.values = filteredPasswords;
                return filterResults;
            }

            /**
             * Method to check if the scope given is in the password's scopes
             *
             * @param password: password from check the existence of the scope
             * @param scope:    scope to compare
             * @return whether the scope given is in the list
             **/
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
     * Method to refresh the current {@link #passwords} list with a new list <br>
     *
     * @param passwords: new passwords list
     */
    @SuppressLint("NotifyDataSetChanged")
    public void refreshPasswordsList(ArrayList<Password> passwords) {
        this.passwords = passwords;
        resetPasswordsList();
    }

    /**
     * Method to reset the {@link #filteredPasswords} and filled it with the {@link #passwords}
     * full list <br>
     * Any params required
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
    @SuppressLint("NonConstantResourceId")
    public static class PasswordView extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * {@code tail} view
         **/
        @ResId(id = R.id.tail)
        private final MaterialTextView tail;

        /**
         * {@code scopesLayout} view for the scopes layout
         **/
        @ResId(id = R.id.scopesLayout)
        private final TextInputLayout scopesLayout;

        /**
         * {@code scopeLayout} view for the scope layout
         **/
        @ResId(id = R.id.scopeLayout)
        private final TextInputLayout scopeLayout;

        /**
         * {@code scope} view that can be filled with a scope
         **/
        @ResId(id = R.id.scope)
        private final TextInputEditText scope;

        /**
         * {@code scopes} view for the scopes list
         **/
        @ResId(id = R.id.scopes)
        private final AutoCompleteTextView scopes;

        /**
         * {@code scopeActions} {@link RelativeLayout} for the scope actions
         **/
        @ResId(id = R.id.relScopeActions)
        private final RelativeLayout scopeActions;

        /**
         * {@code scopeValue} view of the scope to make an action on
         **/
        @ResId(id = R.id.scopeText)
        private final MaterialTextView scopeValue;

        /**
         * {@code password} view
         **/
        @ResId(id = R.id.password)
        private final AutoCompleteTextView password;

        /**
         * {@code actionBtn} the button that can {@code "copy"} or {@code "recover"} a password
         **/
        @ResId(id = R.id.actionBtn)
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
            scopeActions = itemView.findViewById(R.id.relScopeActions);
            scopeValue = itemView.findViewById(R.id.scopeText);
            scopes = itemView.findViewById(R.id.scopes);
            password = itemView.findViewById(R.id.password);
            actionBtn = itemView.findViewById(R.id.actionBtn);
            actionBtn.setOnClickListener(this);
            for (int btn : new int[]{R.id.deleteBtn, R.id.editBtn, R.id.removeBtn,
                    R.id.closeScopeActions}) {
                itemView.findViewById(btn).setOnClickListener(this);
            }
        }

        /**
         * {@inheritDoc}
         * **/
        @Override
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
                case R.id.editBtn -> {
                    scopeActions.setVisibility(View.GONE);
                    scopeLayout.setVisibility(View.VISIBLE);
                }
                case R.id.removeBtn -> {
                    // TODO: 06/01/2023 REQUEST THEN
                    showSnackbar(v, MAIN_ACTIVITY.getString(R.string.scope_removed_successfully));
                    restoreScopesLayout();
                }
                case R.id.closeScopeActions -> restoreScopesLayout();
            }
        }

        /**
         * Method to re-set the {@link #scopesLayout} <br>
         * Any params required
         */
        private void restoreScopesLayout() {
            scopeActions.setVisibility(View.GONE);
            scopesLayout.setVisibility(View.VISIBLE);
        }

    }

}
