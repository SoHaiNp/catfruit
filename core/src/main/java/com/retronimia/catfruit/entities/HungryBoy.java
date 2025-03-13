package com.retronimia.catfruit.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class HungryBoy {
    private Texture texture;
    private float x, y;
    private float hiddenY;  // Posição inicial abaixo do topo
    private float spyY;     // Posição quando espiando
    private float speed = 200f; // Velocidade de movimento
    private boolean spying = false; // Controla se o garoto está visível
    private float spyTimer = 0; // Tempo que ele fica visível
    private float nextSpyTime; // Tempo até a próxima aparição
    private Random random;

    private static final float SPY_DURATION = 2f; // Tempo que ele fica visível
    private static final float MIN_WAIT_TIME = 3f; // Tempo mínimo para reaparecer
    private static final float MAX_WAIT_TIME = 7f; // Tempo máximo para reaparecer

    public HungryBoy(float x, float hiddenY, float spyY) {
        this.texture = new Texture(Gdx.files.internal("menino_com_fome.png")); // Substituir pelo caminho real da textura
        this.x = x;
        this.hiddenY = hiddenY;
        this.spyY = spyY;
        this.y = hiddenY; // Começa escondido
        this.random = new Random();
        this.nextSpyTime = getRandomTime();
    }

    public void update(float delta) {
        if (spying) {
            spyTimer += delta;
            if (spyTimer >= SPY_DURATION) {
                spying = false;
                spyTimer = 0;
                nextSpyTime = getRandomTime();
            }
        } else {
            nextSpyTime -= delta;
            if (nextSpyTime <= 0) {
                spying = true;
            }
        }

        // Controle da posição (subir quando espiando, descer quando escondido)
        if (spying && y < spyY) {
            y += speed * delta;
            if (y > spyY) y = spyY;
        } else if (!spying && y > hiddenY) {
            y -= speed * delta;
            if (y < hiddenY) y = hiddenY;
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    private float getRandomTime() {
        return MIN_WAIT_TIME + random.nextFloat() * (MAX_WAIT_TIME - MIN_WAIT_TIME);
    }

    public void dispose() {
        texture.dispose();
    }
}
