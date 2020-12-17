package com.dgricko.simplerecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordFragment extends Fragment implements View.OnClickListener {


    private NavController navController;
    private ImageButton listBtn;
    private ImageButton recordBtn;
    private TextView fileNameText;

    private MediaRecorder mediaRecorder;
    private String recordFile;

    private Chronometer timer;

    private boolean isRecording = false;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_item);
        recordBtn = view.findViewById(R.id.record_btn);
        timer = view.findViewById(R.id.record_timer);
        fileNameText = view.findViewById(R.id.record_filename);

        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_list_item:

                if (isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            navController.navigate(R.id.action_recordFragment_to_audioFragment);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("Cancel",null);
                    alertDialog.setTitle("Audio still Recording");
                    alertDialog.setMessage("You want to stop the recording?");
                    alertDialog.create().show();
                }
                navController.navigate(R.id.action_recordFragment_to_audioFragment);
                break;
            case R.id.record_btn:
                if (isRecording){
                    //Stop recording
                    stopRecording();

                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone_stop));
                    isRecording=false;
                }else {
                    //Start recording
                    startRecording();
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone_start));
                    isRecording=true;
                }
                break;
        }
    }

    private void stopRecording() {
        timer.stop();

        fileNameText.setText("Recording stop: "+recordFile+ " Save");

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm", Locale.getDefault());
        Date now =new Date();
        recordFile = "REC:"+formatter.format(now)+".3gp";

        fileNameText.setText("Recording start: "+recordFile);


        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath+"/"+recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {

            stopRecording();
        }
    }
}