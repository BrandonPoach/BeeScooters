package com.csit321mf03aproject.beescooters;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScooterInfoSheet extends BottomSheetDialogFragment {
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.scooter_info_layout, container, false);
        return v;
    }

    public void startQRCodeScanner (View v)
    {
        //User clicked RIDE, jump to QR Code Scanner
        Intent intent = new Intent(v.getContext(), QRCodeScannerScreen.class);
        startActivity(intent);
    }
}
