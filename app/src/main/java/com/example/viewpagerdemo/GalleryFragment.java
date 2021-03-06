package com.example.viewpagerdemo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.example.viewpagerdemo.MainActivity.dataList;


public class GalleryFragment extends Fragment {

    protected View fragView;
    protected Context fragContext;

    public static GridView gridView;
    public static ImageAdapter imgAdapter;
    FloatingActionButton fab1,fab2,fab3,fab4;
    Animation fabOpen,fabClose,rotateForward,rotateBackward;
    boolean isOpen = false;
    private int type;
    private OnFragmentInteractionListener mListener;
    public static Map<String,String> Check;

    public GalleryFragment() {
        // Required empty public constructor
    }

    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        this.fragView = view;
        this.fragContext = getContext();

        fab1 = (FloatingActionButton) fragView.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) fragView.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) fragView.findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) fragView.findViewById(R.id.fab4);

        fabOpen = AnimationUtils.loadAnimation(this.fragContext, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this.fragContext, R.anim.fab_close);
        rotateBackward = AnimationUtils.loadAnimation(this.fragContext, R.anim.rotate_backward);
        rotateForward = AnimationUtils.loadAnimation(this.fragContext, R.anim.rotate_forward);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Toast.makeText(fragContext,"Camera fab clicked.",Toast.LENGTH_SHORT).show();
                if(CameraUtils.checkPermissions(getActivity())){
                    ((MainActivity)getActivity()).captureImage();
                } else{
                    ((MainActivity)getActivity()).requestCameraPermission(1);
                }
                isOpen = true;
                animateFab();
            }
        });
        fab3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Toast.makeText(fragContext,"Gallery fab clicked",Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).getPhoto();
                isOpen = true;
                animateFab();
            }
        });
        fab4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (CameraUtils.checkPermissions(getActivity())) {
                    ((MainActivity)getActivity()).captureVideo();
                } else {
                    ((MainActivity)getActivity()).requestCameraPermission(2);
                }
                isOpen = true;
                animateFab();
            }
        });

        gridView = (GridView) fragView.findViewById(R.id.grid_view);

        imgAdapter = new ImageAdapter(fragContext);
        imgAdapter.notifyDataSetChanged();
        gridView.invalidateViews();
        gridView.setAdapter(imgAdapter);

        /**
         * *On Click event for single gridview item
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //sending image id to FullScreenActiviey
                Intent i = new Intent(fragContext, FullImageActivity.class);
                //passing array index
                i.putExtra("id", position);
                i.putExtra("imgUri",ImageAdapter.imageList.get(position).toString());
                startActivity(i);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "long click !", Toast.LENGTH_SHORT).show();
                String stringUri = ImageAdapter.imageList.get(position).toString();
                Log.d(">>>>>>>>>uri",stringUri);
                ImageToServer(MainActivity.userAccountId, stringUri);
                Toast.makeText(getContext(), "image goes to server!!! !", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ((MainActivity)getActivity()).restoreFromBundle(savedInstanceState);

        return view;
    }

    private void animateFab(){
        if(isOpen){
            fab1.startAnimation(rotateForward);
            fab2.startAnimation(fabClose);
            fab3.startAnimation(fabClose);
            fab4.startAnimation(fabClose);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            isOpen = false;
        }else{
            fab1.startAnimation(rotateBackward);
            fab2.startAnimation(fabOpen);
            fab3.startAnimation(fabOpen);
            fab4.startAnimation(fabOpen);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isOpen = true;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public void ImageToServer(int userAccountId, String uri){
        Log.d(">>>>>>>>>>>image to file","1");
        File file = new File(Uri.parse(uri).getPath());
        Log.d(">>>>>>>>>>>image to file","2");
        byte[] bytesArray = new byte[(int) file.length()];
        try{
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody id = RequestBody.create(MediaType.parse("test/plain"), String.valueOf(userAccountId));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        String string = Arrays.toString(bytesArray);
        Map<String, RequestBody> map = new HashMap<>();
        Log.d(">>>>>>>>>>>image to file","3");
        map.put("Id", id);
        map.put("file", requestFile);
        final Call<ResponseBody> call = RetrofitClient.getInstacne().getApi().upload(body,map);
        Check = new HashMap<String,String>();
        Thread getContactThread = new Thread(){
            @Override
            public void run(){
                try{
                    ResponseBody response =call.execute().body();
                    Log.d("userContact>>>>>>>>>>>",response.toString());
                    Check.put("CS496_application_result_test","DONE");
                }catch(Exception e){
                    Log.d("RETROFIT>>>>>", "Error in getting all contacts : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        getContactThread.start();
        while(!Check.containsKey("CS496_application_result_test")){}
        Check.remove("CS496_application_result_test");
      /*String sb = HttpConnection.GetAllContacts(String.valueOf(userAccountId));
      Gson gson = new Gson();
      Type type = new TypeToken<List<Contact>>(){}.getType();
      List<Contact> FromServerContacts = gson.fromJson(sb,type);
      for(int i=0;i<FromServerContacts.size();i++){
          String name = FromServerContacts.get(i).getName();
          String pn = FromServerContacts.get(i).getPhone_number();
          String listitem = name+": "+pn;
          items.add(listitem);
      }*/
    }
}
