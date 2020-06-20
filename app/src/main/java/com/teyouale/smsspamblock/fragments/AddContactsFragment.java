package com.teyouale.smsspamblock.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.teyouale.smsspamblock.Adapter.ContactsCursorAdapter;
import com.teyouale.smsspamblock.ContactsAccessHelper;
import com.teyouale.smsspamblock.ContactsAccessHelper.ContactSourceType;
import com.teyouale.smsspamblock.R;
import com.teyouale.smsspamblock.utils.FragmentConstants;


public class AddContactsFragment extends Fragment implements FragmentConstants {
    public ContactSourceType sourceType = null;
    private ContactsCursorAdapter cursorAdapter = null;
    private int contactType = 0;

    public AddContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            contactType = arguments.getInt(CONTACT_TYPE);
            sourceType = (ContactSourceType) arguments.getSerializable(SOURCE_TYPE);
           // singleNumberMode = arguments.getBoolean(SINGLE_NUMBER_MODE);
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_contacts, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cursorAdapter = new ContactsCursorAdapter(getContext());

        cursorAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               }

        });
        // add cursor listener to the list
        ListView listView = view.findViewById(R.id.contacts_list);
        listView.setAdapter(cursorAdapter);

        // on list empty comment
        TextView textEmptyView = view.findViewById(R.id.text_empty);
        listView.setEmptyView(textEmptyView);

        // init and run the loader of contacts
         getLoaderManager().initLoader(0, null, newLoaderCallbacks(null));

    }

    // Creates new contacts loader
    private ContactsLoaderCallbacks newLoaderCallbacks(String itemsFilter) {
        return new ContactsLoaderCallbacks(getContext(), sourceType, cursorAdapter, itemsFilter);
    }




    // Contact items loader
    private static class ContactsLoader extends CursorLoader {
        private ContactSourceType sourceType;
        private String itemsFilter;

        ContactsLoader(Context context,
                       ContactSourceType sourceType,
                       String itemsFilter) {
            super(context);
            this.sourceType = sourceType;
            this.itemsFilter = itemsFilter;
        }

        @Override
        public Cursor loadInBackground() {
            ContactsAccessHelper dao = ContactsAccessHelper.getInstance(getContext());
            return dao.getContacts(getContext(), sourceType, itemsFilter);
        }
    }

    // Contact items loader callbacks
    private static class ContactsLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        //ProgressDialogHolder progress = new ProgressDialogHolder();
        private Context context;
        private ContactSourceType sourceType;
        private ContactsCursorAdapter cursorAdapter;
        private String itemsFilter;

        ContactsLoaderCallbacks(Context context,
                                ContactSourceType sourceType,
                                ContactsCursorAdapter cursorAdapter,
                                String itemsFilter) {
            this.context = context;
            this.sourceType = sourceType;
            this.cursorAdapter = cursorAdapter;
            this.itemsFilter = itemsFilter;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
           // progress.show(context, 0, R.string.Loading_);
            return new ContactsLoader(context, sourceType, itemsFilter);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            cursorAdapter.changeCursor(data);

            //progress.dismiss();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            cursorAdapter.changeCursor(null);
           // progress.dismiss();
        }
    }

}
