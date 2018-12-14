package com.posfone.promote.posfone.fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.posfone.promote.posfone.MainActivity;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.TwilioTokenManager;
import com.posfone.promote.posfone.VoiceActivity2;
import com.posfone.promote.posfone.database.DatabaseHelper;
import com.posfone.promote.posfone.Dialer;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.adapters.GenericListAdapter;
import com.posfone.promote.posfone.rest.RESTClient;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContactLog extends Fragment {

     String id;

    //private OnFragmentInteractionListener mListener;
    GenericListAdapter genericListAdapter;
    DatabaseHelper mDatabaseHelper;
    public ContactLog() {
        // Required empty public constructor
    }

    public static ContactLog newInstance(String param1, String param2) {
        ContactLog fragment = new ContactLog();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHelper = new DatabaseHelper(getActivity(),"people_table");
        Cursor data = mDatabaseHelper.getData();
        if(data.getCount()>50){
            mDatabaseHelper.deletesingleName(); }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getActivity().deleteDatabase("people_table");
        View v=inflater.inflate(R.layout.fragment_contact_log, container, false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.dialer);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent=new Intent(getActivity(), Dialer.class);
                startActivity(intent);*/

                CustomAlertDialog.showInputDialog(getActivity(), "Make Call To", R.layout.custom_input_dialo, new CustomAlertDialog.I_CustomInputDialog() {
                    @Override
                    public void onPositiveClick(final String accountIdentity) {
                        Intent intent=new Intent(getActivity(), VoiceActivity2.class);
                        intent.setAction(VoiceActivity2.ACTION_OUTGOING_CALL);
                        intent.putExtra("to_number",accountIdentity);
                        startActivity(intent);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });


            }
        });
        loadContactList(v);
        return v;
    }


    private void loadContactList(final View v) {
        ListView listView = v.findViewById(R.id.mlistView);
        Cursor data = mDatabaseHelper.getData();
        if(data.getCount()>50){
            mDatabaseHelper.deletesingleName(); }
        final ArrayList<String> listData = new ArrayList<>();
        final ArrayList<String> listName = new ArrayList<>();
        final ArrayList<String> listTime = new ArrayList<>();
        final ArrayList<String> listID = new ArrayList<>();

        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listID.add(data.getString(0));
            listData.add(data.getString(1));
            listName.add(data.getString(2));
            listTime.add(data.getString(3));

        }
       genericListAdapter = new GenericListAdapter(getActivity(),listData.size(),R.layout.log_list_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView contact_title = view.findViewById(R.id.contact_log_title);
                final TextView contact_name = view.findViewById(R.id.contact_name);
                final TextView contact_number = view.findViewById(R.id.contact_number);
                final TextView contact_time = view.findViewById(R.id.contact_time);
                //listData.get(position);
               // contact_title.setText((contact.getContactName().charAt(0)+"").toUpperCase());
                contact_name.setText(listData.get(listData.size()-position-1));
                String title=listData.get(listData.size()-position-1);
                char text=title.charAt(0);
                title=Character.toString(text);
                contact_title.setText(title);
                contact_number.setText(listName.get(listName.size()-position-1));
                contact_time.setText(listTime.get(listTime.size()-position-1));
                 id=listID.get(listID.size()-position-1 );
                System.out.println(id);

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        //Toast.makeText(getActivity(),contact_name.getText().toString(),Toast.LENGTH_SHORT).show();
                        String[] log_option;
                        if(contact_name.getText().toString().equals("No Name"))
                        log_option=new String[]{"Edit Number","Copy Number","Add to Contacts"};
                        else
                            log_option=new String[]{"Edit Number","Copy Number"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                 builder.setTitle(contact_number.getText().toString())
                                .setItems(log_option, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        switch (which) {
                                            case 0:
                                                //Edit Number
                                                Intent intent = new Intent(getContext(),Dialer.class);
                                                intent.putExtra("number",contact_number.getText().toString());
                                                startActivity(intent);
                                                break;
                                            case 1:
                                                 // Copy Number
                                                final android.content.ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clipData = ClipData.newPlainText("Source Text", contact_number.getText().toString());
                                                clipboardManager.setPrimaryClip(clipData);
                                                break;/*
                                            case 2:
                                                  mDatabaseHelper.deleteName(id);
                                                  genericListAdapter.notifyDataSetChanged();
                                                  loadContactList(v);
                                                  System.out.println("---------------------------------------------------");
                                                break;*/
                                            case 2 :
                                                Intent add_intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                                                add_intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                                                add_intent.putExtra(ContactsContract.Intents.Insert.PHONE, "8003867926")
                                                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                                                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
                                                startActivity(add_intent);
                                                break;

                                        }

                                    }
                                });
                         builder.create().show();
                         return true;
                    }
                });

                return view;
            }
        };
        //ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listData);

       // mListView.setAdapter(adapter);
        listView.setAdapter(genericListAdapter);
       // ImageView imageView=.findViewById(R.id.call_button);

    }

    @Override
    public void onResume(){
        super.onResume();
        //genericListAdapter.notifyDataSetChanged();
        loadContactList(getView());
    }



}
