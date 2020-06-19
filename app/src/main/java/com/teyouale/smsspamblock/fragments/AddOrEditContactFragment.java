package com.teyouale.smsspamblock.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teyouale.smsspamblock.Adapter.ContactsCursorAdapter;
import com.teyouale.smsspamblock.ContactsAccessHelper.ContactSourceType;
import com.teyouale.smsspamblock.R;
import com.teyouale.smsspamblock.utils.FragmentConstants;


public class AddOrEditContactFragment extends Fragment implements FragmentConstants {
    public ContactSourceType sourceType = null;
    public int contact_type = 0;
    private ContactsCursorAdapter cursorAdapter = null;

    public AddOrEditContactFragment() {
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
        if(arguments != null){
            contact_type = arguments.getInt(CONTACT_TYPE);
            sourceType = (ContactSourceType) arguments.getSerializable(SOURCE_TYPE);
            //singleNumberMode = arguments.getBoolean(SINGLE_NUMBER_MODE);
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_or_edit_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cursorAdapter = new ContactsCursorAdapter(getContext());

        // add cursor listener to the list
        ListView listView = view.findViewById(R.id.contacts_list);
        listView.setAdapter(cursorAdapter);

        // on list empty comment
        TextView textEmptyView = view.findViewById(R.id.text_empty);
        listView.setEmptyView(textEmptyView);

        // init and run the loader of contacts
       // getLoaderManager().initLoader(0, null, newLoaderCallbacks(null));

    }
}
