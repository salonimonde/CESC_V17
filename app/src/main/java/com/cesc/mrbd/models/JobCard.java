package com.cesc.mrbd.models;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Bynry01 on 09-09-2016.
 */
public class JobCard implements Serializable {

    public String id;
    public String consumer_name;//	"consumer_name": "shubham33",
    public String consumer_no;//"consumer_no": "None",
    public String meter_no;//"meter_no": "688686",

    public String dt_code;//"dt_code": "123",
    public String bill_cycle_code;//"bill_cycle_code": "200"
    public String schedule_month;//"schedule_month": "201609",
    public String schedule_end_date;//"schedule_end_date": "2016-09-30",
    public String route_code;//"route_code": "1000",
    public String pole_no;//"pole_no": "88888",
    public String phone_no;//	"phone_no": "8007271913",
    public String address;//"address": "ABC Road Mumbai ",

    public String meter_reader_id;
    public String prv_meter_reading;
    public String lattitude;
    public String longitude;
    public String job_card_status;
    public String assigned_date;//assigned_date
    public String job_card_id;//"job_card_id": "33",
    public String is_revisit;
    public String prv_sequence;
    public String location_guidance;
    public String prv_kvah_reading;
    public String prv_kva_reading;
    //    public String prv_kwh_reading;
    public String iskwhroundcompleted;
    public String iskvahroundcompleted;
    public String zone_code;
    public String category_id;
    public String avg_consumption;
    public String meter_digit;
    public String account_no;
    public String route_image;
    public String current_sequence;
    public String snf;
    public String prv_status;
    public String zone_name;
    public String attempt;
    public String pdc;
    public JobCard() {
    }

    public JobCard(String consumer_name, String consumer_id, String meter_no, String dt_code, String bil_cycle_code, String pol_no, String route_id, String status) {
        this.consumer_name = consumer_name;
        this.consumer_no = consumer_id;
        this.meter_no = meter_no;
        this.dt_code = dt_code;
        this.bill_cycle_code = bil_cycle_code;
        this.pole_no = pol_no;
        this.route_code = route_id;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public static Comparator<JobCard> CityNameComparator = new Comparator<JobCard>() {

        public int compare(JobCard s1, JobCard s2) {
            String JobCard1 = s1.getAccount_no().toUpperCase();
            String JobCard2 = s2.getAccount_no().toUpperCase();
            //ascending order
            return JobCard1.compareTo(JobCard2);
            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };
}
