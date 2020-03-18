package com.cesc.mrbd.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.activity.BillDistributionLandingScreen;
import com.cesc.mrbd.activity.LandingActivity;
import com.cesc.mrbd.adapters.BillDistributionHistoryAdapter;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.models.HistoryCard;
import com.cesc.mrbd.models.UploadBillHistory;
import com.cesc.mrbd.models.UploadsHistory;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

import static com.cesc.mrbd.activity.LandingActivity.meter_reader_id;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BillDistributionHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BillDistributionHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillDistributionHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<UploadBillHistory> uploadBillHistories;
    private OnFragmentInteractionListener mListener;
    private BillDistributionHistoryAdapter adapter;
    private TextView lblBlankScreenMsg;

    public BillDistributionHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillDistributionHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillDistributionHistoryFragment newInstance(String param1, String param2) {
        BillDistributionHistoryFragment fragment = new BillDistributionHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bill_distribution_history, container, false);
        //delete records not in scope
        DatabaseManager.deleteUploadsBillHistory(this.getContext(), BillDistributionLandingScreen.meter_reader_id);

        mContext = getActivity();
        lblBlankScreenMsg = (TextView) rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        getHistoryDatesArray();

        if (uploadBillHistories != null && uploadBillHistories.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void getHistoryDatesArray() {
        try {
            //delete old records which are not in range of days.
            DatabaseManager.deleteUploadsBillHistory(this.getContext(), BillDistributionLandingScreen.meter_reader_id);
            uploadBillHistories = new ArrayList<>();
            for (int i = 0; i < AppConstants.UPLOAD_HISTORY_DATE_COUNT; i++) {
                String date = CommonUtils.getPreviousDate(i);
                ArrayList<String> uploadsHistoryRoutes = DatabaseManager.getUploadsHistoryBillRoutes(mContext, date, BillDistributionLandingScreen.meter_reader_id);

                Log.d("asdasdasd","dd: "+uploadsHistoryRoutes);
                if (uploadsHistoryRoutes != null) {

                    for (int j = 0; j < uploadsHistoryRoutes.size(); j++) {

                        String route = uploadsHistoryRoutes.get(j);
                        ArrayList<UploadBillHistory> uploadsHistory = DatabaseManager.getUploadsHistoryBill(mContext, CommonUtils.getPreviousDate(i), route, BillDistributionLandingScreen.meter_reader_id);
                        String billcycle = uploadsHistory.get(0).cycle_code;

                        int normal = 0;
                        int newConsumer = 0;
                        for (int i2 = 0; i2 < uploadsHistory.size(); i2++) {
                            if (uploadsHistory.get(i2).is_new.equalsIgnoreCase("true")) {
                                newConsumer += 1;
                            } else if (uploadsHistory.get(i2).is_new .equalsIgnoreCase("false")) {
                                normal += 1;
                            }
                        }

                        UploadBillHistory historyCard = new UploadBillHistory();
                        historyCard.reading_date = date;
                        historyCard.binder_code = route;
                        historyCard.consumer_no = String.valueOf(normal);
                        historyCard.is_new = String.valueOf(newConsumer);
//                        historyCard.revisit = revisit;
                        historyCard.cycle_code = billcycle;

                        uploadBillHistories.add(historyCard);

                        if (uploadBillHistories != null) {
                            adapter = new BillDistributionHistoryAdapter(this.getContext(), uploadBillHistories, this);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
