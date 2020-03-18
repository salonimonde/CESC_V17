package com.cesc.mrbd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cesc.mrbd.R;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.models.JobCard;
import com.squareup.picasso.Picasso;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class GoogleMapImageActivity extends ParentActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView imgBack;
    private JobCard userJobCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_image);
        Intent i = this.getIntent();
        if(i != null)
        {

            if(userJobCard == null)
            {
                userJobCard = (JobCard) i.getSerializableExtra(AppConstants.CURRENT_JOB_CARD);

            }
        }

        mContext = this;
        imgBack = (ImageView)findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        ImageViewTouch imageViewTouch = (ImageViewTouch)findViewById(R.id.image_view_zoom);
        if(userJobCard!=null&& !userJobCard.route_image.equalsIgnoreCase(" ")&& !userJobCard.route_image.equalsIgnoreCase(""))
            Picasso.with(this).load(userJobCard.route_image).into(imageViewTouch);
        else
            Toast.makeText(this,"Image Not Found ",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        if(v == imgBack){
            finish();
        }
    }
}
