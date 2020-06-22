package com.teyouale.smsspamblock.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.teyouale.smsspamblock.R;
import com.teyouale.smsspamblock.utils.CheckableLinearLayout;
import com.teyouale.smsspamblock.utils.DatabaseAccessHelper;
import com.teyouale.smsspamblock.utils.DatabaseAccessHelper.Contact;
import com.teyouale.smsspamblock.utils.IdentifiersContainer;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ViewHolder {

    public StringBuilder sb = new StringBuilder();
    public Contact contact;
    public IdentifiersContainer identifiersContainer;
    public int itemId;
    public CheckableLinearLayout rowView;
    public TextView nameTextView;
    public TextView numbersTextView;
    public CheckBox checkBox;

    ViewHolder(Context context, View row) {
        this(context, (CheckableLinearLayout) row,
                (TextView) row.findViewById(R.id.contact_name),
                (TextView) row.findViewById(R.id.contact_numbers),
                (CheckBox) row.findViewById(R.id.contact_cb));
    }

    ViewHolder(Context context, CheckableLinearLayout rowView, TextView nameTextView,
               TextView numbersTextView, CheckBox checkBox) {
        this.contact = null;
        this.itemId = 0;
        this.rowView = rowView;
        this.nameTextView = nameTextView;
        this.numbersTextView = numbersTextView;
        this.checkBox = checkBox;
        identifiersContainer =  ContactsCursorAdapter.getIdentifiersContainer();
    }

    void setModel(Context context, DatabaseAccessHelper.Contact contact) {
        this.contact = contact;

        itemId = (int) contact.id;
        boolean oneNumberEquals = false;

        // show contact name
        String name = contact.name;
        if (contact.numbers.size() == 1) {
            DatabaseAccessHelper.ContactNumber number = contact.numbers.get(0);
            if (name.equals(number.number)) {
                // there is just 1 number and it equals to the contact name
                // add number type title before the contact name
                name = getNumberTypeTitle(context, number.type) + name;
                oneNumberEquals = true;
            }
        }
        nameTextView.setText(name);

        // show contact numbers
        sb.setLength(0);
        if (!oneNumberEquals) {
            String[] titles = getSortedNumberTitles(context, contact.numbers);
            String separator = (titles.length > 5 ? ", " : "\n");
            for (int i = 0; i < titles.length; i++) {
                sb.append(titles[i]);
                if (i < titles.length - 1) {
                    sb.append(separator);
                }
            }
        }
        numbersTextView.setText(sb.toString());
        if (numbersTextView.getText().length() == 0) {
            numbersTextView.setVisibility(View.GONE);
        } else {
            numbersTextView.setVisibility(View.VISIBLE);
        }

        // set selection
        boolean checked = isChecked();
        checkBox.setChecked(checked);
        rowView.setChecked(checked);
    }


    void toggle() {
        setChecked(!isChecked());
        Log.d("TAG", "toggle: " + identifiersContainer.getSize());
    }

    boolean isChecked() {
        return identifiersContainer.contains(itemId);
    }

    private void setChecked(boolean checked) {
        identifiersContainer.set(itemId, checked);
        checkBox.setChecked(checked);
        rowView.setChecked(checked);
    }
    private String[] getSortedNumberTitles(Context context, List<DatabaseAccessHelper.ContactNumber> numbers) {
        Set<String> titles = new TreeSet<>();
        for (DatabaseAccessHelper.ContactNumber number : numbers) {
            titles.add(getNumberTypeTitle(context, number.type) + number.number);
        }
        return titles.toArray(new String[titles.size()]);
    }

    private String getNumberTypeTitle(Context context, int type) {
        switch (type) {
            case DatabaseAccessHelper.ContactNumber.TYPE_STARTS:
                return "Starts_with" + " ";
            case DatabaseAccessHelper.ContactNumber.TYPE_ENDS:
                return "Ends_with" + " ";
            case DatabaseAccessHelper.ContactNumber.TYPE_CONTAINS:
                return "Contains" + " ";
        }
        return "";
    }


}