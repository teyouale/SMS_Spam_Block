package com.teyouale.smsspamblock.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.teyouale.smsspamblock.R;
import com.teyouale.smsspamblock.utils.DialogBuilder;
import com.teyouale.smsspamblock.utils.FragmentConstants;
import com.teyouale.smsspamblock.utils.Permissions;
import com.teyouale.smsspamblock.utils.Utils;


public class ContactsFragment extends Fragment implements FragmentConstants {
    private int contactType = 0;
    private String itemsFilter = null;
    private ListView listView = null;
    private int listPosition = 0;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set activity title
        Bundle arguments = getArguments();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (arguments != null && actionBar != null) {
            actionBar.setTitle(arguments.getString(TITLE));
        }
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
        }
        /*  To Implement Save On instance
            if (savedInstanceState != null) {
                listPosition = savedInstanceState.getInt(LIST_POSITION, 0);
            }*/
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);

        // tune menu options
        MenuItem itemSearch = menu.findItem(R.id.action_search);
        itemSearch.setVisible(true);
        MenuItem itemAdd = menu.findItem(R.id.action_add);
        itemAdd.setVisible(true);

        // get the view from search menu item
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        searchView.setQueryHint("Search_action");
        // set on text change listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                reloadItems(newText);
                return true;
            }
        });

        // on search cancelling
        // SearchView.OnCloseListener is not calling so use other way...
        MenuItemCompat.setOnActionExpandListener(itemSearch,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        reloadItems(null);
                        return true;
                    }
                });

        // item's 'add contact' on click listener
        itemAdd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // show menu dialog
                showAddContactsMenuDialog();

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    //Shows menu Dialog of Contacts adding
    private void showAddContactsMenuDialog() {
        // create and show menu dialog for actions with the contact
        DialogBuilder dialog = new DialogBuilder(getContext());
        dialog.setTitle(R.string.Add_contact).
                addItem(R.string.From_contacts_list, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddContactsActivity(Permissions.READ_CONTACTS,
                                ContactSourceType.FROM_CONTACTS,
                                R.string.List_of_contacts);
                    }
                }).
                addItem(R.string.From_calls_list, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*showAddContactsActivity(Permissions.READ_CALL_LOG,
                                ContactSourceType.FROM_CALLS_LOG,
*/                              //  R.string.List_of_calls);
                    }
                }).
                addItem(R.string.From_SMS_list, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*showAddContactsActivity(Permissions.READ_SMS,
                                ContactSourceType.FROM_SMS_LIST,
 */                           //    R.string.List_of_SMS);
                    }
                });
        if (contactType == TYPE_WHITE_LIST) {
            dialog.addItem(R.string.From_Black_list, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*showAddContactsActivity(Permissions.WRITE_EXTERNAL_STORAGE,
                            ContactSourceType.FROM_BLACK_LIST,*/
                            //R.string.Black_list);
                }
            });
        } else {
            dialog.addItem(R.string.From_White_list, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*showAddContactsActivity(Permissions.WRITE_EXTERNAL_STORAGE,
                            ContactSourceType.FROM_WHITE_LIST,*/
                         //   R.string.White_list);
                }
            });
        }
        dialog.addItem(R.string.Manually, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*showAddContactsActivity(Permissions.WRITE_EXTERNAL_STORAGE,
                        null, R.string.Adding_contact);
  */          }
        }).show();

    }

    private void showAddContactsActivity(String permission,
                                         ContactSourceType sourceType, @StringRes int titleId) {
        // if permission isn't granted
        if (Permissions.notifyIfNotGranted(getContext(), permission)) {
            return;
        }

        // permission is granted
        Bundle arguments = new Bundle();
        Class<? extends Fragment> fragmentClass;
        if (sourceType != null) {
            // fragment of adding contacts from sms-content / call log
            arguments.putInt(CONTACT_TYPE, contactType);
            arguments.putSerializable(SOURCE_TYPE, sourceType);
            arguments.putBoolean(SINGLE_NUMBER_MODE, true);
            fragmentClass = AddContactsFragment.class;
        } else {
            // fragment of adding contacts manually
            arguments.putInt(CONTACT_TYPE, contactType);
            fragmentClass = AddOrEditContactFragment.class;
        }

        // open activity with contacts adding fragment
        CustomFragmentActivity.show(getActivity(),
                getString(titleId), fragmentClass, arguments, 0);
    }

    // Types of contact sources
    public enum ContactSourceType {
        FROM_CONTACTS,
        FROM_CALLS_LOG,
        FROM_SMS_LIST,
        FROM_BLACK_LIST,
        FROM_WHITE_LIST
    }

    // Reloads items
    private void reloadItems(String itemsFilter) {
        Utils.showToast((Activity) getContext(),"Search Change", 1);
    }
}
