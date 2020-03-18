package com.cesc.mrbd.models;

/**
 * Created by Bynry01 on 8/30/2015.
 */
public class MeterReading {
    public String meter_no;
    public String meter_reader_id;
    public String job_card_id;// "job_card_id":"151",
    public String current_meter_reading;//"current_meter_reading":"123456",
    public String meter_status;//"meter_status":"test",
    public String reader_status;//"reader_status":"test",
    public String reading_month;//"reading_month":"201609",

    public MeterImage meter_image;
    public String suspicious_activity;//"suspicious_activity":"true",
    public MeterImage suspicious_activity_image;

    public String reader_remark_comment;
    public String suspicious_remark;
    public String cur_lat;
    public String cur_lng;
    public String isUploaded;
    public String isRevisit;
    public String prv_sequence;
    public String new_sequence;
    public String reading_date;
    public String reading_taken_by;
    public String pole_no;
    public String location_guidance;
    public String current_kvah_reading;
    public String current_kva_reading;
    public String iskwhroundcompleted;
    public String iskvahroundcompleted;
    public String mobile_no;
    public String panel_no;
    public String meter_type;
    public String zone_code;
    public String current_kw_reading;
    public String current_pf_reading;
    public String status_changed;
    public String sms_sent;
    public String time_taken;
    public String meter_location;


    public String consumer_category_remark;
    public String suspicious_activity_status;
    public String air_conditioner_exist;
    public String no_of_air_conditioners;
    public String is_plastic_cover_cut;
    public String is_lat_long_verified;
    public String distance;

}


