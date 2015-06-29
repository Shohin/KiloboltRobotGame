package com.shohin.robotgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.graphics.Color;
import android.graphics.Paint;

import com.shohin.framework.Game;
import com.shohin.framework.Graphics;
import com.shohin.framework.Image;
import com.shohin.framework.Screen;
import com.shohin.framework.Input.TouchEvent;

public class GameScreen extends Screen {

	enum GameState {
		Ready, Running, Paused, GameOver
	}
	
	GameState state = GameState.Ready;
	
	private static Background bg1, bg2;
	private static Robot robot;
	public static Heliboy hb, hb2;
	
	private Image currentSprite, character, character2, character3, heliboy, heliboy2, heliboy3, heliboy4, heliboy5;
	private Animations anim, hanim;
	
	private ArrayList tileArray = new ArrayList();
	
	int livesLeft = 1;
	Paint paint, paint2;
	
	public GameScreen(Game game) {
		super(game);
		bg1 = new Background(0, 0);
		bg2 = new Background(2160, 0);
		robot = new Robot();
		hb = new Heliboy(340, 360);
		hb2 = new Heliboy(700, 360);
		
		character = Assets.character;
		character2 = Assets.character2;
		character3 = Assets.character3;
		
		heliboy = Assets.heliboy;
		heliboy2 = Assets.heliboy2;
		heliboy3 = Assets.heliboy3;
		heliboy4 = Assets.heliboy4;
		heliboy5 = Assets.heliboy5;
		
		anim = new Animations();
		anim.addFrame(character, 1250);
		anim.addFrame(character2, 50);
		anim.addFrame(character3, 50);
		anim.addFrame(character2, 50);

		hanim = new Animations();
		hanim.addFrame(heliboy, 100);
		hanim.addFrame(heliboy2, 100);
		hanim.addFrame(heliboy3, 100);
		hanim.addFrame(heliboy4, 100);
		hanim.addFrame(heliboy5, 100);
		hanim.addFrame(heliboy4, 100);
		hanim.addFrame(heliboy3, 100);
		hanim.addFrame(heliboy2, 100);

		currentSprite = anim.getImage();

		loadMap();

		// Defining a paint object
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);

