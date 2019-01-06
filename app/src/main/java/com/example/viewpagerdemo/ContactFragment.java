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


public class ContactFragment extends Fragment {

    private static final int CONTACT_PERMISSION_REQCODE = 123;
    private static final int STORAGE_PERMISSION_REQCODE = 456;

    protected View fragView;

    EditText nameText;
    EditText phoneText;

    Button saveBtn;
    Button synchroBtn;

    public static ArrayAdapter adapter;

    public static ListView listview;

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
        CheckPermissionLoadContact();
        listview = (ListView) fragView.findViewById(R.id.listview);
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, MainActivity.items);
        listview.setAdapter(adapter);

        nameText = (EditText) fragView.findViewById(R.id.nameText);
        phoneText = (EditText) fragView.findViewById(R.id.phoneText);
        saveBtn = (Button) fragView.findViewById(R.id.saveBtn);
        synchroBtn = (Button)fragView.findViewById(R.id.synchroBtn);

        saveBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                onClickSaveBtn();
            }
        });
        synchroBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v ){
                synchroWithServer();
            }
        });

      /* listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (-1 < position && position < adapter.getCount()) {
                    // SET NAME AND PHONE NUMBER!
                    nameText.setText(MainActivity.dataList.get(position).get("name"));
                    phoneText.setText(MainActivity.dataList.get(position).get("phone"));

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
                        saveBtn.setVisibility(View.VISIBLE);

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
                        saveBtn.setVisibility(View.GONE);

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

    public void synchroWithServer(){
        //when user visit app first
        if(MainActivity.isFirstVisited){

        }else{
            ((MainActivity)getActivity()).loadContactFromServer();
        }
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

        Log.i("***COUNT***", "" + MainActivity.dataList.size());

        if(((nameText.getText().length() > 0) && (phoneText.getText().length() > 0)) ){
            Toast.makeText(getContext(), nameText.getText() + " " + phoneText.getText() ,Toast.LENGTH_SHORT).show();
            MainActivity.items.add(nameText.getText().toString()+": "+phoneText.getText().toString());

            MainActivity.contactName = nameText.getText().toString();
            MainActivity.contactPhone = phoneText.getText().toString();

            MainActivity.mViewPager.setCurrentItem(1, true);
        } else {
            Toast.makeText(getContext(), "Fill in the every blank!" ,Toast.LENGTH_SHORT).show();
        }
    }
}
