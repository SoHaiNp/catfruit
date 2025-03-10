package com.retronimia.catfruit.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.retronimia.catfruit.screens.GameScreen;

public class Player {
    private Texture texture;
    private TextureRegion textureRegion;
    private float x, y;
    private float originalX, originalY;
    private float scaleX, scaleY;
    private boolean facingRight;
    private float oscillation;
    private float time;
    private float rotation;
    private float alpha = 1.0f;

    public static final float WIDTH = 326;
    public static final float HEIGHT = 326;

    private static final float OSCILLATION_AMPLITUDE = 5;
    private static final float SCALE_VARIATION = 0.02f;
    private static final float ROTATION_AMPLITUDE = 1f;
    private static final float OSCILLATION_SPEED = 8;

    private Rectangle bounds;

    private boolean isHidden = false;
    private float targetX, targetY;
    private float lerpSpeed = 6f;

    public Player(float x, float y) {
        this.texture = new Texture("gato_morango.png");
        this.textureRegion = new TextureRegion(texture);
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;
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

        float targetAlpha = isHidden ? 0.7f : 1.0f;
        alpha += (targetAlpha - alpha) * 5f * delta;

        if (isHidden) {
            x += (targetX - x) * lerpSpeed * delta;
            y += (targetY - y) * lerpSpeed * delta;
        } else {
            y += (originalY - y) * lerpSpeed * delta;
        }

        // Atualiza posição do retângulo de colisão
        bounds.setPosition(x, y + oscillation);
    }

    public void draw(SpriteBatch batch) {
        batch.setColor(1, 1, 1, alpha);
        if (facingRight) {
            batch.draw(textureRegion, x, y + oscillation, texture.getWidth() / 2f, texture.getHeight() / 2f,
                texture.getWidth(), texture.getHeight(), scaleX, scaleY, rotation);
        } else {
            batch.draw(textureRegion, x, y + oscillation, texture.getWidth() / 2f, texture.getHeight() / 2f,
                texture.getWidth(), texture.getHeight(), -scaleX, scaleY, -rotation);
        }
        batch.setColor(1, 1, 1, 1);
    }

    public void toggleHide(float objectX, float objectY) {
        isHidden = !isHidden;

        if (isHidden) {
            targetX = objectX;
            targetY = objectY; // Move para a posição do objeto
        } else {
            targetX = objectX; // Retorna à posição original ao sair do esconderijo
            targetY = originalY;
        }

        System.out.println("Jogador escondido? " + isHidden + " | targetX: " + targetX + " | targetY: " + targetY);
    }

    public boolean isHidden() {
        return isHidden;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getOriginalX() {
        return originalX;
    }

    public float getOriginalY() {
        return originalY;
    }

    public void dispose() {
        texture.dispose();
    }
}
