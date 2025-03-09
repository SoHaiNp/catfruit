package com.retronimia.catfruit.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Texture texture;
    private TextureRegion textureRegion;
    private float x, y;
    private float scaleX, scaleY;
    private boolean facingRight;
    private float oscillation;
    private float time;
    private float rotation;

    public static final float WIDTH = 326;
    public static final float HEIGHT = 326;

    private static final float OSCILLATION_AMPLITUDE = 5;
    private static final float SCALE_VARIATION = 0.02f;
    private static final float ROTATION_AMPLITUDE = 1f;
    private static final float OSCILLATION_SPEED = 8;

    private Rectangle bounds;

    public Player(float x, float y) {
        this.texture = new Texture("gato_morango.png");
        this.textureRegion = new TextureRegion(texture);
        this.x = x;
        this.y = y;
        this.scaleX = 1f;
        this.scaleY = 1f;
        this.facingRight = true;
        this.oscillation = 0;
        this.time = 0;
        this.rotation = 0f;
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void update(float delta, float moveDirection) {
        if (moveDirection != 0) {
            time += delta * OSCILLATION_SPEED; // Só anima se estiver se movendo

            // Oscilação "up and down"
            oscillation = (float) Math.sin(time) * OSCILLATION_AMPLITUDE;

            // Expansão e contração
            scaleX = 1f + (float) Math.sin(time) * SCALE_VARIATION;
            scaleY = 1f + (float) Math.sin(time) * SCALE_VARIATION;

            // Rotação suave
            rotation = (float) Math.sin(time) * ROTATION_AMPLITUDE;

            // Atualiza direção
            facingRight = moveDirection < 0;
        } else {
            // Reseta animação quando parado
            oscillation = 0;
            scaleX = 1f;
            scaleY = 1f;
            rotation = 0f;
        }

        // Atualiza posição do retângulo de colisão
        bounds.setPosition(x, y + oscillation);
    }

    public void draw(SpriteBatch batch) {
        if (facingRight) {
            batch.draw(textureRegion, x, y + oscillation, texture.getWidth() / 2f, texture.getHeight() / 2f,
                texture.getWidth(), texture.getHeight(), scaleX, scaleY, rotation);
        } else {
            batch.draw(textureRegion, x, y + oscillation, texture.getWidth() / 2f, texture.getHeight() / 2f,
                texture.getWidth(), texture.getHeight(), -scaleX, scaleY, -rotation);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
