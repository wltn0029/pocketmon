package com.example.viewpagerdemo;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.VISIBLE;


public class CardFragment extends Fragment {

    protected View fragView;
    protected Context fragContext;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    private View mCardFrontLayout;
    private View mCardBackLayout;

    ImageView selectImage;
    ImageView outputImage;
    Button captureBtn;
    Button shareBtn;
    Button storageBtn;
    String strUri;
    public static TextView nameCard;
    public static TextView phoneCard;
    public static TextView addressCard;

    EasyFlipView easyFlipView;
    ImageView userImage;
    VideoView userVideo;
    RelativeLayout relativeLayout;

    private int[] images = {
            R.drawable.flower,
            R.drawable.milkyway,
            R.drawable.toystory,
            R.drawable.tree
    };

    private OnFragmentInteractionListener mListener;

    public CardFragment() {
        // Required empty public constructor
    }

    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        } else {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_card, container, false);
        this.fragView = view;
        this.fragContext = getContext();


        layoutManager = new GridLayoutManager(fragContext, 1, GridLayoutManager.HORIZONTAL, false);

        recyclerView = (RecyclerView) fragView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(images);
        recyclerView.setAdapter(adapter);
        findViews();
        selectImage = mCardFrontLayout.findViewById(R.id.cardImage);
        nameCard = mCardFrontLayout.findViewById(R.id.nameCard);
        phoneCard = mCardFrontLayout.findViewById(R.id.phoneCard);
        addressCard = mCardFrontLayout.findViewById(R.id.addressCard);
        userImage = mCardBackLayout.findViewById(R.id.userImage);
        userVideo = mCardBackLayout.findViewById(R.id.userVideo);
        setCardText();
        setUserImage();
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectImage = mCardFrontLayout.findViewById(R.id.cardImage);
                Glide.with(fragContext).load(images[position]).into(selectImage);
                selectImage.setVisibility(VISIBLE);
            }
        });

        loadAnimations();
        changeCameraDistance();

        captureBtn = (Button) fragView.findViewById(R.id.capture);
        captureBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeScreenshot();
            }
        });

        storageBtn = (Button) fragView.findViewById(R.id.storage);
        storageBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.sec.android.app.myfiles");
                if (launchIntent != null) {
                    startActivity(launchIntent); //null pointer check in case package name was not found
                } else {
                    Toast.makeText(fragContext, "File manager unavailable", Toast.LENGTH_SHORT).show();
                }

            }
        });

        relativeLayout = fragView.findViewById(R.id.relativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strUri!=null&&(strUri.contains("jpg")||strUri.contains("media"))){
                    userImage.setVisibility(VISIBLE);
                }
                flipCard();
            }
        });
        return view;
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardFrontLayout.setCameraDistance(scale);
        mCardBackLayout.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(fragContext, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(fragContext, R.animator.in_animation);
    }

    private void findViews() {
        mCardBackLayout = fragView.findViewById(R.id.card_back);
        mCardFrontLayout = fragView.findViewById(R.id.card_front);
    }

    public void flipCard() {
        if (!mIsBackVisible) {
            mSetRightOut.setTarget(mCardFrontLayout);
            mSetLeftIn.setTarget(mCardBackLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = true;
            if (strUri != null) {
                if(strUri.contains(".mp4")){
                    userVideo.setVisibility(VISIBLE);
                    userVideo.start();
                }
            }
        } else {
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
            if (strUri != null) {
                if(strUri.contains(".mp4")){
                    userVideo.setVisibility(VISIBLE);
                    userVideo.start();
                }
            }
        }
    }



    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
           final String mPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + now + ".jpg";

            selectImage.setDrawingCacheEnabled(true);
            final Bitmap bitmap = Bitmap.createBitmap(selectImage.getDrawingCache());
            selectImage.setDrawingCacheEnabled(false);

            final File imageFile = new File(mPath);

            if(ActivityCompat.checkSelfPermission(fragContext,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();

                Toast.makeText(fragContext, "Captured @ " +  mPath, Toast.LENGTH_SHORT).show();
            } else {
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    FileOutputStream outputStream = null;
                                    try {
                                        outputStream = new FileOutputStream(imageFile);
                                        int quality = 100;
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                                        outputStream.flush();
                                        outputStream.close();

                                        Toast.makeText(fragContext, "Captured @ " +  mPath, Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else if (report.isAnyPermissionPermanentlyDenied()) {
                                    Toast.makeText(fragContext, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), options);

        // outputImage.setImageBitmap(bitmap);
    }

    private void setCardText() {
        if(MainActivity.contactName != null) {
            nameCard.setText(MainActivity.contactName);
        }
        if(MainActivity.contactPhone != null) {
            phoneCard.setText(MainActivity.contactPhone);
        }
        if(MainActivity.contactAddress != null) {
            addressCard.setText(MainActivity.contactAddress);
        }
    }

    private  void setUserImage() {
        if(MainActivity.selUri != null) {
            strUri =MainActivity.selUri.toString();
            if(strUri.contains("mp4")){
                userImage.setVisibility(View.GONE);
                //userVideo.setVisibility(View.VISIBLE);
                userVideo.setVideoURI(MainActivity.selUri);
            }
            else if(strUri.contains("images") || strUri.contains("jpg")){
                userVideo.setVisibility(View.GONE);
                //userImage.setVisibility(View.VISIBLE);
                Glide.with(this).load(MainActivity.selUri).into(userImage);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setCardText();
        setUserImage();
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
}
