package com.teyouale.smsspamblock.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.teyouale.smsspamblock.Adapter.ContactsCursorAdapter;
import com.teyouale.smsspamblock.ContactsAccessHelper;
import com.teyouale.smsspamblock.ContactsAccessHelper.ContactSourceType;
import com.teyouale.smsspamblock.R;
import com.teyouale.smsspamblock.utils.ButtonsBar;
import com.teyouale.smsspamblock.utils.DatabaseAccessHelper;
import com.teyouale.smsspamblock.utils.DatabaseAccessHelper.Contact;
import com.teyouale.smsspamblock.utils.DatabaseAccessHelper.ContactNumber;
import com.teyouale.smsspamblock.utils.DialogBuilder;
import com.teyouale.smsspamblock.utils.FragmentConstants;
import com.teyouale.smsspamblock.utils.Permissions;
import com.teyouale.smsspamblock.utils.ProgressDialogHolder;
import com.teyouale.smsspamblock.utils.Utils;

import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class AddContactsFragment extends Fragment implements FragmentConstants {
    public ContactSourceType sourceType = null;
    private ContactsCursorAdapter cursorAdapter = null;
    private int contactType = 0;
    private final String TAG = AddContactsFragment.class.getSimpleName();
    private ButtonsBar snackBar = null;
    private boolean singleNumberMode = false;
    private LongSparseArray<ContactNumber> singleContactNumbers = new LongSparseArray<>();

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
            singleNumberMode = arguments.getBoolean(SINGLE_NUMBER_MODE);
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_contacts, container, false);
    }
    @Override
    public void onViewCreated( final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // snack bar
        snackBar = new ButtonsBar(view, R.id.three_buttons_bar);
        // "Cancel button" button
        snackBar.setButton(R.id.button_left,
                getString(R.string.CANCEL),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.showToast((Activity) getContext(),"CANCEL", 1);
                        finishActivity(Activity.RESULT_CANCELED);

                    }
                });
        // "Add" button
        snackBar.setButton(R.id.button_center,
                getString(R.string.ADD),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                        Utils.showToast((Activity) getContext(),"To Be Implemented", 1);
                        // write checked contacts to the DB
                        addCheckedContacts();
                    }
                });
        // "Select all" button
        snackBar.setButton(R.id.button_right,
                getString(R.string.SELECT_ALL),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCheckedAllItems();
                    }
                });
        snackBar.setButton(R.id.button_secondRight,
                getString(R.string.Clear),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: ");
                        setUnCheckedAllItems();
                        snackBar.dismiss();
                    }
                });

        cursorAdapter = new ContactsCursorAdapter(getContext());

        cursorAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View row) {
                if (cursorAdapter.hasCheckedItems()) {
                    snackBar.show();
                } else {
                    snackBar.dismiss();
                }
                if(singleNumberMode && cursorAdapter.isItemChecked(row)){
                    Utils.showToast((Activity) getContext(),"is checked", 1);
                    Contact contact = cursorAdapter.getContact(row);
                    if(contact != null && contact.numbers.size() > 1){
                        askForSingleContactNumber(contact);
                    }
                }

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
    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(0);
        super.onDestroyView();
    }

    private void askForSingleContactNumber(final Contact contact) {
        DialogBuilder dialog = new DialogBuilder(getContext());
        dialog.setTitle(contact.name);
        for (ContactNumber contactNumber : contact.numbers) {
            dialog.addItem(0, contactNumber.number, contactNumber, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactNumber contactNumber = (ContactNumber) v.getTag();
                    if (contactNumber != null) {
                        singleContactNumbers.put(contact.id, contactNumber);
                    }
                }
            });
        }
        if (contact.numbers.size() > 1) {
            dialog.addItem(0, "Select_all", null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    singleContactNumbers.remove(contact.id);
                }
            });
        }
        dialog.show();

    }

    // Clears all items selection
    private void clearCheckedItems() {
        singleContactNumbers.clear();
        if (cursorAdapter != null) {
            cursorAdapter.setAllItemsChecked(false);
        }
    }

    private void addCheckedContacts() {
        // get list of contacts and write it to the DB
        List<Contact> contacts = cursorAdapter.extractCheckedContacts();
        addContacts(contacts, singleContactNumbers);
    }

    private void addContacts(List<Contact> contacts, LongSparseArray<ContactNumber> singleContactNumbers) {
        // if permission is granted
        if (!Permissions.notifyIfNotGranted(getContext(), Permissions.WRITE_EXTERNAL_STORAGE)) {
            ContactsWriter writer = new ContactsWriter(contactType, contacts, singleContactNumbers.clone());
            writer.execute();
        }
    }

    private void setUnCheckedAllItems() {
        singleContactNumbers.clear();
        if (cursorAdapter != null) {
            cursorAdapter.setAllItemsChecked(false);
        }
    }

    // Sets all items selected
    private void setCheckedAllItems() {
        singleContactNumbers.clear();
        if (cursorAdapter != null) {
            cursorAdapter.setAllItemsChecked(true);
        }
    }

    private void finishActivity(int results) {
        getActivity().setResult(results);
        getActivity().finish();
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

    // Async task - writes contacts to the DB
    private class ContactsWriter extends AsyncTask<Void, Integer, Void> {
        ProgressDialogHolder progress = new ProgressDialogHolder();
        List<Contact> contacts;
        LongSparseArray<ContactNumber> singleContactNumbers;
        private int contactType;

        ContactsWriter(int contactType, List<Contact> contacts,
                       LongSparseArray<ContactNumber> singleContactNumbers) {
            this.contactType = contactType;
            this.contacts = contacts;
            this.singleContactNumbers = singleContactNumbers;
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseAccessHelper db = DatabaseAccessHelper.getInstance(getContext());
            if (db != null) {
                int count = 1;
                for (Contact contact : contacts) {
                    if (isCancelled()) break;
                    ContactNumber contactNumber = singleContactNumbers.get(contact.id);
                    if (contactNumber != null) {
                        // add only the single number of contact
                        db.addContact(contactType, contact.name, contactNumber);
                        publishProgress(100 / contacts.size() * count++);
                    } else {
                        // add all numbers of contact
                        db.addContact(contactType, contact.name, contact.numbers);
                        publishProgress(100 / contacts.size() * count++);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progress.dismiss();
            finishActivity(Activity.RESULT_OK);
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            finishActivity(Activity.RESULT_OK);
        }

        @Override
        protected void onPreExecute() {
            progress.show(getContext(), new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ContactsWriter.this.cancel(true);
                }
            });
            progress.setMessage("Saving" + " 0%");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setMessage("Saving" + " " + values[0] + "%");
        }
    }
}
