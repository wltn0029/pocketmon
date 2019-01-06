package com.example.viewpagerdemo;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;


public class ContactFragment extends Fragment {

    private static final int CONTACT_PERMISSION_REQCODE = 123;
    private static final int STORAGE_PERMISSION_REQCODE = 456;

    protected View fragView;

    EditText nameText;
    EditText phoneText;

    Button saveBtn;
    Button synchroBtn;

    public static ArrayAdapter adapter;
    public static int i;
    public static ListView listview;
    public static Map<String,String> Check;
    SoftKeyboard softKeyboard;
    ConstraintLayout constraintLayout;

    private CardFragment.OnFragmentInteractionListener mListener;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        this.fragView = view;
        listview = (ListView) fragView.findViewById(R.id.listview);
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, MainActivity.items);
        listview.setAdapter(adapter);

        nameText = (EditText) fragView.findViewById(R.id.nameText);
        phoneText = (EditText) fragView.findViewById(R.id.phoneText);
        saveBtn = (Button) fragView.findViewById(R.id.saveBtn);
        synchroBtn = (Button)fragView.findViewById(R.id.synchroBtn);
        CheckPermissionLoadContact();

        saveBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                onClickSaveBtn();
            }
        });

        synchroBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v ){
                //when user visit app first
                if(MainActivity.isFirstVisited){
                    int count = MainActivity.dataList.size();
                    for(i=0;i<count;i++) {
                        /**
                         * Call<Contact> postUserContact(
                         * *           @Path("userID")String userID,
                         *             @Field("id") String id,
                         *             @Field("phone_number") String phone_number,
                         *             @Field("name") String name,
                         *
                         *     );
                         */
                        final Call<Contact> call = RetrofitClient.getInstacne().getApi().postUserContact(String.valueOf(MainActivity.userAccountId),
                                                                                                         MainActivity.dataList.get(i).getPhone_number(),
                                                                                                         MainActivity.dataList.get(i).getName());
                        Check = new HashMap<String,String>();
                        Thread postNewUserContact = new Thread(){
                            @Override
                            public void run(){
                                try{
                                    call.execute();
                                    Check.put("CS496_application_post_contact_test"+String.valueOf(i),"DONE");
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        };
                        postNewUserContact.start();
                        while(!Check.containsKey("CS496_application_post_contact_test"+String.valueOf(i))){}
                        Check.remove("CS496_application_contact_test"+String.valueOf(i));
                    }
                }else{
                    ((MainActivity)getActivity()).loadContactFromServer();
                }
            }
        });

      /* listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (-1 < position && position < adapter.getCount()) {
                    // SET NAME AND PHONE NUMBER!
                    nameText.setText(MainActivity.dataList.get(position).getName());
                    phoneText.setText(MainActivity.dataList.get(position).getPhone_number());

                } else {
                    Log.i("Error", "ITEM DOES NOT EXIST!");
                }
            }
        });*/
        constraintLayout = (ConstraintLayout) fragView.findViewById(R.id.contactlayout);

        InputMethodManager controlManager = (InputMethodManager)getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(constraintLayout, controlManager);

        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged()
        {
            @Override
            public void onSoftKeyboardHide()
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //키보드 내려왔을때
                        MainActivity.mNavigation.setVisibility(View.VISIBLE);
                        synchroBtn.setVisibility(View.VISIBLE);
                        //listview.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow()
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //키보드 올라왔을때
                        MainActivity.mNavigation.setVisibility(View.INVISIBLE);
                        synchroBtn.setVisibility(View.GONE);
                        //listview.setVisibility(View.GONE);
                    }
                });
            }
        });
        return view;
    }


    public void CheckPermissionLoadContact(){
        if(getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Log.i("***PERMISSION", "Got ContactPermission");
            if (MainActivity.isFirstVisited) {
                ((MainActivity) getActivity()).loadContacts();
            }
        }
        else{
            Log.i("***PERMISSION","Try to get ContactPermission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},CONTACT_PERMISSION_REQCODE);
        }
    }

    public void GetStoragePermission(){
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.i("***PERMISSION","Got StoragePermission");
        } else {
            Log.i("***PERMISSION","Try to get ContactPermission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_REQCODE);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CardFragment.OnFragmentInteractionListener) {
            mListener = (CardFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void uploadContact(){

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void onClickSaveBtn(){
        int count, idx;
        count = adapter.getCount();

      //  Log.i("***COUNT***", "" + MainActivity.dataList.size());
        Log.i(">>>>>>>>>>..>>.>","CHECK");
        if(((nameText.getText().length() > 0) && (phoneText.getText().length() > 0)) ){
            Toast.makeText(getContext(), nameText.getText() + " " + phoneText.getText() ,Toast.LENGTH_SHORT).show();
            MainActivity.items.add(nameText.getText().toString()+": "+phoneText.getText().toString());
            /**
             * Call<Contact> postUserContact(
             * *           @Path("userID")String userID,
             *             @Field("id") String id,
             *             @Field("phone_number") String phone_number,
             *             @Field("name") String name,
             *
             *     );
             */
            final Call<Contact> call = RetrofitClient.getInstacne().getApi().postUserContact( String.valueOf(MainActivity.userAccountId),
                                                                                                phoneText.getText().toString(),
                                                                                                nameText.getText().toString());
            Check = new HashMap<String,String>();
            Thread postNewContact = new Thread(){
                @Override
                public void run(){
                    try{
                        call.execute();
                        Check.put("CS496_application_contact_test","DONE");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            };
            postNewContact.start();
            while(!Check.containsKey("CS496_application_contact_test")){}
            Check.remove("CS496_application_contact_test");
            //ContactFragment.adapter.notifyDataSetChanged();
            //ContactFragment.listview.invalidateViews();
            //ContactFragment.listview.setAdapter(ContactFragment.adapter);
            //MainActivity.mViewPager.setCurrentItem(1, true);
            ((MainActivity)getActivity()).loadContactFromServer();
        } else {
            Toast.makeText(getContext(), "Fill in the every blank!" ,Toast.LENGTH_SHORT).show();
        }
    }
}
