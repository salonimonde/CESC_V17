package com.cesc.mrbd.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.models.Consumer;
import com.cesc.mrbd.utils.App;

//import me.dm7.barcodescanner.zbar.Result;
//import me.dm7.barcodescanner.zbar.ZBarScannerView;


/**
 * Created by Bynry01 on 9/17/16.
 */
public class BarCodeScanActivity extends ParentActivity implements /*ZBarScannerView.ResultHandler,*/ View.OnClickListener {

/*
    public ZBarScannerView zBarScannerView;
*/
    private TextView tv_status, title;
    private Handler handler = new Handler();
    private boolean isSuccessfulScanning = false;
    private ImageView mBack;
    private Typeface regular;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        regular = App.getSansationRegularFont();
        initUI();

    }

    private void initUI() {
        /*zBarScannerView = (ZBarScannerView) findViewById(R.id.zBarScannerView);*/

        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_status.setTypeface(regular);
        title = (TextView) findViewById(R.id.title_bar);
        title.setTypeface(regular);
        mBack = (ImageView) findViewById(R.id.img_back);
        mBack.setOnClickListener(this);

      /*  zBarScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        zBarScannerView.stopCamera();
        zBarScannerView.startCamera();*/
        handler.postDelayed(run, AppConstants.DEFAULT_SCANNING_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
/*
        zBarScannerView.stopCamera();
*/
        handler.removeCallbacks(run);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(run);

    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (!isSuccessfulScanning) {
                /*zBarScannerView.stopCamera();*/
                tv_status.setText(R.string.stopped);
                showScanFailedDialog(getString(R.string.scanning_failed), "");
            }
        }
    };

    public void showScanFailedDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.rescan), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                isSuccessfulScanning = false;
                /*zBarScannerView.stopCamera();
                zBarScannerView.startCamera();*/
                tv_status.setText(R.string.scanning);
                handler.postDelayed(run, AppConstants.DEFAULT_SCANNING_TIME);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                handler.removeCallbacks(run);
                /*zBarScannerView.stopCamera();*/
                tv_status.setText(R.string.stopped);
                BarCodeScanActivity.this.finish();
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        nbutton.setTextSize(15.0f);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.BLACK);
        pbutton.setTextSize(15.0f);

    }

   /* @Override
    public void handleResult(Result result) {
        isSuccessfulScanning = true;
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        String qr_text = result.getContents().trim();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Result Data on Barcode");
        builder.setMessage(String.valueOf(qr_text));
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
       *//* ArrayList consumerInfo = new ArrayList(Arrays.asList(qr_text.split("\\|")));
        if (consumerInfo.size() >= 7)
        {
            JobCard consumerJobCard = DatabaseManager.getJobCardsbyConsumerNo(this, consumerInfo.get(4).toString());
            if (consumerJobCard != null)
            {

                App.ReadingTakenBy = getString(R.string.meter_reading_qr_code);

                Intent meterReadingIntent = new Intent(this, MeterReadingActivity.class);
                meterReadingIntent.putExtra(AppConstants.CURRENT_JOB_CARD, consumerJobCard);
                startActivity(meterReadingIntent);
                this.finish();
            }
            else
            {
                App.ConsumerAddedBy = getString(R.string.meter_reading_qr_code);
                if (consumerInfo.get(4).toString().trim().length() == 12) {
                    Consumer consumer=new Consumer();
                    consumer.consumer_no=consumerInfo.get(4).toString().trim();
                    consumer.meter_no=consumerInfo.get(5).toString().trim();
                    consumer.consumer_name=consumerInfo.get(6).toString();
                    showAddNewConsumerDialog(getString(R.string.qr_code_successfully_scanned_consumer_info_not_found_do_you_want_to_add_new_consumer),consumer);
                }
                else
                    showMessage(getString(R.string.invalid_qr_code_do_you_want_to_rescan));
            }
        }
        else
        {
            showMessage(getString(R.string.invalid_qr_code_do_you_want_to_rescan));
        }*//*
    }*/

    private void showAddNewConsumerDialog(String msg, final Consumer consumer) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(msg);
        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(BarCodeScanActivity.this, AddNewConsumerActivity.class);
                intent.putExtra(AppConstants.CONSUMER_OBJ, consumer);
                startActivity(intent);
                BarCodeScanActivity.this.finish();
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BarCodeScanActivity.this.finish();
            }
        });
        alert.show();
    }

    /* Display message */
    private void showMessage(String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(msg);
        alert.setCancelable(false);
        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isSuccessfulScanning = false;
                /*zBarScannerView.resumeCameraPreview(BarCodeScanActivity.this);*/
                handler.removeCallbacks(run);
                handler.postDelayed(run, AppConstants.DEFAULT_SCANNING_TIME);
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BarCodeScanActivity.this.finish();
            }
        });
        alert.show();
    }

    @Override
    public void onClick(View view) {
        if (view == mBack) {
            /*zBarScannerView.stopCamera();*/
            handler.removeCallbacks(run);
            finish();
            this.onBackPressed();
        }
    }
}
