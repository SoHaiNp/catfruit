package com.retronimia.catfruit.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class HungryBoy {
    private Texture texture;
    private float x, y;
    private float initialY, targetY;
    private float progress;
    private float speed;
    private boolean active;
    private boolean retreating;
    private float visibleTimer;
    private float stayDuration;

    /**
     * Construtor para HungryBoy.
     *
     * @param initialY    Posição Y inicial (fora da tela, abaixo da mesa)
     * @param targetY     Posição Y alvo (onde ele espiará a mesa)
     * @param speed       Velocidade da transição vertical (usada no Lerp)
     * @param stayDuration Tempo que ele permanece espiando antes de recuar (em segundos)
     */
    public HungryBoy(float initialY, float targetY, float speed, float stayDuration) {
        this.initialY = initialY;
        this.targetY = targetY;
        this.speed = speed;
        this.stayDuration = stayDuration;
        this.progress = 0;
        this.active = false;
        this.retreating = false;
        this.visibleTimer = 0;
        this.texture = new Texture(Gdx.files.internal("menino_com_fome.png"));
    }

    /**
     * Ativa o HungryBoy, definindo sua posição X aleatória dentro da área visível.
     *
     * @param screenWidth Largura da tela em unidades do mundo (ex: VIRTUAL_WIDTH)
     */
    public void activate(float screenWidth) {
        active = true;
        retreating = false;
        progress = 0;
        visibleTimer = 0;
        // Define a posição X aleatória dentro do intervalo da tela
        x = MathUtils.random(0, screenWidth - texture.getWidth());
        y = initialY;
    }

    /**
     * Atualiza a posição vertical (Y) do HungryBoy utilizando Lerp.
     * Primeiro ele sobe (de 0 a 1) e, após o tempo de permanência,
     * ele recua (de 1 a 0) e desativa-se ao final do ciclo.
     *
     * @param delta Tempo decorrido desde o último frame.
     */
    public void update(float delta) {
        if (!active) return;

        if (!retreating) {
            if (progress < 1) {
                progress += speed * delta;
                if (progress >= 1) {
                    progress = 1;
                    visibleTimer = 0;
                }
            } else {
                visibleTimer += delta;
                if (visibleTimer >= stayDuration) {
                    retreating = true;
                }
            }
        } else {
            progress -= speed * delta;
            if (progress <= 0) {
                progress = 0;
                active = false;
            }
        }
        y = MathUtils.lerp(initialY, targetY, progress);
    }

    /**
     * Atualiza a posição horizontal (X) para que o HungryBoy se mova
     * junto com o cenário.
     *
     * @param deltaX Valor a ser somado ao X (por exemplo, moveDirection * speed * delta)
     */
    public void updateX(float deltaX) {
        if (!active) return;
        x += deltaX;
    }

    /**
     * Renderiza o HungryBoy na tela.
     *
     * @param batch SpriteBatch para desenhar a textura.
     */
    public void draw(SpriteBatch batch) {
        if (!active) return;
        batch.draw(texture, x, y);
    }

    public boolean isActive() {
        return active;
    }

    public void dispose() {
        texture.dispose();
    }
}
