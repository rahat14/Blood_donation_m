package com.metacoders.blood_donation;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ConvertTime {
    public static final String TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String LIST_SEPARATOR = ",";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String listKeString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str).append(LIST_SEPARATOR);
        }
        sb.setLength(sb.length() - LIST_SEPARATOR.length());
        return sb.toString();
    }

    public static ArrayList<String> stringKeList(String s) {
        return new ArrayList<>(Arrays.asList(s.split(LIST_SEPARATOR)));
    }

    public static String capitalize(String input) {
        String[] katas = input.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < katas.length; i++) {
            String kata = katas[i];
            if (i > 0 && kata.length() > 0) {
                builder.append(" ");
            }
            String cap = kata.substring(0, 1).toUpperCase() + kata.substring(1);
            builder.append(cap);
        }
        return builder.toString();
    }

    private static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String covertTimeToText(String dataDate) {

        if (!dataDate.contains("T")) {
            dataDate = dataDate.replace(" ", "T");
        }

        String convTime = null;

        String prefix = "";
        String suffix = "Ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pasTime);
            calendar.add(Calendar.HOUR, 6);
            pasTime = calendar.getTime();
            Date nowTime = new Date();


            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + " Seconds " + suffix;
            } else if (minute < 60) {
                convTime = minute + " Minutes " + suffix;
            } else if (hour < 24) {
                convTime = hour + " Hours " + suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " Months " + suffix;
                } else {
                    convTime = (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {
                convTime = day + " Days " + suffix;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
            ;

        }

        return convTime;
    }

    public static String getTimeAgo(Date date) {
        long time = date.getTime();
        // + ((2)*HOUR_MILLIS) + 0*MINUTE_MILLIS
//        if (time < 1000000000000L) {
//            time *= 1000 ;
//        }

        long now = currentDate().getTime();
        if (time > now || time <= 0) {
            return "just now";
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "min ago";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " min ago";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if (diff < 3 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat th = new SimpleDateFormat("yyyy");
            int tahun = Integer.parseInt(th.format(date));
            if (Calendar.getInstance().get(Calendar.YEAR) == tahun) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
                return format.format(date);
            } else {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
                return df.format(date);
            }
        }
    }

    public static int getTimeDifferance(String  pastDate) {
        SimpleDateFormat df = new SimpleDateFormat(ConvertTime.TIME_FORMAT);
        Date date = null;
        long time = 0 ;
        try {
            date = df.parse(pastDate);
             time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // + ((2)*HOUR_MILLIS) + 0*MINUTE_MILLIS
//        if (time < 1000000000000L) {
//            time *= 1000 ;
//        }

        long now = currentDate().getTime();
        if (time > now || time <= 0) {
            return 0;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return 0;
        } else if (diff < 2 * MINUTE_MILLIS) {
            return 0;
        } else if (diff < 60 * MINUTE_MILLIS) {
            return 0;
        } else if (diff < 2 * HOUR_MILLIS) {
            return 0;
        } else if (diff < 24 * HOUR_MILLIS) {
            return 0;
        } else if (diff < 48 * HOUR_MILLIS) {
            return 1;
        } else if (diff < 3 * DAY_MILLIS) {
            return 1;
        } else {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat th = new SimpleDateFormat("yyyy");
            int tahun = Integer.parseInt(th.format(date));
            if (Calendar.getInstance().get(Calendar.YEAR) == tahun) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
                return 0;
            } else {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dff = new SimpleDateFormat("dd MMMM yyyy");
                return 0;
            }
        }
    }

    public static int cekHari(String tgl) {
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        SimpleDateFormat nf = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        try {
            Date date = df.parse(tgl);
            String ntgl = nf.format(date);
            int getTgl = ntgl.indexOf(" ");
            int getBln = ntgl.indexOf(" ", getTgl + 1);
            int getThn = ntgl.indexOf(" ", getBln + 1);
            int bln = Integer.parseInt(ntgl.substring(getTgl + 1, getBln)) + 1;
            int hari = Integer.parseInt(ntgl.substring(0, getTgl));
            int tahun = Integer.parseInt(ntgl.substring(getBln + 1));
            Calendar calendar = Calendar.getInstance();
            int thn_now = calendar.get(Calendar.YEAR);
            int bln_now = calendar.get(Calendar.MONTH);
            int tgl_now = calendar.get(Calendar.DAY_OF_MONTH);

            if (thn_now == tahun && bln_now == bln) {
                return hari - tgl_now;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}