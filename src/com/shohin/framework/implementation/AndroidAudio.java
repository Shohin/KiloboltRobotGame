package com.shohin.framework.implementation;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import com.shohin.framework.Audio;
import com.shohin.framework.Music;
import com.shohin.framework.Sound;

public class AndroidAudio implements Audio {
	
	AssetManager assets;
	SoundPool soundPool;
	
	public AndroidAudio(Activity activity) {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = activity.getAssets();
		this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
	}
	
	@Override
	public Music createMusic(String fileName) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(fileName);
			return new AndroidMusic(assetDescriptor);
		} catch (Exception e) {
			throw new RuntimeException("Couldn`t load music '" + fileName + "'");
		}
	}
	
	@Override
	public Sound createSound(String fileName) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(fileName);
			int soundId = soundPool.load(assetDescriptor, 0);
			return new AndroidSound(soundPool, soundId);
		} catch (Exception e) {
			throw new RuntimeException("Couldn`t load sound '" + fileName + "'");
		}
	}
	
}
