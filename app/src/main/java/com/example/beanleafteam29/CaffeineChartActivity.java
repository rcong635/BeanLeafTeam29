package com.example.beanleafteam29;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CaffeineChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caffeine_chart);


        HashMap<String, Object> userHistory = FirebaseUIActivity.getUserHistory();
        long[] caffeineAtDay = new long[7];
        for (int i = 0; i < 7; i++) {
            caffeineAtDay[i] = 0;
        }

        long secondsInAWeek = 604800;
        long secondsInADay = 86400;
        // iterate for every item in user history
        for (Map.Entry mapElement : userHistory.entrySet()) {
            HashMap<String, Object> myDocument = (HashMap) mapElement.getValue();
            Timestamp itemTimestamp = (Timestamp) myDocument.get("Date");
            long timeStampSeconds = itemTimestamp.getSeconds();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY,23);
            cal.set(Calendar.MINUTE,59);
            cal.set(Calendar.SECOND,59);
            cal.set(Calendar.MILLISECOND,999);
            long endOfTodaySeconds = cal.toInstant().getEpochSecond();

            if (endOfTodaySeconds - timeStampSeconds < secondsInAWeek) {
                // item was purchased this week
                long index = (secondsInAWeek - (endOfTodaySeconds - timeStampSeconds)) / secondsInADay;
                String name = (String) myDocument.get("Name");
                long caffeine = (long) myDocument.get("Caffeine");
                caffeineAtDay[(int)index] += caffeine;
            }
        }

        BarChart barChart = findViewById(R.id.barchart);
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        long seconds = Instant.now().getEpochSecond() - 6*secondsInADay;
        Date d;
        DateFormat df = new SimpleDateFormat("dd/MM");
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(caffeineAtDay[i], i));
            d = new Date(seconds * 1000);
            String myDate = df.format(d).toString();
            labels.add(myDate);
            seconds += secondsInADay;
        }

        BarDataSet bardataset = new BarDataSet(entries, "Your caffeine consumption day by day");
        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of labels into chart
        //barChart.setDescription("Your caffeine consumption day by day");  // set the description
        bardataset.setColors(new int[] {Color.rgb(48, 63, 159)});
        barChart.animateY(1000);

    }
}
