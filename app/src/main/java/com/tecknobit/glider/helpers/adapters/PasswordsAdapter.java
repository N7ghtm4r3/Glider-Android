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
import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.apimanager.annotations.android.ResId;
import com.tecknobit.apimanager.apis.SocketManager;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.GliderLauncher.Operation;
import com.tecknobit.glider.helpers.local.ManageRequest;
import com.tecknobit.glider.records.Device.DevicePermission;
import com.tecknobit.glider.records.Password;
import com.tecknobit.glider.records.Password.PasswordKeys;
import com.tecknobit.glider.records.Password.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static android.view.LayoutInflater.from;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tecknobit.glider.R.string.add_more;
import static com.tecknobit.glider.R.string.current_scope_edited_successfully;
import static com.tecknobit.glider.R.string.edit_the_current_scope;
import static com.tecknobit.glider.R.string.new_scope;
import static com.tecknobit.glider.R.string.new_scope_inserted_successfully;
import static com.tecknobit.glider.R.string.new_scope_is_required;
import static com.tecknobit.glider.R.string.no_scopes_for_this_password;
import static com.tecknobit.glider.R.string.ope_failed;
import static com.tecknobit.glider.R.string.password_successfully_deleted;
import static com.tecknobit.glider.R.string.password_successfully_recovered;
import static com.tecknobit.glider.R.string.recover;
import static com.tecknobit.glider.R.string.scope_hint;
import static com.tecknobit.glider.R.string.scope_must_be_filled;
import static com.tecknobit.glider.R.string.scope_removed_successfully;
import static com.tecknobit.glider.R.string.the_scope_edited;
import static com.tecknobit.glider.R.string.you_are_not_authorized;
import static com.tecknobit.glider.helpers.GliderLauncher.GliderKeys.ope;
import static com.tecknobit.glider.helpers.GliderLauncher.GliderKeys.statusCode;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.ADD_SCOPE;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.DELETE_PASSWORD;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.EDIT_SCOPE;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.RECOVER_PASSWORD;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.REMOVE_SCOPE;
import static com.tecknobit.glider.helpers.local.User.DEVICE_NAME;
import static com.tecknobit.glider.helpers.local.User.socketManager;
import static com.tecknobit.glider.helpers.local.User.user;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_PRIMARY;
import static com.tecknobit.glider.helpers.local.Utils.copyText;
import static com.tecknobit.glider.helpers.local.Utils.getTextFromEdit;
import static com.tecknobit.glider.helpers.local.Utils.hideKeyboard;
import static com.tecknobit.glider.helpers.local.Utils.showSnackbar;
import static com.tecknobit.glider.records.Device.DeviceKeys.name;
import static com.tecknobit.glider.records.Device.DeviceKeys.type;
import static com.tecknobit.glider.records.Device.Type.MOBILE;
import static com.tecknobit.glider.records.Session.SessionKeys.sessionPassword;
import static com.tecknobit.glider.ui.activities.MainActivity.MAIN_ACTIVITY;

/**
 * The {@link PasswordsAdapter} is the adapter for the passwords lists
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Adapter
 * @see Filterable
 */
public class PasswordsAdapter extends RecyclerView.Adapter<PasswordsAdapter.PasswordView> implements Filterable {

    /**
     * {@code isRecoveryMode} whether the list of the {@link #passwords} is in a
     * {@link Status#DELETED} status and can be recovered or is in an {@link Status#ACTIVE} status
     */
    private static boolean isRecoveryMode;

    /**
     * {@code passwords} list of {@link Password} to manage
     */
    private ArrayList<Password> passwords;

    /**
     * {@code filteredPasswords} list of {@link Password} to manage
     *
     * @apiNote this list is used as main list and is used in {@link #getFilter()} and
     * {@link #resetPasswordsList()} methods
     */
    private ArrayList<Password> filteredPasswords;

