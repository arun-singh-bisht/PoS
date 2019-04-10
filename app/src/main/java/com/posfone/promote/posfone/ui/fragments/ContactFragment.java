package com.posfone.promote.posfone.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.TitilliumWebTextView;
import com.posfone.promote.posfone.data.local.db.DatabaseHelper;
import com.posfone.promote.posfone.data.local.models.Contact;
import com.posfone.promote.posfone.ui.activities.Dialer;
import com.posfone.promote.posfone.ui.activities.VoiceActivity2;
import com.posfone.promote.posfone.ui.adapters.ConatactPageAdapter;
import com.posfone.promote.posfone.ui.adapters.GenericListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class ContactFragment extends BaseFragment implements LoaderManager.LoaderCallbacks,AdapterView.OnItemClickListener{
    TabLayout tabLayout;
    ViewPager viewPager;
    ConatactPageAdapter viewPageAdapter;
    String fragmentName;
    private View view;
    Cursor cursor;
    DatabaseHelper mDatabaseHelper;
    GenericListAdapter genericListAdapter;
    ArrayList<Contact> alContacts;
    public static ArrayList<Contact> searchContacts;

    static String search_value;


    public String getSearch_value() {
        return search_value;
    }

    public void setSearch_value(String searchValue) {
        this.search_value = searchValue;
    }

    private final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";

    @Nullable
    @BindView(R.id.txt_search)
    EditText searchBox;

    //Add Contact
    @OnClick(R.id.add_contact)
    public  void add(){

// Creates a new Intent to insert a contact
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
// Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        /*
         * Inserts new data into the Intent. This data is passed to the
         * contacts app's Insert screen
         */
// Inserts an email address
       // intent.putExtra(ContactsContract.Intents.Insert.EMAIL, "rajatkumar593@gmail.com")
/*
 * In this example, sets the email type to be a work email.
 * You can set other email types as necessary.
 */
                //.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
// Inserts a phone number
               // intent.putExtra(ContactsContract.Intents.Insert.PHONE, "8003867926")
/*
 * In this example, sets the phone type to be a work phone.
 * You can set other phone types as necessary.
 */             // .putExtra(ContactsContract.Intents.Insert.NAME,"Rajat")
               // .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
                startActivity(intent);
                //Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT).show();
    }

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
        mDatabaseHelper = new DatabaseHelper(getActivity(),"contact_table");
        mDatabaseHelper.deleteContacts();
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
                if(charSequence.toString().equals("")){
                    // reset listview
                    System.out.println("---------------clear here ------------------------------------1   ");
                    loadContactList(alContacts);
                }
                else{
                         serachContact(charSequence.toString());
                    System.out.println("---------------clear here ------------------------------------2   ");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
               // loadContactList(alContacts);
                serachContact(editable.toString());
            }
        });
    }


    @Override
    public void onResume() {
        Log.i("PaymentFragment",fragmentName+"Fragment onResume");
        super.onResume();
        setTitle("Contacts");
        setSearch_value("");

    }


    private void serachContact(String searchValue)
    {
        /*ArrayList<Contact> contacts=new ArrayList<>();
        for(Contact item:alContacts) {
            if (item.getContactName().contains(searchValue)) {
                contacts.add(item);
            }
        }
        genericListAdapter.notifyDataSetChanged();*/
       // listView.setAdapter(genericListAdapter);
        //listView.setOnItemClickListener(this);
      //Reset Loader
        getLoaderManager().restartLoader(1, null, this);
        //Log.i("serachContact",searchValue);
        setSearch_value(searchValue);
        System.out.println("---------------clear here ------------------------------------  "+getSearch_value());

        Bundle bundle = new Bundle();

        bundle.putString("searchValue",searchValue);
        getLoaderManager().initLoader(1, bundle, this);
        getActivity().getSupportLoaderManager().initLoader(1, bundle, this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        Uri CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        CursorLoader cursorLoader = null;

        System.out.println("value here"+getSearch_value());
        //if(args!=null && args.getString("searchValue") !=null && args.getString("searchValue").length()>0)
        if(getSearch_value()!=null && getSearch_value().length()>0)
        {
            Log.i("onCreateLoader my",getSearch_value());
            //System.out.println("---------------clear here ------------------------------------");
            String[] mSelectionArgs = new String[1];
            mSelectionArgs[0]  = "%" + getSearch_value() + "%";
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

        cursor = (Cursor) data;
        if (cursor.isClosed())
               return;

        if (cursor.moveToFirst()) {

            do {
                String name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone_number = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
               long id= cursor.getLong(cursor
                       .getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
               String key=cursor.getString(cursor
                       .getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
               // Log.i("onLoadFinished","name:"+name+" phone_number:"+phone_number);

                Contact contact = new Contact();
                contact.setContactName(name);
                contact.setContactNumber(phone_number);
                contact.setId(id);
                contact.setKey(key);
                alContacts.add(contact);

            } while (cursor.moveToNext());
        }

        searchContacts=(ArrayList<Contact>) alContacts.clone();
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
        genericListAdapter = new GenericListAdapter(getActivity(),alContacts.size(),R.layout.contact_lis_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView contact_title = view.findViewById(R.id.contact_title);
                TextView contact_name = view.findViewById(R.id.contact_name);
                TextView contact_number = view.findViewById(R.id.contact_number);

                Contact contact = alContacts.get(position);
                contact_title.setText((contact.getContactName().charAt(0)+"").toUpperCase());
                contact_name.setText(contact.getContactName());
                contact_number.setText(contact.getContactNumber());
                //AddData(contact.getContactName(),contact.getContactNumber());
                return view;
            }
        };

        listView.setAdapter(genericListAdapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        final Contact contact = alContacts.get(i);
        final String size_max=contact.getContactNumber();
        final String size_med=size_max.substring(0,2);
        final String size_min=size_max.substring(0,1);
        //Show Confirmation Dialog before making a call
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.contact_display, null);
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
        ImageView edit_contact=sheetView.findViewById(R.id.edit_contact);
        TextView contact_name=sheetView.findViewById(R.id.contact_name);
        TitilliumWebTextView contact_number=sheetView.findViewById(R.id.contact_number);
        ImageView call_button=sheetView.findViewById(R.id.call_button);
        final ImageView favourite=sheetView.findViewById(R.id.set_favourite);
        favourite.setOnClickListener(new View.OnClickListener() {
            boolean flag=false;
            @Override
            public void onClick(View view) {
                if(!flag){
                favourite.setImageResource(R.drawable.star_fill);
                flag=true;}
                else{
                    favourite.setImageResource(R.drawable.star);flag=false;}
            }
        });
        edit_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri mSelectedContactUri= ContactsContract.Contacts.getLookupUri(contact.getId(), contact.getKey());
                // Creates a new Intent to edit a contact
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                /*
                 * Sets the contact URI to edit, and the data type that the
                        * Intent must match
                 */
                editIntent.setDataAndType(mSelectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);

                // Sets the special extended data for navigation
                editIntent.putExtra("finishActivityOnSaveCompleted", true);
                startActivity(editIntent);
            }
        });
        contact_name.setText(contact.getContactName());
        contact_number.setText(contact.getContactNumber());
        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(size_max.contains("+44") || size_max.contains("+91") || size_med.contains("+1") || size_med.contains("44") || size_med.contains("91")|| size_min.contains("1"))
                {
                    /*Intent intent = new Intent(getContext(),VoiceActivity2.class);
                    intent.setAction(VoiceActivity2.ACTION_OUTGOING_CALL);
                    intent.putExtra("from_number","16617480240");
                    intent.putExtra("to_number",contact.getContactNumber());
                    intent.putExtra("to_name",contact.getContactName());
                    startActivity(intent);*/
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    String merchantTwilioNumber = "+917676997124";
                    intent.setData(Uri.parse("tel:" + merchantTwilioNumber));
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(),Dialer.class);
                    intent.putExtra("number",size_max);
                    startActivity(intent);
                }
            }
        });

        /*CustomAlertDialog.showDialog(getActivity(), "Make a call to " + contact.getContactName() + "?","Call","Edit ", R.layout.custom_dialog_upgrade, new CustomAlertDialog.I_CustomAlertDialog() {
            @Override
            public void onPositiveClick() {



                if(size_max.contains("+44") || size_max.contains("+91") || size_med.contains("+1") || size_med.contains("44") || size_med.contains("91")|| size_min.contains("1"))
                {
                    Intent intent = new Intent(getContext(),VoiceActivity.class);
                    intent.putExtra("from_number","16617480240");
                    intent.putExtra("to_number",contact.getContactNumber());
                    intent.putExtra("to_name",contact.getContactName());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(),Dialer.class);
                    intent.putExtra("number",size_max);
                    startActivity(intent);
                }

            }

            @Override
            public void onNegativeClick() {
                Uri mSelectedContactUri= ContactsContract.Contacts.getLookupUri(contact.getId(), contact.getKey());
                // Creates a new Intent to edit a contact
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                *//*
                 * Sets the contact URI to edit, and the data type that the
                 * Intent must match
                 *//*
                editIntent.setDataAndType(mSelectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);

                // Sets the special extended data for navigation
                editIntent.putExtra("finishActivityOnSaveCompleted", true);
                startActivity(editIntent);
            }
        });*/

    }

    private void showToast(String msg)
    {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }
    public void AddData(String newEntry1,String newEntry2) {

        boolean insertData = mDatabaseHelper.addContactData(newEntry1,newEntry2);

        if (insertData) {

            showToast("Data Successfully Inserted!");

        } else {

            showToast("Something went wrong");

        }

    }
    public void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
           // cursor.close();
        }
    }
}
