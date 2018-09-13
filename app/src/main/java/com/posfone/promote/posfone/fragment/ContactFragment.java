package com.posfone.promote.posfone.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.posfone.promote.posfone.ProfileActivity;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.VoiceActivity;
import com.posfone.promote.posfone.adapters.GenericListAdapter;
import com.posfone.promote.posfone.model.BaseModel;
import com.posfone.promote.posfone.model.Contact;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class ContactFragment extends BaseFragment implements LoaderManager.LoaderCallbacks,AdapterView.OnItemClickListener{

    String fragmentName;
    private View view;
    ArrayList<Contact> alContacts;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getActivity().getSupportLoaderManager().initLoader(1, null, this);
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

        return view;
    }

    @Override
    public void onResume() {
        Log.i("PaymentFragment",fragmentName+"Fragment onResume");
        super.onResume();

        setTitle("Contacts");

    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(getActivity(), CONTACT_URI, null, null, null, "display_name ASC");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        alContacts = new ArrayList<>();

        Cursor cursor = (Cursor) data;
        if (cursor.moveToFirst()) {

            do {
                String name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone_number = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //phone_number = phone_number.replaceAll("\\W+", "");
                //phone_number = phone_number.length() > 10 ? phone_number.substring(phone_number.length() - 10) : phone_number;
                /*if (phone_number.length() == 10)
                    stringStringHashMap.put("91" + phone_number, name);*/

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

        Contact contact = alContacts.get(i);

        Intent intent = new Intent(getContext(),VoiceActivity.class);
        intent.putExtra("from_number","16617480240");
        intent.putExtra("to_number",contact.getContactNumber());
        intent.putExtra("to_name",contact.getContactName());
        startActivity(intent);

    }
}