    /**
     * Constructor to init {@link PasswordsAdapter} object
     *
     * @param passwords:      list of {@link Password} to manage
     * @param isRecoveryMode: whether the list of the {@link #passwords} is in a
     *                        {@link Status#DELETED} status and can be recovered or is in an {@link Status#ACTIVE} status
     */
    public PasswordsAdapter(ArrayList<Password> passwords, boolean isRecoveryMode) {
        this.passwords = new ArrayList<>(passwords);
        filteredPasswords = new ArrayList<>(passwords);
        PasswordsAdapter.isRecoveryMode = isRecoveryMode;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public PasswordsAdapter.PasswordView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PasswordView(from(MAIN_ACTIVITY).inflate(R.layout.password_item, parent, false));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull PasswordsAdapter.PasswordView holder, int position) {
        Password password = filteredPasswords.get(position);
        holder.tail.setText(password.getTail());
        ArrayList<String> scopes = new ArrayList<>(password.getScopes());
        List<String> listItems;
        if (scopes.size() > 0) {
            if (!isRecoveryMode && user.isPasswordManager())
                scopes.add(MAIN_ACTIVITY.getString(add_more));
            listItems = List.of(scopes.toArray(new String[0]));
        } else {
            if (!isRecoveryMode) {
                if (user.isPasswordManager())
                    listItems = List.of(MAIN_ACTIVITY.getString(add_more));
                else
                    listItems = List.of(MAIN_ACTIVITY.getString(you_are_not_authorized));
            } else
                listItems = List.of(MAIN_ACTIVITY.getString(no_scopes_for_this_password));
        }
        holder.scopes.setAdapter(new ArrayAdapter<>(MAIN_ACTIVITY, android.R.layout.simple_list_item_1,
                listItems));
        if (!isRecoveryMode) {
            holder.scopes.setOnItemClickListener((parent, view, sPosition, id) -> {
                if (user.isPasswordManager()) {
                    int hintId, helperTextId, errorId;
                    Operation ope;
                    holder.scopesLayout.setVisibility(GONE);
                    if (sPosition == parent.getLastVisiblePosition()) {
                        hintId = new_scope;
                        helperTextId = scope_hint;
                        errorId = new_scope_is_required;
                        ope = ADD_SCOPE;
                        holder.scopeLayout.setVisibility(VISIBLE);
                    } else {
                        hintId = the_scope_edited;
                        helperTextId = edit_the_current_scope;
                        errorId = scope_must_be_filled;
                        ope = EDIT_SCOPE;
                        holder.scopeActions.setVisibility(VISIBLE);
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
                        String oldScope = null;
                        if (ope.equals(EDIT_SCOPE))
                            oldScope = holder.scopeValue.getText().toString();
                        if (!newScope.isEmpty()) {
                            holder.executeOperation(ope, holder.tail, newScope, oldScope);
                            clearScopesLayout(holder);
                        } else
                            showSnackbar(v, errorId);
                    });
                }
            });
        } else
            holder.actionBtn.setText(recover);
        holder.password.setText(password.getPassword());
        if (isRecoveryMode) {
            if (user.isPasswordManager())
                holder.relActionButtons.setVisibility(VISIBLE);
            else
                holder.relActionButtons.setVisibility(GONE);
        } else {
            if (user.isPasswordManager()) {
                holder.relActionButtons.setVisibility(VISIBLE);
                holder.copyBtn.setVisibility(GONE);
            } else {
                holder.relActionButtons.setVisibility(GONE);
                holder.copyBtn.setVisibility(VISIBLE);
            }
        }
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
        holder.scopesLayout.setVisibility(VISIBLE);
        holder.scopeLayout.setVisibility(GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return filteredPasswords.size();
    }

