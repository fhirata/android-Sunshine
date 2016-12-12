package com.example.fabiohh.sunshine.app;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by fabiohh on 10/16/16.
 */

/*
 * Thread to manage live recording/playback of voice input from the device's microphone.
 */
public class Audio extends AsyncTask<Void, Pair, Integer> {
    private boolean stopped = false;
    DetailFragment fragment;
    double originalSpeed;
    AudioRecord recorder = null;
    //AudioTrack track = null;
    short[][] buffers = new short[256][160];
    int ix = 0;
    Integer minimumLevel = 29000;
    Integer maxLevel = 31200;
    Integer N = new Integer(0);

    /**
     * Give the thread high priority so that it's not canceled unexpectedly, and start it
     */
    public Audio(DetailFragment fragment, double originalSpeed) {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        this.fragment = fragment;
        this.originalSpeed = originalSpeed;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Log.i("Audio", "Running Audio Thread");

        /*
         * Initialize buffer to hold continuously recorded audio data, start recording, and start
         * playback.
         */
        try {
            N = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, N * 10);
            //track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
            //        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, N * 10, AudioTrack.MODE_STREAM);
            recorder.startRecording();
            //track.play();

            /*
             * Loops until something outside of this thread stops it.
             * Reads the data from the recorder and writes it to the audio track for playback.
             */
            while (!stopped) {
                //Log.i("Map", "Writing new data to buffer");
                short[] buffer = buffers[ix++ % buffers.length];
                N = recorder.read(buffer, 0, buffer.length);
                //track.write(buffer, 0, buffer.length);

                for (int i = 0; i < N; i++) {
                    if (buffer[i] > maxLevel) {
                        Pair<Integer, Boolean> status = new Pair<>((int) buffer[i], true);
                        publishProgress(status);
                    } else if (buffer[i] > minimumLevel) {
                        Pair<Integer, Boolean> status = new Pair<>((int) buffer[i], false);
                        publishProgress(status);
                    }
                }
            }
        } catch (Throwable x) {
            Log.w("Audio", "Error reading voice audio", x);
        }
        return N;
    }

    @Override
    protected void onProgressUpdate(Pair... values) {
        super.onProgressUpdate(values);

        Pair<Integer, Boolean> pairs = (Pair) values[0];
        double soundValue = pairs.first / 1000;
        double newSpeed = (soundValue > originalSpeed) ? soundValue : originalSpeed;

        Animation rotation = AnimationUtils.loadAnimation(fragment.getActivity(), R.anim.rotation);
        rotation.setDuration((long) fragment.convertWindSpeed(newSpeed));
//        fragment.getView().findViewById(R.id.windmill).setAnimation(rotation);
        fragment.setWindSpeed((int) newSpeed);

        if (pairs.second) {
            fragment.blowAwayViews();
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        // Frees the thread's resources after the loop completes so that it can be run again
        recorder.stop();
        recorder.release();
        //track.stop();
        //track.release();
    }
}