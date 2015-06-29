package com.shohin.framework.implementation;

import com.shohin.framework.Audio;
import com.shohin.framework.FileIO;
import com.shohin.framework.Game;
import com.shohin.framework.Graphics;
import com.shohin.framework.Input;
import com.shohin.framework.Screen;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

public class AndroidGame extends Activity implements Game{
	
	AndroidFastRenderView renderView;
	Graphics graphics;
	Audio audio;
	Input input;
	FileIO fileIO;
	Screen screen;
	WakeLock wakeLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		int frameBufferWidth = isPortrait ? 480 : 800;
		int frameBufferHeight = isPortrait ? 800 : 480;
		
		Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);
		float scaleX = (float) frameBufferWidth / getWindowManager().getDefaultDisplay().getWidth();
		float scaleY = (float) frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();
		
		renderView = new AndroidFastRenderView(this, frameBuffer);
		graphics = new AndroidGraphics(getAssets(), frameBuffer);
		fileIO = new AndroidFileIO(this);
		audio = new AndroidAudio(this);
		input = new AndroidInput(this, renderView, scaleX, scaleY);
		screen = getInitScreen();
		setContentView(renderView);
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyGame");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		wakeLock.acquire();
		screen.resume();
		renderView.resume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		wakeLock.release();
		renderView.pause();
		screen.pause();
		
		if (isFinishing()) {
			screen.dispose();
		}
	}
	
	@Override
	public Input getInput() {
		// TODO Auto-generated method stub
		return input;
	}
	
	@Override
	public FileIO getFileIO() {
		// TODO Auto-generated method stub
		return fileIO;
	}
	
	@Override
	public Graphics getGraphics() {
		// TODO Auto-generated method stub
		return graphics;
	}
	
	@Override
	public Audio getAudio() {
		// TODO Auto-generated method stub
		return audio;
	}
	
	@Override
	public void setScreen(Screen screen) {
		if (screen == null) {
			throw new IllegalArgumentException("Screen must not be null");
		}
		
		this.screen.pause();
		this.screen.dispose();
		screen.resume();
		screen.update(0);
		this.screen = screen;
	}
	@Override
	public Screen getCurrentScreen() {
		// TODO Auto-generated method stub
		return screen;
	}

	@Override
	public Screen getInitScreen() {
		// TODO Auto-generated method stub
		return null;
	}
}
