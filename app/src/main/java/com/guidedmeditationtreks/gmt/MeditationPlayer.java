package com.guidedmeditationtreks.gmt;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

/**
 * Created by mrrussell on 2/17/16.
 */
public class MeditationPlayer {

    public static final int MED_SPIRIT_GUIDE = 1;
    public static final int MED_RELATIONSHIP = 2;
    public static final int MED_THOUGHTS = 3;
    public static final int MED_MANIFEST = 4;

    private int activeMeditation;
    private boolean isIsochronic;
    private boolean isPlaying;
    private int medDuration;

    private MediaPlayer voicePlayer;
    private MediaPlayer binauralPlayer;
    private MediaPlayer isoPlayer;
    private MainActivity activity;

    private int voiceCurrentPosition;

    private float currentToneVolume;
    private float currentVoiceVolume;
    private float currentNatureVolume;
    private float currentNoiseVolume;

    private SoundPool soundPool;
    private int noiseSoundId;
    private int waterdropsSoundId;
    private int rainSoundId;
    private int natureSoundId;
    private int noiseStreamId;
    private int waterdropsStreamId;
    private int rainStreamId;
    private boolean streamsLoaded = false;

    public MeditationPlayer (MainActivity activity, float currentToneVolume, float currentVoiceVolume, float currentNatureVolume, float currentNoiseVolume) {
        this.activity = activity;
        this.currentToneVolume = currentToneVolume;
        this.currentNatureVolume = currentNatureVolume;
        this.currentVoiceVolume = currentVoiceVolume;
        this.currentNoiseVolume = currentNoiseVolume;

        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        noiseSoundId = soundPool.load(activity.getBaseContext(), R.raw.pinknoise, 3);
        waterdropsSoundId = soundPool.load(activity.getBaseContext(), R.raw.waterdrop, 2);
        rainSoundId = soundPool.load(activity.getBaseContext(), R.raw.rain, 1);
    }

    public void beginMeditation(int activeMeditation) {

        if (streamsLoaded == false) {
            rainStreamId = soundPool.play(rainSoundId, 0, 0, 1, -1, 1);
            waterdropsStreamId = soundPool.play(waterdropsSoundId, 0, 0, 1, -1, 1);
            noiseStreamId = soundPool.play(noiseSoundId, 0, 0, 1, -1, 1);
            streamsLoaded = true;
        }

        if (this.activeMeditation != 0) {

            if (voicePlayer.isPlaying()) {
                voicePlayer.stop();
            }
            if (binauralPlayer.isPlaying()) {
                binauralPlayer.stop();
            }
            if (isoPlayer.isPlaying()) {
                isoPlayer.stop();
            }
        }

        this.activeMeditation = activeMeditation;

        switch (this.activeMeditation) {
            case MeditationPlayer.MED_SPIRIT_GUIDE:
                voicePlayer = MediaPlayer.create(activity, R.raw.m01voice);
                isoPlayer = MediaPlayer.create(activity, R.raw.m01isochronic);
                binauralPlayer = MediaPlayer.create(activity, R.raw.m01binaural);
                natureSoundId = rainSoundId;
                break;
            case MeditationPlayer.MED_RELATIONSHIP:
                voicePlayer = MediaPlayer.create(activity, R.raw.m02voice);
                isoPlayer = MediaPlayer.create(activity, R.raw.m02isochronic);
                binauralPlayer = MediaPlayer.create(activity, R.raw.m02binaural);
                natureSoundId = waterdropsSoundId;
                break;
            case MeditationPlayer.MED_THOUGHTS:
                voicePlayer = MediaPlayer.create(activity, R.raw.m03voice);
                isoPlayer = MediaPlayer.create(activity, R.raw.m03isochronic);
                binauralPlayer = MediaPlayer.create(activity, R.raw.m03binaural);
                natureSoundId = rainSoundId;
                break;
            case MeditationPlayer.MED_MANIFEST:
                voicePlayer = MediaPlayer.create(activity, R.raw.m04voice);
                isoPlayer = MediaPlayer.create(activity, R.raw.m04isochronic);
                binauralPlayer = MediaPlayer.create(activity, R.raw.m04binaural);
                natureSoundId = rainSoundId;
                break;
            default:
                return;
        }

        if (isIsochronic) {
            binauralPlayer.setVolume(0, 0);
            isoPlayer.setVolume(currentToneVolume, currentToneVolume);
        } else {
            isoPlayer.setVolume(0, 0);
            binauralPlayer.setVolume(currentToneVolume, currentToneVolume);
        }

        voicePlayer.setVolume(currentVoiceVolume, currentVoiceVolume);
        voiceCurrentPosition = 0;

        voicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                activity.hidePlayButton();
                setNatureVolumeValue(0);
                setNoiseVolumeValue(0);
                setActiveMeditation(0);
                binauralPlayer.stop();
                isoPlayer.stop();
            }
        });

        medDuration = voicePlayer.getDuration();
        play();
    }

    public void play(){
//        voicePlayer.seekTo(voiceCurrentPosition);
//        binauralPlayer.seekTo(voiceCurrentPosition);
//        isoPlayer.seekTo(voiceCurrentPosition);

        setVoiceVolume(currentVoiceVolume);
        setTonesVolume(currentToneVolume);
        setNatureVolume(currentNatureVolume);
        setNoiseVolume(currentNoiseVolume);

        voicePlayer.start();
        isoPlayer.start();
        binauralPlayer.start();
        isPlaying = true;
    }

    public void pause() {
        voiceCurrentPosition = voicePlayer.getCurrentPosition();
        voicePlayer.pause();
        setNatureVolumeValue(0);
        setNoiseVolumeValue(0);
        isoPlayer.pause();
        binauralPlayer.pause();
        isPlaying = false;
    }

    public void setIsochronic(boolean isIsochronic) {
        this.isIsochronic = isIsochronic;
        setTonesVolume(currentToneVolume);
    }

    public void setVoiceVolume(float volume) {
        currentVoiceVolume = volume;
        if (isPlaying)
            voicePlayer.setVolume(volume, volume);
    }

    public void setTonesVolume(float volume) {
        currentToneVolume = volume;
        if (isPlaying)
        {
            if (isIsochronic) {
                binauralPlayer.setVolume(0, 0);
                isoPlayer.setVolume(currentToneVolume, currentToneVolume);
            } else {
                isoPlayer.setVolume(0, 0);
                binauralPlayer.setVolume(currentToneVolume, currentToneVolume);
            }
        }
    }

    public void setNoiseVolume(float volume) {
        currentNoiseVolume = volume;
        setNoiseVolumeValue(volume);
    }

    private void setNoiseVolumeValue(float volume) {
        soundPool.setVolume(noiseStreamId, volume, volume);
    }

    public void setNatureVolume(float volume) {
        currentNatureVolume = volume;
        setNatureVolumeValue(volume);
    }

    private void setNatureVolumeValue(float volume) {
        if (natureSoundId == waterdropsSoundId) {
            soundPool.setVolume(waterdropsStreamId, volume, volume);
            soundPool.setVolume(rainStreamId, 0, 0);
        } else {
            soundPool.setVolume(waterdropsStreamId, 0, 0);
            soundPool.setVolume(rainStreamId, volume, volume);
        }
    }

    private void setActiveMeditation(int a) {
        this.activeMeditation = a;
    }

    public MediaPlayer getVoicePlayer() {
        return voicePlayer;
    }

}
