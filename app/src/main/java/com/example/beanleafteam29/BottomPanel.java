package com.example.beanleafteam29;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class BottomPanel extends BottomSheetDialogFragment {
    BottomPanel self = null;
    String titleStr;
    String locId;
    GeoPoint coordinate;
    LatLng userLocation;

    public BottomPanel(String _titleStr, String _locId, GeoPoint _coordinate, LatLng _userLocation){
        titleStr = _titleStr;
        locId = _locId;
        coordinate = _coordinate;
        userLocation = _userLocation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet, container, false);
        TextView title = v.findViewById(R.id.titleTV);
        title.setText(titleStr);
        Button button1 = v.findViewById(R.id.menuBtn);
        final Button button2 = v.findViewById(R.id.navigateBtn);
        Button button3 = v.findViewById(R.id.btmLocHistory);

        HashMap<String, Object> myLocations = FirebaseUIActivity.getUserLocations();
        if (myLocations.containsKey(locId)) {
            button2.setVisibility(View.VISIBLE);
            button3.setVisibility(View.VISIBLE);
        } else {
            button2.setVisibility(View.GONE);
            button3.setVisibility(View.GONE);
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderMenuIntent = new Intent(getContext(), OrderMenuActivity.class);
                orderMenuIntent.putExtra("locationID", locId);
                orderMenuIntent.putExtra("locationName", titleStr);
                orderMenuIntent.putExtra("userLat", userLocation.latitude);
                orderMenuIntent.putExtra("userLng", userLocation.longitude);
                orderMenuIntent.putExtra("locationLat", coordinate.getLatitude());
                orderMenuIntent.putExtra("locationLng", coordinate.getLongitude());
                startActivity(orderMenuIntent);
                self.dismiss();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editLocIntent = new Intent(getContext(), Edit_Location.class);
                editLocIntent.putExtra("locationID", locId);
                startActivity(editLocIntent);
                self.dismiss();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SellerHistoryActivity.class);
                intent.putExtra("locationID", locId);
                intent.putExtra("locationName", titleStr);
                startActivity(intent);
                self.dismiss();
            }
        });

        return v;
    }
}