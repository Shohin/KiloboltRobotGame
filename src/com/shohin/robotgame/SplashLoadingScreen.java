package com.shohin.robotgame;

import com.shohin.framework.Game;
import com.shohin.framework.Graphics;
import com.shohin.framework.Screen;
import com.shohin.framework.Graphics.ImageFormat;

public class SplashLoadingScreen extends Screen {

	public SplashLoadingScreen(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(float deltaTime) {
		Graphics g = game.getGraphics();
		Assets.splash = g.newImage("splash.jpg", ImageFormat.RGB565);
		
		game.setScreen(new LoadingScreen(game));

	}

	@Override
	public void paint(float deltaTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void backButton() {
		// TODO Auto-generated method stub

	}

}
