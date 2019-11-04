package com.example.beanleafteam29;
//import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
//import android.support.design.widget.BottomSheetDialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomPanel extends BottomSheetDialogFragment {
//    private BottomSheetListener mListener;
    String titleStr = new String();
    String locId = new String();

    public BottomPanel(String _titleStr, String _locId){
        titleStr = _titleStr;
        locId = _locId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet, container, false);
        TextView title = v.findViewById(R.id.titleTV);
        title.setText(titleStr);
        Button button1 = v.findViewById(R.id.menuBtn);
        Button button2 = v.findViewById(R.id.navigateBtn);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderMenuIntent = new Intent(getContext(), OrderMenuActivity.class);
                orderMenuIntent.putExtra("locationID", locId);
                startActivity(orderMenuIntent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editMenuIntent = new Intent(getContext(), OrderMenuActivity.class);
                startActivity(editMenuIntent);
            }
        });

        return v;
    }

//    public interface BottomSheetListener {
//        void onButtonClicked(String text);
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try {
//            mListener = (BottomSheetListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement BottomSheetListener");
//        }
//    }
}