		paint2 = new Paint();
		paint2.setTextSize(100);
		paint2.setTextAlign(Paint.Align.CENTER);
		paint2.setAntiAlias(true);
		paint2.setColor(Color.WHITE);
	}
	
	private void loadMap() {
		ArrayList lines = new ArrayList();
		int width = 0;
		int height = 0;
		
		Scanner scanner = new Scanner(SampleGame.map);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			if (line == null) {
				break;
			}
			
			if (!line.startsWith("!")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}
		}
		
		height = lines.size();
		
		for (int j = 0; j < 12; j++) {
			String line = (String) lines.get(j);
			for (int i = 0; i < width; i++) {
				if (i < line.length()) {
					char ch = line.charAt(i);
					Tile t = new Tile(i, j, Character.getNumericValue(ch));
					tileArray.add(t);
				}
			}
		}
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		
		if (state == GameState.Ready) {
			updateReady(touchEvents);
		} else if (state == GameState.Running) {
			updateRunning(touchEvents, deltaTime);
		} else if (state == GameState.Paused) {
			updatePaused(touchEvents);
		} else if (state == GameState.GameOver) {
			updateGameOver(touchEvents);
		}
	}
	
	private void updateReady(List touchEvents) {
		if (touchEvents.size() > 0) {
			state = GameState.Running;
		}
	}
	
	private void updateRunning(List touchEvents, float deltaTime) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = (TouchEvent) touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_DOWN) {
				if (inBounds(event, 0, 285, 65, 65)) {
					robot.jump();
					currentSprite = anim.getImage();
					robot.setDucked(false);
				} else if (inBounds(event, 0, 350, 65, 65)) {
					if (robot.isDucked() == false && robot.isJumped() == false && robot.isReadyToFire()) {
						robot.shoot();
					}
				} else if (inBounds(event, 0, 415, 65, 65) && robot.isJumped() == false) {
					currentSprite = Assets.characterDown;
					robot.setDucked(true);
					robot.setSpeedX(0);
				} 
				if (event.x > 400) {
					robot.moveRight();
					robot.setMovingRight(true);
				}
			} else if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 0, 415, 65, 65)) {
					currentSprite = anim.getImage();
					robot.setDucked(false);
				} 
				if (inBounds(event, 0, 0, 35, 35)) {
					pause();
				}
				if (event.x > 400) {
					robot.stopRight();
				}
			}
		}
		
		if (livesLeft == 0) {
			state = GameState.GameOver;
		}
		
		robot.update();
		if (robot.isJumped()) {
			currentSprite = Assets.characterJump;
		} else if (robot.isJumped() == false && robot.isDucked() == false) {
			currentSprite = anim.getImage();
		}
		
		ArrayList projectiles = robot.getProjectiles();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = (Projectile)projectiles.get(i);
			if (p.isVisible() == true) {
				p.update();
			} else {
				projectiles.remove(i);
			}
		}
		
		updateTiles();
		hb.update();
		hb2.update();
		bg1.update();
		bg2.update();
		animate();
		
		if (robot.getCenterY() > 500) {
			state = GameState.GameOver;
		}
	}
	
	private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		if (event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private void updatePaused(List touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = (TouchEvent) touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 0, 0, 800, 240)) {
					if (!inBounds(event, 0, 0, 35, 35)) {
						resume();
					}
				}
				if (inBounds(event, 0, 240, 800, 240)) {
					nullify();
					goToMenu();
				}
			}
		}
	}
	
	private void updateGameOver(List touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = (TouchEvent) touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_DOWN) {
				if (inBounds(event, 0, 0, 800, 240)) {
					nullify();
					game.setScreen(new MainMenuScreen(game));
					return;
				}
			}
		}
	}
	
	private void updateTiles() {
		for (int i = 0; i < tileArray.size(); i++) {
			Tile t = (Tile) tileArray.get(i);
			t.update();
		}
	}
	
	@Override
	public void paint(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
		g.drawImage(Assets.background, bg2.getBgX(), bg2.getBgY());
		
		paintTiles(g);
		
		ArrayList projectiles = robot.getProjectiles();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = (Projectile) projectiles.get(i);
			g.drawRect(p.getX(), p.getY(), 10, 5, Color.YELLOW);
		}
		
		g.drawImage(currentSprite, robot.getCenterX() - 61, robot.getCenterY() - 63);
		g.drawImage(hanim.getImage(), hb.getCenterX() - 48, hb.getCenterY() - 48);
		g.drawImage(hanim.getImage(), hb2.getCenterX() - 48, hb2.getCenterY() - 48);
		
		//g.drawImage(Assets.background, 0, 0);
		
		if (state == GameState.Ready) {
			drawReadyUI();
		} else if (state == GameState.Running) {
			drawRunningUI();
		} else if (state == GameState.Paused) {
			drawPausedUI();
		} else if(state == GameState.GameOver) {
			drawGameOverUI();
		}
	}
	
	private void paintTiles(Graphics g) {
		for (int i = 0; i < tileArray.size(); i++) {
			Tile t = (Tile) tileArray.get(i);
			if (t.type != 0) {
				g.drawImage(t.getTileImage(), t.getTileX(), t.getTileY());
			}
		}
	}
	
	private void animate() {
		anim.update(10);
		hanim.update(50);
	}
	
	private void nullify() {
		paint = null;
		bg1 = null;
		bg2 = null;
		robot = null;
		hb = null;
		hb2 = null;
		currentSprite = null;
		character = null;
		character2 = null;
		character3 = null;
		heliboy = null;
		heliboy2 = null;
		heliboy3 = null;
		heliboy4 = null;
		heliboy5 = null;
		anim = null;
		hanim = null;

		// Call garbage collector to clean up memory.
		System.gc();
	}
	
	private void drawReadyUI() {
		Graphics g = game.getGraphics();
		
		g.drawARGB(155, 0, 0, 0);
		g.drawString("Tap to Start", 400, 240, paint);
	}
	
	private void drawRunningUI() {
		Graphics g = game.getGraphics();

		g.drawImage(Assets.button, 0, 285, 0, 0, 65, 65);
		g.drawImage(Assets.button, 0, 350, 0, 65, 65, 65);
		g.drawImage(Assets.button, 0, 415, 0, 130, 65, 65);
		g.drawImage(Assets.button, 0, 0, 0, 195, 65, 65);
	}
	
	private void drawPausedUI() {
		Graphics g = game.getGraphics();
		g.drawARGB(155, 0, 0, 0);
		g.drawString("Resume", 400, 165, paint2);
		g.drawString("Menu", 400, 360, paint2);
	}
	
	private void drawGameOverUI() {
		Graphics g = game.getGraphics();
		
		g.drawRect(0, 0, 1281, 801, Color.BLACK);
		g.drawString("Game Over", 400, 240, paint2);
		g.drawString("Game Over", 400, 290, paint);
	}
	
	@Override
	public void pause() {
		if (state == GameState.Running) {
			state = GameState.Paused;
		}

	}

	@Override
	public void resume() {
		if (state == GameState.Paused) {
			state = GameState.Running;
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void backButton() {
		pause();
	}
	
	private void goToMenu() {
		game.setScreen(new MainMenuScreen(game));
	}
	
	public static Background getBg1() {
		return bg1;
	}
	
	public static Background getBg2 () {
		return bg2;
	}
	
	public static Robot getRobot() {
		return robot;
	}
	

}
