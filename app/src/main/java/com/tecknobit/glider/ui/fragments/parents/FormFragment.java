package com.tecknobit.glider.ui.fragments.parents;

import android.content.res.ColorStateList;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.local.Utils;

import java.util.HashMap;

import static com.tecknobit.glider.helpers.local.Utils.COLOR_PRIMARY;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_RED;
import static com.tecknobit.glider.helpers.local.Utils.instantiateViews;

/**
 * The {@link FormFragment} is the super class where a fragment inherit the base methods to get data from a
 * form {@link GliderFragment}
 *
 * @author Tecknobit - N7ghtm4r3
 * @see GliderFragment
 */
@Structure
public abstract class FormFragment extends GliderFragment {

    /**
     * {@code inputsErrors} list of the error messages to show when an error occurred during the insertion
     * of the parameters for the password creation
     */
    protected static final HashMap<Integer, String> inputsErrors = new HashMap<>();

    /**
     * {@code inputsHints} list of the hint messages to show when the user trigger the
     * {@link TextInputLayout#setStartIconOnClickListener(View.OnClickListener)}
     */
    protected static final HashMap<Integer, String> inputsHints = new HashMap<>();

    /**
     * {@code textInputLayouts} list of the layout for the {@link #textInputEditTexts}
     */
    protected TextInputLayout[] textInputLayouts;

    /**
     * {@code textInputEditTexts} list of the text input edit texts from fetch the password's parameters
     * for it creation
     */
    protected TextInputEditText[] textInputEditTexts;

    /**
     * Method to instantiate the {@link #textInputLayouts} and {@link #textInputEditTexts} instances
     *
     * @param container:  the container view where the instates are contained
     * @param layoutsIds: the ids of the {@link #textInputLayouts}
     * @param editsIds:   the ids of the {@link #textInputEditTexts}
     */
    protected void instantiateInputs(View container, int[] layoutsIds, int[] editsIds) {
        instantiateViews(container, textInputLayouts, layoutsIds);
        instantiateViews(container, textInputEditTexts, editsIds);
    }

    /**
     * Method to load the {@link #inputsErrors} and the {@link #inputsHints} lists
     * with the right messages <br>
     * No-any params required
     */
    protected abstract void loadInputMessagesLists();

    /**
     * Method to set the:
     * <ul>
     *     <li>
     *          {@link TextInputEditText#addTextChangedListener(TextWatcher)} for the {@link #textInputEditTexts}
     *          to check if the required field are filled or not and get error from {@link #inputsErrors}
     *     </li>
     *     <li>
     *          {@link TextInputLayout#setStartIconOnClickListener(View.OnClickListener)} for the {@link #textInputLayouts}
     *          to show the hint or not from {@link #inputsHints}
     *     </li>
     * </ul>
     * @apiNote will be automatically invoked the {@link #loadInputMessagesLists()} method
     * No-any params required
     */
    protected void startInputsListenWorkflow() {
        loadInputMessagesLists();
    }

    /**
     * Method to set the text helper layout of {@link TextInputLayout}
     *
     * @param layout: text input layout where set helper text layout
     * @param text:   text to set for the helper text
     */
    protected void setHelperTextLayout(TextInputLayout layout, String text) {
        ColorStateList colorStateList = ColorStateList.valueOf(COLOR_RED);
        if (!text.equals(getString(R.string.required)))
            colorStateList = ColorStateList.valueOf(COLOR_PRIMARY);
        layout.setHelperText(text);
        layout.setHelperTextColor(colorStateList);
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote this method is called when:
     * <ul>
     *     <li>this fragment is not more the first showing</li>
     *     <li>an error is occurred during the password creation</li>
     *     <li>the normal conclusion of the password creation's routine</li>
     * </ul>
     */
    @Override
    protected void clearViews() {
        for (int j = 0; j < textInputEditTexts.length; j++) {
            textInputEditTexts[j].setText("");
            textInputLayouts[j].setError(null);
        }
        enableEditTexts(true);
    }

    /**
     * Method to enable or not the {@link #textInputEditTexts}
     *
     * @param enable: whether enable the {@link #textInputEditTexts}:
     *                <ul>
     *                    <li>{@code "true"} -> the {@link #textInputEditTexts} can be filled</li>
     *                    <li>{@code "false"} -> the {@link #textInputEditTexts} cannot be filled</li>
     *                </ul>
     */
    protected void enableEditTexts(boolean enable) {
        for (TextInputEditText editText : textInputEditTexts)
            editText.setEnabled(enable);
    }

    /**
     * Method to show an error with the {@link Utils#showSnackbar(View, int)} method
     *
     * @param index: index of the error tho show
     */
    protected abstract void showsError(int index);

}
