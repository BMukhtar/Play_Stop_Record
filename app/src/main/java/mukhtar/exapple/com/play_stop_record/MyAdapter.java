package mukhtar.exapple.com.play_stop_record;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static mukhtar.exapple.com.play_stop_record.MainActivity.ctx;

public class MyAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> data;
    int xml;

    MediaPlayer mPlayer;
    MediaRecorder mRecorder;

    boolean isPlaying = false;
    boolean isRecording = false;

    private final String LOG_TAG = "MPlayer";
    private String mFilePath;

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long updatedTime = 0L;
    private TextView timerValue;

    public MyAdapter(Context ctx, ArrayList<String> data, int xml){
        this.data = data;
        this.context = ctx;
        this.xml = xml;
        mFilePath = Environment.getExternalStorageDirectory()+"/recordsForPlayer";
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(xml, viewGroup, false);
        }

        TextView tv = (TextView)view.findViewById(R.id.textview_title);
        tv.setText(data.get(position));

        Button mPlayButton = (Button) view.findViewById(R.id.play_button);
        mPlayButton.setTag(position);
        mPlayButton.setOnClickListener(play);


        Button mStopButton = (Button) view.findViewById(R.id.stop_button);
        mStopButton.setTag(position);
        mStopButton.setOnClickListener(stop);

        ImageView im = (ImageView) view.findViewById(R.id.image_mic);
        im.setTag(position);
        im.setOnTouchListener(touchListener);

        return view;
    }

    View.OnClickListener play = new View.OnClickListener() {
        public void onClick(View v) {
            if(!isPlaying){
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlaying = false;
                            mPlayer.release();
                            mPlayer = null;
                        }
                    });
                    mPlayer.setDataSource(mFilePath+"/records/record"+v.getTag()+".3gp");
                    mPlayer.prepare();
                    mPlayer.start();
                    isPlaying = true;

                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                    isPlaying = false;
                }
            }
        }
    };


    View.OnClickListener stop = new View.OnClickListener() {
        public void onClick(View v) {
            if(isPlaying){
                isPlaying = false;
                mPlayer.release();
                mPlayer = null;
            }
        }
    };

    View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Vibrator vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(100);

                    timerValue = (TextView) ((RelativeLayout)v.getParent()).findViewById(R.id.time_tv);
                    startRecording((Integer)v.getTag());

                    Log.d(LOG_TAG, "Action_Down from item"+v.getTag());
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    Log.d(LOG_TAG, "Action_Move x cordinate: "+x);
                    if(x<-100){
                        Vibrator vb1 = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                        vb1.vibrate(100);
                        cancelRecording((Integer) v.getTag());
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    cancelRecording((Integer) v.getTag());
                    Log.d(LOG_TAG, "Action_Cancel ");

                case MotionEvent.ACTION_UP:
                    Log.d(LOG_TAG, "Action_Up ");
                    stopRecording((Integer)v.getTag());
                    break;
            }
            return true;
        }
    };

    private void startRecording(int id) {
        isRecording = true;
        File outFile = new File(mFilePath+"/records/record"+id+".3gp");
        if (outFile.exists()) {
            if(!outFile.renameTo(new File(mFilePath+"/tmp/record"+id+".3gp"))){
                Toast.makeText(ctx,"Failed finding tmp folder rename to method", Toast.LENGTH_SHORT).show();
            }
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFilePath+"/records/record"+id+".3gp");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
            timerCount(true);
        } catch (IOException e) {
            isRecording = false;
            Log.e(LOG_TAG, "prepare() failed");
        }

    }

    private void stopRecording(int id) {
        if(isRecording){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            timerCount(false);
            File tmp1 = new File(mFilePath+"/tmp/record"+id+".3gp");
            try {
                if(tmp1.exists())
                    tmp1.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRecording = false;
        }
    }

    private void cancelRecording(int id){
        if(isRecording){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            File record1 = new File(mFilePath+"/records/record"+id+".3gp");
            File tmp1 = new File(mFilePath+"/tmp/record"+id+".3gp");
            try {
                if(tmp1.exists()){
                    if(record1.exists()) record1.delete();
                    tmp1.renameTo(new File(mFilePath+"/records/record"+id+".3gp"));
                }else {
                    if(record1.exists()) record1.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRecording = false;
            timerCount(false);
            setPreviousTime(id);
        }
    }

    private void timerCount(boolean count){
        if(count){
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
        }else{
            customHandler.removeCallbacks(updateTimerThread);
        }

    }

    private void setPreviousTime(int id){
        if((new File(mFilePath+"/records/record"+id+".3gp")).exists()){
            MediaPlayer m = new MediaPlayer();
            try {
                m.setDataSource(mFilePath+"/records/record"+id+".3gp");
                m.prepare();
                updateTimerTv(m.getDuration());
                m.release();
                m=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            updateTimerTv(0);
        }


    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            updatedTime = SystemClock.uptimeMillis() - startTime;
            updateTimerTv(updatedTime);
            customHandler.postDelayed(this, 0);
        }
    };

    public void updateTimerTv(long time){
        int secs = (int) (time / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        timerValue.setText("" + String.format("%02d", mins) + ":"+ String.format("%02d", secs));
    }

}
