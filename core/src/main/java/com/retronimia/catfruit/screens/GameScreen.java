package com.retronimia.catfruit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.retronimia.catfruit.entities.Coin;
import com.retronimia.catfruit.entities.KitchenMap;
import com.retronimia.catfruit.entities.Player;

import java.util.ArrayList;

public class GameScreen implements Screen {

    private static final int VIRTUAL_WIDTH = 1920;
    private static final int VIRTUAL_HEIGHT = 1080;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private KitchenMap kitchenMap;
    private Player player;

    private ArrayList<Coin> coins;
    private static final int COIN_COUNT = 10;
    private static final float COIN_SPACING = 500;
    private static final float COIN_Y_POSITION = 120;

    private boolean debugMode = false;
    private float speed = 300;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
//        viewport = new FitViewport(VIRTUAL_WIDTH * 2, VIRTUAL_HEIGHT * 2, camera); // Possibilita ver outras partesdesenhadas  como debug
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        kitchenMap = new KitchenMap(speed);

        coins = new ArrayList<>();
        for (int i = 0; i < COIN_COUNT; i++) {
            float x = i * COIN_SPACING + VIRTUAL_WIDTH;
            coins.add(new Coin(x, COIN_Y_POSITION));
        }

        player = new Player(VIRTUAL_WIDTH / 2f - Player.WIDTH / 2f - 200, 100);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            debugMode = !debugMode;
        }

        float touchX = -1;
        float moveDirection = 0;

        if (Gdx.input.isTouched()) {
            touchX = Gdx.input.getX();
        }

        if (touchX > 0) {
            moveDirection = (touchX > Gdx.graphics.getWidth() / 2f) ? -1 : 1;
        }

        kitchenMap.update(delta, moveDirection);

        for (Coin coin : coins) {
            coin.update(delta);

            // Movemos as moedas no sentido oposto ao do jogador
            coin.x += moveDirection * speed * 0.9f * delta;

            // Reposiciona a moeda coletada APENAS quando o jogador anda para frente
            if (coin.isCollected(player)) {
                float maxX = getMaxCoinX();
                coin.x = maxX + COIN_SPACING;
//                System.out.println("Moeda coletada pelo jogador.");
            }
        }

        player.update(delta, moveDirection);

        batch.begin();
        kitchenMap.draw(batch);

        for (Coin coin : coins) {
            coin.draw(batch);
        }

        player.draw(batch);
        batch.end();

        if (debugMode) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.rect(player.getBounds().x, player.getBounds().y, player.getBounds().width, player.getBounds().height);

            shapeRenderer.setColor(0, 0, 1, 1);
            for (Coin coin : coins) {
                shapeRenderer.rect(coin.getBounds().x, coin.getBounds().y, coin.getBounds().width, coin.getBounds().height);
            }

            shapeRenderer.end();
        }
    }

    private float getMaxCoinX() {
        float maxX = 0;
        for (Coin coin : coins) {
            if (coin.x > maxX) {
                maxX = coin.x;
            }
        }
        return maxX;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        kitchenMap.dispose();
        player.dispose();
        shapeRenderer.dispose();
        for (Coin coin : coins) {
            coin.dispose();
        }
    }
}
