package com.mvcoelacanth.hntfapp;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private LinearLayout donateLinearLayout, loadLinearLayout, menuLinearLayout;
    private RelativeLayout downloadRelativeLayout, bookRelativeLayout;
    private ImageView downloadLockImageView, bookLockImageView, downloadImageView, bookImageView, hntfLogoImageView;
    private boolean lockDownload = true, lockBook = true;
    private final int PERMISSION_REQUEST_CODE = 11;
    private final String url = "https://www.oracle.com/technetwork/database/enterprise-edition/oraclenetservices-neteasyconnect-133058.pdf";
    private final String fileName = "hntf";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadRelativeLayout = findViewById(R.id.downloadL);
        bookRelativeLayout = findViewById(R.id.bookL);
        donateLinearLayout = findViewById(R.id.donateL);
        downloadLockImageView = findViewById(R.id.lock1);
        bookLockImageView = findViewById(R.id.lock2);
        loadLinearLayout = findViewById(R.id.loadL);
        downloadImageView = findViewById(R.id.dwnldImg);
        hntfLogoImageView = findViewById(R.id.hwntflogo);
        menuLinearLayout = findViewById(R.id.menuLayout);

        bookImageView = findViewById(R.id.booking);

        ;

        donateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, PyamentActivity.class);
                startActivity(i);
            }
        });

        downloadRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lockDownload) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            downloadFile();
                        } else {
                            requestPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }
                    } else {
                        downloadFile();
                    }


                } else {
                    Toast.makeText(MainActivity.this, "Please donate first", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bookRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lockBook) {
                    requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getApplicationContext().getPackageName(), fileName);
                    Uri path = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", pdfFile);


                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(path, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("HNTF App")
                                .setMessage("File can't be read as your device hasn't any pdf reader installed. Do you want to download it from Play Store?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.pdfviewer"));
                                            startActivity(viewIntent);
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "Something went wrong, try again!", Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                    }


                    /*
                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getApplicationContext().getPackageName() + "/" + fileName);
                    if (f.exists()) {


                        try {
                            Intent target = new Intent(Intent.ACTION_QUICK_VIEW);

                            target.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() +File.separator+ "Android"+File.separator+"data"+File.separator + getApplicationContext().getPackageName()+File.separator+ fileName)), "application/pdf");
                            Intent intent = Intent.createChooser(target, "Open File");






                            startActivity(intent);

                        } catch (Exception e) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("HNTF App")
                                    .setMessage("File can't be read as your device hasn't any pdf reader installed. Do you want to download it from Play Store?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.pdfviewer"));
                                                startActivity(viewIntent);
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Something went wrong, try again!", Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        }

                    } else {
                        unlockDownloadLayout();
                        lockBookLayout();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please download first", Toast.LENGTH_SHORT).show();
                }
                */
                }
            }
        });

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            makePortrait();
        } else {
            makeLandscape();
        }

    }

    public int pxFromDp(final float dp) {
        return Math.round(dp * this.getResources().getDisplayMetrics().density);
    }


    private void makePortrait() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pxFromDp(350), pxFromDp(350));
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(0, pxFromDp(30), 0, 0);
        hntfLogoImageView.setLayoutParams(params);

        menuLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params2.setMargins(0, 0, 0, pxFromDp(30));
        menuLinearLayout.setLayoutParams(params2);
    }

    private void makeLandscape() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pxFromDp(350), pxFromDp(350));

        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMargins(pxFromDp(30), 0, 0, 0);
        hntfLogoImageView.setLayoutParams(params);
        hntfLogoImageView.invalidate();

        menuLinearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params2.addRule(RelativeLayout.CENTER_VERTICAL);
        params2.setMargins(0, 0, pxFromDp(30), 0);
        menuLinearLayout.setLayoutParams(params2);
        menuLinearLayout.invalidate();

    }

    private void hideDonateLayout() {
        donateLinearLayout.setVisibility(View.GONE);

    }

    private void showDonateLayout() {
        donateLinearLayout.setVisibility(View.VISIBLE);

    }

    private void lockDownloadLayout() {

        downloadLockImageView.setVisibility(View.VISIBLE);
        lockDownload = true;
        downloadImageView.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
        downloadImageView.invalidate();
    }

    private void unlockDownloadLayout() {

        downloadLockImageView.setVisibility(View.GONE);
        lockDownload = false;
        downloadImageView.getDrawable().clearColorFilter();
        downloadImageView.invalidate();

    }

    private void hideDownloadLayout() {

        downloadRelativeLayout.setVisibility(View.GONE);
    }

    private void lockBookLayout() {
        bookLockImageView.setVisibility(View.VISIBLE);
        lockBook = true;
        bookImageView.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
        bookImageView.invalidate();

    }

    private void unlockBookLayout() {
        bookLockImageView.setVisibility(View.GONE);
        lockBook = false;
        bookImageView.getDrawable().clearColorFilter();
        bookImageView.invalidate();

    }

    private void loading() {
        loadLinearLayout.setVisibility(View.VISIBLE);
    }

    private void notLoading() {
        loadLinearLayout.setVisibility(View.GONE);
    }

    private void downloadFile() {

        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName());
        new DownloadFile(this, url, fileName, file) {
            @Override
            protected void onFinish() {
                notLoading();
            }

            @Override
            protected void onDownloadSuccess() {
                Toast.makeText(MainActivity.this, "Download finished", Toast.LENGTH_SHORT).show();
                hideDownloadLayout();
                unlockBookLayout();
            }

            @Override
            protected void onDownloadFailed() {
                Toast.makeText(MainActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
                unlockDownloadLayout();
            }

            @Override
            protected void onStart() {
                loading();
            }
        }.execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            makeLandscape();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            makePortrait();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to download the book. Please allow this permission.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        //   ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);


        boolean pur = sharedPref.getBoolean("purchased", false);
        if (pur) {
            hideDonateLayout();
            unlockDownloadLayout();

        } else {
            showDonateLayout();
            lockDownloadLayout();
            lockBookLayout();
        }

        File f = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName() + "/" + fileName);
        if (f.exists()) {
            hideDonateLayout();
            hideDownloadLayout();
            unlockBookLayout();
        }

    }


}
