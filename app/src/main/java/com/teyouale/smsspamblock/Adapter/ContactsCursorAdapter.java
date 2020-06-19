package com.teyouale.smsspamblock.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cursoradapter.widget.CursorAdapter;

import com.teyouale.smsspamblock.R;
import com.teyouale.smsspamblock.utils.DatabaseAccessHelper;

public class ContactsCursorAdapter extends CursorAdapter {
    public ContactsCursorAdapter(Context context) {
        super(context, null,0);
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
        return view;

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
}
