package com.csit321mf03aproject.beescooters;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//The bottom sheet fragment that appears when a scooter icon is tapped
public class ScooterInfoSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.scooter_info_layout, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtBattery = view.findViewById(R.id.txtBattery);
        TextView txtDistanceAway = view.findViewById(R.id.txtMarker);

        //Retrieve Bundle from Main Screen
        Bundle bundle = this.getArguments();
        String battery = bundle.getString("batteryLevel", "50");
        String distance = bundle.getString("distanceAway", "50");

        //set scooter distance and battery level
        //battery level is static and fixed for now at 80%
        txtBattery.setText(battery + "%");
        txtDistanceAway.setText(distance);
    }

}
