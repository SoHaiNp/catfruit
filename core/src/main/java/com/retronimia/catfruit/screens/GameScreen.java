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
import com.retronimia.catfruit.entities.TableObject;

import java.util.ArrayList;
import java.util.Random;

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

    private ArrayList<TableObject> tableObjects;
    private static final float DISTANCE_BETWEEN_OBJECTS = 1500; // Espaçamento mínimo entre objetos
    private static final float OBJECT_REMOVAL_LIMIT = VIRTUAL_WIDTH; // Limite para remover objetos
    private float lastObjectX = 0;
    private Random random;

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
        random = new Random();

        kitchenMap = new KitchenMap(speed);

        coins = new ArrayList<>();
        for (int i = 0; i < COIN_COUNT; i++) {
            float x = i * COIN_SPACING + VIRTUAL_WIDTH;
            coins.add(new Coin(x, COIN_Y_POSITION));
        }

        tableObjects = new ArrayList<>();
        player = new Player(VIRTUAL_WIDTH / 2f - Player.WIDTH / 2f - 200, 100);

        // Gera objetos iniciais na mesa antes do jogador se mover
        float startX = player.getBounds().x + 200; // Começa à frente do jogador
        for (int i = 0; i < 3; i++) { // Define quantos objetos iniciais queremos
            TableObject.Type type = TableObject.Type.values()[random.nextInt(TableObject.Type.values().length)];
            tableObjects.add(new TableObject(startX + i * DISTANCE_BETWEEN_OBJECTS, type));
        }
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
        float moveDirectionRaw = 0;

        if (Gdx.input.isTouched()) {
            touchX = Gdx.input.getX();
        }

        if (touchX > 0) {
            moveDirectionRaw = (touchX > Gdx.graphics.getWidth() / 2f) ? -1 : 1;
        }

        final float moveDirection = moveDirectionRaw; // Criamos uma variável final para usar na lambda

        kitchenMap.update(delta, moveDirection);

        for (Coin coin : coins) {
            coin.update(delta);

            // Movemos as moedas no sentido oposto ao do jogador
            coin.x += moveDirection * speed * 0.9f * delta;

            // Reposiciona a moeda coletada APENAS quando o jogador anda para frente
            if (coin.isCollected(player)) {
                float maxX = getMaxCoinX();
                coin.x = maxX + COIN_SPACING;
            }
        }

        if (moveDirection == -1) { // Agora só gera ao andar para frente
            float spawnX = getMaxObjectX() + DISTANCE_BETWEEN_OBJECTS; // Garante que o próximo spawn esteja sempre adiante

            if (spawnX < player.getBounds().x + VIRTUAL_WIDTH) { // Só gera objetos se estiver longe o suficiente
                // Seleciona um tipo de objeto aleatoriamente
                TableObject.Type type = TableObject.Type.values()[random.nextInt(TableObject.Type.values().length)];

                // Cria e adiciona o objeto na mesa
                tableObjects.add(new TableObject(spawnX, type));
            }
        }

        // Atualizar posição dos objetos e remover os que ultrapassaram o limite
        tableObjects.removeIf(obj -> {
            obj.update(delta, moveDirection, speed);
            return obj.isOutOfBounds(OBJECT_REMOVAL_LIMIT);
        });

        player.update(delta, moveDirection);

        batch.begin();
        kitchenMap.draw(batch);

        for (TableObject obj : tableObjects) {
            obj.draw(batch);
        }

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

            shapeRenderer.setColor(0, 1, 0, 1);
            for (TableObject obj : tableObjects) {
                shapeRenderer.rect(obj.getBounds().x, obj.getBounds().y, obj.getBounds().width, obj.getBounds().height);
            }

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

    private float getMaxObjectX() {
        float maxX = 0;
        for (TableObject obj : tableObjects) {
            if (obj.getBounds().x > maxX) {
                maxX = obj.getBounds().x;
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
        for (TableObject obj : tableObjects) {
            obj.dispose();
        }
    }
}
