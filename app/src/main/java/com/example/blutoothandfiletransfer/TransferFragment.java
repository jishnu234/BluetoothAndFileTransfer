package com.example.blutoothandfiletransfer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.blutoothandfiletransfer.databinding.FragmentTransferBinding;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class TransferFragment extends Fragment {

    private static final int REQ_GALLERY = 2;
    private FragmentTransferBinding binding;
    private Context context;
    private Activity activity;
    private  Bitmap bitmapImg;
    private boolean imgStatus = false;
    PermissionListener permissionListener;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransferBinding.inflate(getLayoutInflater(), container, false);


        binding.loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               permissionCheck();
            }
        });
        return binding.getRoot();
    }

    private void permissionCheck() {
        if(permissionListener.onClickPermission()) {
            loadImage();
        } else {
            Toast.makeText(context, "Some Permissions are denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        permissionListener = (PermissionListener) context;
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();
        activity = (Activity) view.getContext();

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgStatus)
                    shareImg();
                else
                    Snackbar.make(view, "Please Load your Image", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Load", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionCheck();
                        }
                    }).show();
            }
        });

    }

    private void shareImg() {
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//
//        File file = new File(getActivity().getExternalCacheDir()+"/"+ "Beautiful picture.jpeg");
//        Intent intent = new Intent();
//
//        try {
//            FileOutputStream outputStream = new FileOutputStream(file);
//            bitmapImg.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
//            outputStream.flush();
//            outputStream.close();
//            intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("image/*");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
//                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        startActivity(Intent.createChooser(intent,"Share image using"));


        try {
            File file = new File(context.getExternalCacheDir(), File.separator +"test_img.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmapImg.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID +".provider", file);
            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/jpg");

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadImage () {

            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra("crop","true");
            intent.putExtra("scale",true);
            intent.putExtra("outputX",256);
            intent.putExtra("outputY",256);
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            intent.putExtra("return-data",true);
            startActivityForResult(intent,REQ_GALLERY);
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_GALLERY) {
            if(resultCode==RESULT_OK)
            {
                if (data == null) throw new AssertionError();
                Bundle bundle=data.getExtras();

                if(bundle!=null)
                {
                    bitmapImg=bundle.getParcelable("data");
                    binding.imageCanvas.setImageBitmap(bitmapImg);
                    imgStatus = true;
                }
            }
        }
    }
}