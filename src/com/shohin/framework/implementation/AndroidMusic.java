package com.shohin.framework.implementation;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;

import com.shohin.framework.Music;

public class AndroidMusic implements Music, OnCompletionListener, OnSeekCompleteListener, OnPreparedListener, OnVideoSizeChangedListener {
	
	MediaPlayer mediaPlayer;
	boolean isPreperaid = false;

	public AndroidMusic(AssetFileDescriptor assetDescriptor) {
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(), assetDescriptor.getStartOffset(), assetDescriptor.getLength());
			mediaPlayer.prepare();
			isPreperaid = true;
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnSeekCompleteListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnVideoSizeChangedListener(this);
			
		} catch (Exception e) {
			throw new RuntimeException("Couldn`t load music!");
		}
	}
	
	@Override
	public void play() {
		if (this.mediaPlayer.isPlaying()) {
			return;
		}
		
		try {
			synchronized (this) {
				if (!isPreperaid) {
					mediaPlayer.prepare();
				}
				mediaPlayer.start();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		if (this.mediaPlayer.isPlaying() == true) {
			this.mediaPlayer.stop();
			
			synchronized (this) {
				isPreperaid = false;
			}
		}
	}

	@Override
	public void pause() {
		if (this.mediaPlayer.isPlaying()) {
			this.mediaPlayer.pause();
		}
	}

	@Override
	public void setLooping(boolean looping) {
		this.mediaPlayer.setLooping(looping);
	}

	@Override
	public void setVolume(float volume) {
		this.mediaPlayer.setVolume(volume, volume);
	}

	@Override
	public boolean isPlaying() {
		return this.mediaPlayer.isPlaying();
	}

	@Override
	public boolean isStopped() {
		return !isPreperaid;
	}

	@Override
	public boolean isLooping() {
		return this.mediaPlayer.isLooping();
	}

	@Override
	public void dispose() {
		if (this.mediaPlayer.isPlaying()) {
			this.mediaPlayer.stop();
		}
		this.mediaPlayer.release();
		
	}

	@Override
	public void seekBegin() {
		this.mediaPlayer.seekTo(0);
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		synchronized (this) {
			isPreperaid = true;
		}
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		synchronized (this) {
			isPreperaid = false;
		}
	}

}
