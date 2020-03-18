package com.cesc.mrbd.models;

import java.io.Serializable;

/**
 * Created by Piyush on 31-05-2017.
 * Bynry
 */
public class BillCard implements Serializable {
    public String jobcard_id;
    public String cycle_code;
    public String binder_code;
    public String start_date;
    public String end_date;
    public String meter_reader_id;
    public String jobcard_status;
    public String remark;
    public String billmonth;
    public String reading_date;
    public String consumer_no;
    public String consumer_name;
    public String address;
    public String prv_lat;
    public String prv_lon;
    public String cur_lat;
    public String cur_lon;
    public String meter_no;
    public String binder_id;
    public String zone_code;
    public String zone_name;
    public String is_new;
    public String bill_distributed;
    public String taken_by;
    public String bill_received;
    public String account_no;
    public String phone_no;

    public String time_taken;

    public BillCard() {
    }
}

