package com.posfone.promote.posfone.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.posfone.promote.posfone.ProfileActivity;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.CustomSelectorDialog;
import com.posfone.promote.posfone.VoiceActivity;
import com.posfone.promote.posfone.adapters.GenericListAdapter;
import com.posfone.promote.posfone.model.BaseModel;
import com.posfone.promote.posfone.model.Contact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class ContactFragment extends BaseFragment implements LoaderManager.LoaderCallbacks,AdapterView.OnItemClickListener{

    String fragmentName;
    private View view;
    ArrayList<Contact> alContacts;
    private final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";


    @BindView(R.id.txt_search)
    EditText searchBox;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        //getActivity().getSupportLoaderManager().initLoader(1, null, this);
        getLoaderManager().initLoader(1, null, this);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Bundle bundle = this.getArguments();
        //fragmentName = bundle.getString("fragmentName");

        Log.i("PaymentFragment",fragmentName+"Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("PaymentFragment",fragmentName+"Fragment onCreateView");

        view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this,view);
        initViews();

        return view;
    }


    private void initViews()
    {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                serachContact(editable.toString());
            }
        });
    }


    @Override
    public void onResume() {
        Log.i("PaymentFragment",fragmentName+"Fragment onResume");
        super.onResume();

        setTitle("Contacts");

    }


    private void serachContact(String searchValue)
    {
        //Reset Loader
        //getLoaderManager().restartLoader(1, null, this);

        Log.i("serachContact",searchValue);

        Bundle bundle = new Bundle();
        bundle.putString("searchValue",searchValue);
        getLoaderManager().initLoader(1, bundle, this);
        //getActivity().getSupportLoaderManager().initLoader(1, bundle, this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        Uri CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        CursorLoader cursorLoader = null;
        if(args!=null && args.getString("searchValue") !=null && args.getString("searchValue").length()>0)
        {
            Log.i("onCreateLoader",args.getString("searchValue"));

            String[] mSelectionArgs = new String[1];
            mSelectionArgs[0]  = "%" + args.getString("searchValue") + "%";
            cursorLoader = new CursorLoader(getActivity(), CONTACT_URI, null, SELECTION, mSelectionArgs, "display_name ASC");
        }else
        {
            Log.i("onCreateLoader","N/A");
            cursorLoader = new CursorLoader(getActivity(), CONTACT_URI, null, null, null, "display_name ASC");
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        Log.i("onLoadFinished","onLoadFinished");

        alContacts = new ArrayList<>();

        Cursor cursor = (Cursor) data;
        if (cursor.moveToFirst()) {

            do {
                String name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone_number = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Log.i("onLoadFinished","name:"+name+" phone_number:"+phone_number);

                Contact contact = new Contact();
                contact.setContactName(name);
                contact.setContactNumber(phone_number);
                alContacts.add(contact);

            } while (cursor.moveToNext());
        }

        //Load into ListView
        loadContactList(alContacts);

    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.i("onLoaderReset","onLoaderReset");
    }

    private void loadContactList(final List<Contact> alContacts)
    {
        ListView listView = view.findViewById(R.id.listView);
        GenericListAdapter genericListAdapter = new GenericListAdapter(getActivity(),alContacts.size(),R.layout.contact_lis_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView contact_title = view.findViewById(R.id.contact_title);
                TextView contact_name = view.findViewById(R.id.contact_name);



                Contact contact = alContacts.get(position);
                contact_title.setText((contact.getContactName().charAt(0)+"").toUpperCase());
                contact_name.setText(contact.getContactName());


                return view;
            }
        };

        listView.setAdapter(genericListAdapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        final Contact contact = alContacts.get(i);

        //Show Confirmation Dialog before making a call
        CustomAlertDialog.showDialog(getActivity(), "Make a call to " + contact.getContactName() + "?","Call","Cancel", R.layout.custom_dialog_upgrade, new CustomAlertDialog.I_CustomAlertDialog() {
            @Override
            public void onPositiveClick() {
                Intent intent = new Intent(getContext(),VoiceActivity.class);
                intent.putExtra("from_number","16617480240");
                intent.putExtra("to_number",contact.getContactNumber());
                intent.putExtra("to_name",contact.getContactName());
                startActivity(intent);
            }

            @Override
            public void onNegativeClick() {

            }
        });

    }
}
