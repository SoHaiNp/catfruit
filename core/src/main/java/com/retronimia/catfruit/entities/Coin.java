package com.retronimia.catfruit.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Coin {

    private Texture texture;
    public float x, y;
    private float baseY;
    private float scaleX = 1f;
    private float time = 0;

    public static final float WIDTH = 134;
    public static final float HEIGHT = 134;
    public static final float OSCILLATION_AMPLITUDE = 6;
    public static final float OSCILLATION_SPEED = 3;
    public static final float SCALE_VARIATION = 0.05f;

    private Rectangle bounds;

    public Coin(float x, float y) {
        this.texture = new Texture(Gdx.files.internal("moeda.png"));
        this.x = x;
        this.y = y;
        this.baseY = y;
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void update(float delta) {
        time += delta * OSCILLATION_SPEED;

        y = baseY + (float) Math.sin(time) * OSCILLATION_AMPLITUDE;

        scaleX = 1f + (float) Math.sin(time) * SCALE_VARIATION;

        bounds.setPosition(x, y);
    }

    public void draw(SpriteBatch batch) {
        float scaledWidth = WIDTH * scaleX;
        float offsetX = (WIDTH - scaledWidth) / 2;

        batch.draw(texture, x + offsetX, y, scaledWidth, HEIGHT);
    }

    public boolean isCollected(Player player) {
        return bounds.overlaps(player.getBounds());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
