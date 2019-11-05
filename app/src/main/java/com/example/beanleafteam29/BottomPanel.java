package com.example.beanleafteam29;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import android.support.annotation.Nullable;
//import android.support.design.widget.BottomSheetDialogFragment;
//import android.support.design.widget.BottomSheetDialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup
//import android.support.v4.app.DialogFragment;
//import android.content.Context;

public class BottomPanel extends BottomSheetDialogFragment {
//    private BottomSheetListener mListener;
    BottomPanel self = null;
    String titleStr = new String();
    String locId = new String();
    GeoPoint coordinate;

    public BottomPanel(String _titleStr, String _locId, GeoPoint _coordinate){
        titleStr = _titleStr;
        locId = _locId;
        coordinate = _coordinate;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet, container, false);
        TextView title = v.findViewById(R.id.titleTV);
        title.setText(titleStr);
        Button button1 = v.findViewById(R.id.menuBtn);
        final Button button2 = v.findViewById(R.id.navigateBtn);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Locations")
                .whereEqualTo("Owner", FirebaseUIActivity.getUid())
                .whereEqualTo("Name", titleStr)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() != 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getId().equals(locId)) {
                                        button2.setVisibility(View.VISIBLE);
                                    } else {
                                        button2.setVisibility(View.GONE);
                                    }
                                }
                            } else { // user does not own any restaurant
                                button2.setVisibility(View.GONE);
                            }
                        }
                    }
    });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderMenuIntent = new Intent(getContext(), OrderMenuActivity.class);
                orderMenuIntent.putExtra("locationID", locId);
                orderMenuIntent.putExtra("locationName", titleStr);
                startActivity(orderMenuIntent);
                self.dismiss();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editLocIntent = new Intent(getContext(), EditLocationActivity.class);
                editLocIntent.putExtra("locationID", locId);
                startActivity(editLocIntent);
                self.dismiss();
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