package com.commonwealth.banking.data.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.Html;

import com.commonwealth.banking.util.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Transaction extends BaseObservable implements Comparable<Transaction> {

    private String id;
    public String effectiveDate;
    private String formattedDate;
    public String description;
    private String amount;
    private String atmId;
    private Date date;
    private boolean isPending = false;
    public boolean isSameDate = false;

    DateFormat inputDateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat newDateFormatter = new SimpleDateFormat("dd MMM yyyy");

    public String getId() {
        return id;
    }

    @Bindable
    public String getFormattedDate() {

        if (formattedDate == null) {
            date = parse(effectiveDate);
            if (date == null) {
                return effectiveDate;
            } else {
                formattedDate = newDateFormatter.format(date).toUpperCase();
            }
        }

        return formattedDate;
    }

    private Date parse(String effectiveDate) {
        Date date = null;
        try {
            date = inputDateFormatter.parse(effectiveDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return date;
    }

    @Bindable
    public String getFormattedDuration() {
        Date now = Calendar.getInstance().getTime();
        if (date == null)
            date = parse(effectiveDate);
        return getDuration(now, date);
    }

    private String getDuration(Date now, Date date) {
        long days = (now.getTime() - date.getTime()) / 86400000;
        return new StringBuffer().append(days).append(" days ago").toString();
    }

    @Bindable
    public String getAmount() {
        return Utils.formatDollar(amount);
    }

    public String getAtmId() {
        return atmId;
    }

    @Override
    public int compareTo(Transaction another) {
        try {
            Date d1 = inputDateFormatter.parse(this.effectiveDate);
            Date d2 = inputDateFormatter.parse(another.effectiveDate);
            return d2.compareTo(d1);
        }
        catch (Exception exp) {
            exp.printStackTrace();
        }

        return 0;
    }

    @Bindable
    public CharSequence getFormattedDescription() {
        return Html.fromHtml(getDescription(isPending()));
    }

    public String getDescription(boolean pending) {
        StringBuffer sb = new StringBuffer();
        if (pending)
            sb.append("<b><font color='black'>PENDING:</font></b> ");
        sb.append(description);

        return sb.toString();
    }

    private boolean isPending() {
        return isPending;
    }

    public void setPending(boolean isPending) {
        this.isPending = isPending;
    }
}
