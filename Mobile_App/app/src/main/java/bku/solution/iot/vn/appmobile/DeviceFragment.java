package bku.solution.iot.vn.appmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by PHITRUONG on 11/19/2021.
 */

public class DeviceFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_fragment, container, false);
        SeekBar seekBarTemp = view.findViewById(R.id.seekBarTempDevice);
        Button btnMode1 = view.findViewById(R.id.btnMode1Device),
                btnMode2 = view.findViewById(R.id.btnMode2Device),
                btnMode3 = view.findViewById(R.id.btnMode3Device);
        final TextView txtAirCondition = view.findViewById(R.id.txtAirConditionerDevice),
                txtStateHumi = view.findViewById(R.id.txtStateHumiDevice);
        final ImageView imgDoor = view.findViewById(R.id.imgDoorDevice),
                imgLight = view.findViewById(R.id.imgLightDevice);
        SwitchCompat swDoor = view.findViewById(R.id.swDoorDevice),
                swLight = view.findViewById(R.id.swLightDevice);
        imgDoor.setImageDrawable(getActivity().getDrawable(R.drawable.ic_closedoor));
        imgLight.setImageDrawable(getActivity().getDrawable(R.drawable.light_off));
        seekBarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    txtAirCondition.setText(Integer.toString(progress) + "°C");
                    txtAirCondition.setTextColor(Color.parseColor("#06ffff"));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnMode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStateHumi.setText("OFF");
                txtStateHumi.setTextColor(Color.parseColor("#06ffff"));
            }
        });
        btnMode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStateHumi.setText("SLOW SPEED");
                txtStateHumi.setTextColor(Color.parseColor("#06ffff"));
            }
        });
        btnMode3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStateHumi.setText("HIGH SPEED");
                txtStateHumi.setTextColor(Color.parseColor("#06ffff"));
            }
        });
        swDoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgDoor.setImageDrawable(getActivity().getDrawable(R.drawable.ic_opendoor));
                } else {
                    imgDoor.setImageDrawable(getActivity().getDrawable(R.drawable.ic_closedoor));
                }
            }
        });
        swLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgLight.setImageDrawable(getActivity().getDrawable(R.drawable.light_on));
                } else {
                    imgLight.setImageDrawable(getActivity().getDrawable(R.drawable.light_off));
                }
            }
        });
        return view;
    }
}
