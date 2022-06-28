package com.jemimah.glamorous_you.ui;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.jemimah.glamorous_you.R;
import com.jemimah.glamorous_you.model.Appointment;
import com.jemimah.glamorous_you.model.BusinessService;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.SingleTop;

import java.util.ArrayList;
import java.util.List;

@Parent
@SingleTop
@Layout(R.layout.header_layout)
public class HeaderView {
    private static String TAG = "HeaderView";

    @View(R.id.txtServices)
    TextView txtServices;

    @View(R.id.txtStatus)
    TextView txtStatus;

    @View(R.id.txtDate)
    TextView txtDate;

    private final Context mContext;
    private final Appointment appointment;

    public HeaderView(Context context, Appointment appointment) {
        this.mContext = context;
        this.appointment = appointment;
    }

    @Resolve
    private void onResolve(){
        List<BusinessService> services = appointment.getServices();
        List<String> serviceNames = new ArrayList<>();
        for (BusinessService businessService: services) {
            serviceNames.add(businessService.getName());
        }
        String selectedServiceNames = TextUtils.join(", ", serviceNames);
        String text = "<font color=#A155B9>Appointment for: </font> <font color=#502B5C>" + selectedServiceNames + "</font>";
        txtServices.setText(Html.fromHtml(text));

        txtStatus.setText(appointment.getStatus());
        txtDate.setText(appointment.getAppointment_date() +" " + appointment.getAppointment_time());
    }

    @Expand
    private void onExpand(){
//        Toast.makeText(mContext, "onExpand ", Toast.LENGTH_SHORT).show();
    }

    @Collapse
    private void onCollapse(){
//        Toast.makeText(mContext, "onCollapse ", Toast.LENGTH_SHORT).show();
    }
}
