package com.example.habitmanagementapplication.progress;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;

public class ProgressAdapter {
    public static void displayProgressRate(Context context, HabitDatabaseHelper dbHelper, LinearLayout prgressLayout) {
        ProgressManager progressManager = new ProgressManager();
        double[] pRate = progressManager.calculateProgressRate(dbHelper);

        prgressLayout.removeAllViews();

        View ProgressRateView = createProgressCardView(context, pRate);
        prgressLayout.addView(ProgressRateView);
    }

    public static View createProgressCardView(Context context, double[] pRate) {
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int margin = 5;
        layoutParams.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(layoutParams);

        TextView progressRateTextView = new TextView(context);
        progressRateTextView.setText("progressRate: " + printPRate(pRate));

        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(progressRateTextView);
        cardView.addView(cardContentLayout);

        return cardView;
    }

    public static String printPRate(double[] pRates) {
        String result = "";
        for (double pRate : pRates) {
            result += pRate + " ";
        }

        return result;
    }
}
