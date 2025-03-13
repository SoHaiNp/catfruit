package com.retronimia.catfruit.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class KitchenMap {
    private Texture background, midground, foreground;
    private float bgX, mgX, fgX;
    private float speed;
    private static final int VIRTUAL_WIDTH = 1920;
    private static final int VIRTUAL_HEIGHT = 1080;

    public KitchenMap(float speed) {
        this.speed = speed;
        this.background = new Texture(Gdx.files.internal("ceu.png"));
        this.midground = new Texture(Gdx.files.internal("plano_de_fundo.png"));
        this.foreground = new Texture(Gdx.files.internal("mesa.png"));
        this.bgX = this.mgX = this.fgX = 0;
    }

    public void update(float delta, float moveDirection) {
        if (moveDirection != 0) {
            bgX += moveDirection * speed * 0.1f * delta;
            mgX += moveDirection * speed * 0.15f * delta;
            fgX += moveDirection * speed * 0.9f * delta;
        }

        // Loop infinito do mapa
        if (moveDirection == -1) {
            if (bgX <= -VIRTUAL_WIDTH) bgX += VIRTUAL_WIDTH;
            if (mgX <= -VIRTUAL_WIDTH) mgX += VIRTUAL_WIDTH;
            if (fgX <= -VIRTUAL_WIDTH) fgX += VIRTUAL_WIDTH;
        } else if (moveDirection == 1) {
            if (bgX >= 0) bgX -= VIRTUAL_WIDTH;
            if (mgX >= 0) mgX -= VIRTUAL_WIDTH;
            if (fgX >= 0) fgX -= VIRTUAL_WIDTH;
        }
    }

    public void drawBackground(SpriteBatch batch) {
        batch.draw(background, bgX, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.draw(background, bgX + VIRTUAL_WIDTH, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    }

    public void drawMidground(SpriteBatch batch) {
        batch.draw(midground, mgX, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.draw(midground, mgX + VIRTUAL_WIDTH, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    }

    public void drawForeground(SpriteBatch batch) {
        batch.draw(foreground, fgX, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.draw(foreground, fgX + VIRTUAL_WIDTH, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    }

    public void dispose() {
        background.dispose();
        midground.dispose();
        foreground.dispose();
    }
}