    /**
     * {@inheritDoc}
     */
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
                        if (Arrays.toString(mScopes).contains(password.getScopesSorted().toString()))
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
             */
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
        this.passwords = new ArrayList<>(passwords);
        resetPasswordsList();
    }

    /**
     * Method to reset the {@link #filteredPasswords} and filled it with the {@link #passwords}
     * full list <br>
     * No-any params required
     */
    @SuppressLint("NotifyDataSetChanged")
    public void resetPasswordsList() {
        filteredPasswords = new ArrayList<>(passwords);
        notifyDataSetChanged();
    }

    /**
     * Method to get {@link #passwords} instance <br>
     * No-any params required
     *
     * @return {@link #passwords} instance as {@link Collection} of {@link Password}
     */
    public Collection<Password> getCurrentPasswords() {
        return passwords;
    }

    /**
     * Method to check whether the current {@link #passwords} list match with an other list
     *
     * @param checkList: list of {@link Password} to check
     * @return {@code "true"} if the list match, {@code "false"} if they don't match
     */
    public boolean passwordsMatch(ArrayList<Password> checkList) {
        return checkList.hashCode() == passwords.hashCode();
    }

    /**
     * The {@link PasswordView} is the view adapter for the {@link PasswordsAdapter}
     *
     * @author Tecknobit - N7ghtm4r3
     * @see RecyclerView.ViewHolder
     * @see View.OnClickListener
     */
    @SuppressLint("NonConstantResourceId")
    public static class PasswordView extends RecyclerView.ViewHolder implements View.OnClickListener,
            ManageRequest {

        /**
         * {@code payload} the payload to send with the request
         */
        protected JSONObject payload;

        /**
         * {@code response} instance to contains the response from the backend
         */
        protected JSONObject response;

        /**
         * {@code actionBtn} the button that can {@code "copyPassword"} or {@code "recover"} a password
         */
        @ResId(id = R.id.actionBtn)
        private final MaterialButton actionBtn;

        /**
         * {@code tail} view
         */
        @ResId(id = R.id.tail)
        private final MaterialTextView tail;

        /**
         * {@code scopesLayout} view for the scopes layout
         */
        @ResId(id = R.id.scopesLayout)
        private final TextInputLayout scopesLayout;

        /**
         * {@code scopeLayout} view for the scope layout
         */
        @ResId(id = R.id.scopeLayout)
        private final TextInputLayout scopeLayout;

        /**
         * {@code scope} view that can be filled with a scope
         */
        @ResId(id = R.id.scope)
        private final TextInputEditText scope;

        /**
         * {@code scopes} view for the scopes list
         */
        @ResId(id = R.id.scopes)
        private final AutoCompleteTextView scopes;

        /**
         * {@code scopeActions} {@link RelativeLayout} for the scope actions
         */
        @ResId(id = R.id.relScopeActions)
        private final RelativeLayout scopeActions;

        /**
         * {@code scopeValue} view of the scope to make an action on
         */
        @ResId(id = R.id.scopeText)
        private final MaterialTextView scopeValue;

        /**
         * {@code password} view
         */
        @ResId(id = R.id.password)
        private final AutoCompleteTextView password;

        /**
         * {@code relActionButtons} container of the action buttons
         */
        @ResId(id = R.id.relActionButtons)
        private final RelativeLayout relActionButtons;

        /**
         * {@code copyBtn} button to copy the password when the user does not have the
         * {@link DevicePermission#PASSWORD_MANAGER}
         */
        @ResId(id = R.id.copyBtn)
        private final MaterialButton copyBtn;

        /**
         * Constructor to init {@link PasswordView} object
         *
         * @param itemView: container view
         */
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
            for (int btn : new int[]{R.id.deleteBtn, R.id.editBtn, R.id.removeBtn, R.id.closeScopeActions})
                itemView.findViewById(btn).setOnClickListener(this);
            relActionButtons = itemView.findViewById(R.id.relActionButtons);
            copyBtn = itemView.findViewById(R.id.copyBtn);
            copyBtn.setOnClickListener(v -> copyPassword());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deleteBtn -> executeOperation(DELETE_PASSWORD);
                case R.id.actionBtn -> {
                    if (!isRecoveryMode)
                        copyPassword();
                    else
                        executeOperation(RECOVER_PASSWORD);
                }
                case R.id.editBtn -> {
                    scopeActions.setVisibility(GONE);
                    scopeLayout.setVisibility(VISIBLE);
                }
                case R.id.removeBtn -> {
                    executeOperation(REMOVE_SCOPE, tail, scopeValue.getText().toString());
                    restoreScopesLayout();
                }
                case R.id.closeScopeActions -> restoreScopesLayout();
            }
        }

        /**
         * Method to re-set the {@link #scopesLayout} <br>
         * No-any params required
         */
        private void restoreScopesLayout() {
            scopeActions.setVisibility(GONE);
            scopesLayout.setVisibility(VISIBLE);
            ((ArrayAdapter) scopes.getAdapter()).notifyDataSetChanged();
        }

        /**
         * {@inheritDoc}
         *
         * @apiNote different payload configurations
         * <ul>
         *     <li>
         *         {@link Operation#DELETE_PASSWORD}, {@link Operation#RECOVER_PASSWORD} ->  will be
         *         passed the {@code "tail"} {@link View} to get the parameter of the {@link Password}
         *         to delete or recover
         *     </li>
         *     <li>
         *         {@link Operation#ADD_SCOPE}, {@link Operation#REMOVE_SCOPE} ->  will be
         *         passed the {@code "tail"} {@link View} to get the parameter of the {@link Password}
         *         to where manage the scope in index {@code "0"} and the scope to add or remove in index
         *         {@code "1"}
         *     </li>
         *     <li>
         *         {@link Operation#EDIT_SCOPE} ->  will be
         *         passed the {@code "tail"} {@link View} to get the parameter of the {@link Password}
         *         to where manage the scope in index {@code "0"}, the scope edited in index
         *         {@code "1"} and the old scope to overwrite in index {@code "2"}
         *     </li>
         * </ul>
         */
        @Override
        public <T> void setRequestPayload(Operation operation, T... parameters) {
            try {
                if (payload == null)
                    setBasePayload();
                payload.put(ope.name(), operation);
                switch (operation) {
                    case DELETE_PASSWORD, RECOVER_PASSWORD -> {
                        payload.put(PasswordKeys.tail.name(),
                                ((MaterialTextView) parameters[0]).getText());
                    }
                    case ADD_SCOPE, REMOVE_SCOPE -> {
                        payload.put(PasswordKeys.tail.name(), ((MaterialTextView) parameters[0]).getText())
                                .put(PasswordKeys.scope.name(), parameters[1]);
                    }
                    case EDIT_SCOPE -> {
                        payload.put(PasswordKeys.tail.name(), ((MaterialTextView) parameters[0]).getText())
                                .put(PasswordKeys.scope.name(), parameters[1])
                                .put(PasswordKeys.oldScope.name(), parameters[2]);
                    }
                }
            } catch (JSONException e) {
                payload = null;
            }
        }

        /**
         * Method to set the base payload for a request <br>
         * No-any params required
         */
        @Override
        public void setBasePayload() {
            try {
                payload = new JSONObject();
                payload.put(name.name(), DEVICE_NAME)
                        .put(type.name(), MOBILE)
                        .put(sessionPassword.name(), user.getSessionPassword());
            } catch (JSONException e) {
                payload = null;
            }
        }

        /**
         * Method to manage the {@link Password}
         *
         * @param operation: operation to execute
         */
        @Wrapper
        public void executeOperation(Operation operation) {
            executeOperation(operation, tail);
        }

        /**
         * Method to manage the {@link Password}
         *
         * @param operation: operation to execute
         * @param parameters : parameters to insert to invoke this method
         */
        public <T> void executeOperation(Operation operation, T... parameters) {
            setRequestPayload(operation, parameters);
            if (payload != null) {
                executor.execute(() -> {
                    try {
                        socketManager.writeContent(payload);
                        response = new JSONObject(socketManager.readContent());
                        MAIN_ACTIVITY.runOnUiThread(() -> {
                            try {
                                int message;
                                switch (operation) {
                                    case RECOVER_PASSWORD -> message = password_successfully_recovered;
                                    case ADD_SCOPE -> message = new_scope_inserted_successfully;
                                    case EDIT_SCOPE -> message = current_scope_edited_successfully;
                                    case REMOVE_SCOPE -> message = scope_removed_successfully;
                                    default -> message = password_successfully_deleted;
                                }
                                if (operation.equals(DELETE_PASSWORD))
                                    message = password_successfully_deleted;
                                if (response.getString(statusCode.name())
                                        .equals(SocketManager.StandardResponseCode.SUCCESSFUL.name())) {
                                    showSnackbar(itemView, message);
                                } else
                                    showSnackbar(itemView, ope_failed);
                            } catch (JSONException e) {
                                showSnackbar(itemView, ope_failed);
                            }
                        });
                    } catch (Exception e) {
                        showSnackbar(itemView, ope_failed);
                    }
                });
            } else
                showSnackbar(itemView, ope_failed);
        }

        /**
         * Method to copy the password value from {@link #password} <br>
         * No-any params required
         */
        public void copyPassword() {
            copyText(password, password);
        }

    }

}
