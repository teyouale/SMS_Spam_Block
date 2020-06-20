package com.teyouale.smsspamblock.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cursoradapter.widget.CursorAdapter;

import com.teyouale.smsspamblock.R;
import com.teyouale.smsspamblock.utils.DatabaseAccessHelper;
import com.teyouale.smsspamblock.utils.IdentifiersContainer;

public class ContactsCursorAdapter extends CursorAdapter {

    public static IdentifiersContainer identifiersContainer = new IdentifiersContainer(0);
    public IdentifiersContainer checkedItems = new IdentifiersContainer(0);
    private View.OnClickListener outerOnClickListener = null;
    private View.OnLongClickListener outerOnLongClickListener = null;
    private RowOnClickListener rowOnClickListener = new RowOnClickListener();
    private RowOnLongClickListener rowOnLongClickListener = new RowOnLongClickListener();

    public ContactsCursorAdapter(Context context) {
        super(context, null,0);
    }

    // For Accessing CheckedItem
    public static IdentifiersContainer getIdentifiersContainer() {
        return identifiersContainer;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) return;

        // get contact
        DatabaseAccessHelper.ContactSource contactSource = (DatabaseAccessHelper.ContactSource) cursor;
        DatabaseAccessHelper.Contact contact = contactSource.getContact();
        // get view holder from the row
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // update the view holder with the new contact
        viewHolder.setModel(context, contact);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if(cursor == null){
            return null;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_contacts,parent,false);

        ViewHolder viewHolder = new ViewHolder(context,view);
        view.setTag(viewHolder);

        // on click listeners for the row and checkbox (which is inside the row)
        view.setOnClickListener(rowOnClickListener);

        return view;

    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        // rebuild checked items container
        int size = (cursor != null ? cursor.getCount() : 0);
        checkedItems = new IdentifiersContainer(size);
        identifiersContainer = checkedItems;

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.outerOnClickListener = onClickListener;
    }


    // Row on click listener
    private class RowOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.toggle();
            if (outerOnClickListener != null) {
                outerOnClickListener.onClick(view);
            }
        }
    }

    // Row on long click listener
    private class RowOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            return (outerOnLongClickListener != null &&
                    outerOnLongClickListener.onLongClick(view));
        }
    }
}
