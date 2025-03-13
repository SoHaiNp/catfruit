package com.retronimia.catfruit.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class HungryBoy {
    public enum State { ESPIAR, CAPTURAR, RETREAT }

    private State state;

    // Textura e posição do HungryBoy
    private Texture texture;
    private float x, y;
    private float initialY, targetY;
    private float progress; // controla o movimento vertical via Lerp
    private float speed;    // velocidade de subida/descida

    // Timer para o tempo que ele fica espiando
    private float visibleTimer;

    // Textura e variáveis para a mão (usada no estado CAPTURAR)
    private Texture handTexture;
    private float handX, handY;         // Posição atual da mão
    private float captureTimer;         // Tempo de animação da mão
    private float handWidth, handHeight;

    // Timer adicional para delay pós tentativa de captura
    private float postCaptureDelayTimer;

    // Variáveis para efeito de fade-out se o jogador se esconder
    private float handFadeOutTimer;      // Inicia quando o jogador se esconde durante CAPTURAR
    private final float handFadeOutDuration = 1.5f;  // Duração do fade-out

    // Textura para feedback visual: mãos na mesa
    private Texture tableHandsTexture;

    // Controle de atividade
    private boolean active;

    public HungryBoy(float initialY, float targetY, float speed) {
        this.initialY = initialY;
        this.targetY = targetY;
        this.speed = speed;
        this.progress = 0;
        this.visibleTimer = 0;
        this.state = State.ESPIAR;
        this.active = false;
        this.captureTimer = 0;
        this.postCaptureDelayTimer = 0;
        this.handFadeOutTimer = 0;

        texture = new Texture(Gdx.files.internal("menino_com_fome.png"));
        handTexture = new Texture(Gdx.files.internal("mao.png"));
        handWidth = handTexture.getWidth();
        handHeight = handTexture.getHeight();

        tableHandsTexture = new Texture(Gdx.files.internal("maos_na_mesa.png"));
    }

    public void activate(float screenWidth) {
        active = true;
        state = State.ESPIAR;
        progress = 0;
        visibleTimer = 0;
        captureTimer = 0;
        postCaptureDelayTimer = 0;
        handFadeOutTimer = 0;
        x = MathUtils.random(0, screenWidth - texture.getWidth());
        y = initialY;
    }

    /**
     * Atualiza o estado do HungryBoy.
     *
     * @param delta  Tempo decorrido desde o último frame.
     * @param player O objeto Player, para verificar se ele está escondido e obter sua posição.
     * @param virtualWidth O valor de VIRTUAL_WIDTH para comparação de distância.
     */
    public void update(float delta, Player player, float virtualWidth) {
        if (!active) return;

        // Checa se a distância horizontal entre os centros do jogador e do HungryBoy excede virtualWidth.
        float playerCenterX = player.getBounds().x + player.getBounds().width / 2f;
        float hungryBoyCenterX = x + texture.getWidth() / 2f;
        if (Math.abs(playerCenterX - hungryBoyCenterX) >= virtualWidth) {
            state = State.RETREAT;
        }

        if (state == State.ESPIAR) {
            if (progress < 1) {
                progress += speed * delta;
                if (progress >= 1) {
                    progress = 1;
                    visibleTimer = 0;
                }
            } else {
                visibleTimer += delta;
                if (visibleTimer >= 4f) {
                    if (!player.isHidden()) {
                        state = State.CAPTURAR;
                        captureTimer = 0;
                        postCaptureDelayTimer = 0;
                        handFadeOutTimer = 0;
                    } else {
                        state = State.RETREAT;
                    }
                }
            }
        } else if (state == State.CAPTURAR) {
            captureTimer += delta;
            // Se o jogador se esconder, inicia o fade-out timer.
            if (player.isHidden()) {
                handFadeOutTimer += delta;
            } else {
                handFadeOutTimer = 0;
            }
            // Durante os primeiros 1.5 segundos, a animação de captura não inicia.
            if (captureTimer < 1.5f) {
                handX = x;
                handY = y;
            } else if (captureTimer < 3.0f && handFadeOutTimer == 0) {
                // Animação normal: a mão se move em direção ao jogador.
                float handProgress = MathUtils.clamp((captureTimer - 1.5f) / 1.5f, 0, 1);
                handX = MathUtils.lerp(x, player.getBounds().x + player.getBounds().width / 2 - handWidth / 2, handProgress);
                handY = MathUtils.lerp(y, player.getBounds().y + player.getBounds().height / 2 - handHeight / 2, handProgress);
            } else if (captureTimer >= 3.0f && handFadeOutTimer == 0) {
                Rectangle handRect = new Rectangle(handX, handY, handWidth, handHeight);
                if (handRect.overlaps(player.getBounds()) && !player.isHidden()) {
                    System.out.println("Jogador Capturado!");
                    // Aqui pode ser disparado o game over.
                } else {
                    postCaptureDelayTimer += delta;
                    if (postCaptureDelayTimer >= 1.5f) {
                        state = State.RETREAT;
                    }
                }
            }
            // Se o fade-out foi iniciado (jogador se escondeu enquanto as mãos na mesa estavam visíveis)
            if (handFadeOutTimer > 0) {
                if (handFadeOutTimer >= handFadeOutDuration) {
                    state = State.RETREAT;
                }
            }
        } else if (state == State.RETREAT) {
            progress -= speed * delta;
            if (progress <= 0) {
                progress = 0;
                active = false;
            }
        }
        y = MathUtils.lerp(initialY, targetY, progress);
    }

    public void updateX(float deltaX) {
        if (!active) return;
        x += deltaX;
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;
        batch.draw(texture, x, y);
    }

    /**
     * Desenha a mão de captura com efeito de fade-in e fade-out.
     * Se o handFadeOutTimer estiver ativo, nada é desenhado (para evitar o fade-out visível).
     */
    public void drawHand(SpriteBatch batch) {
        if (!active) return;
        if (state == State.CAPTURAR) {
            // Se o fade-out foi iniciado, não desenha a mão.
            if (handFadeOutTimer > 0) {
                return;
            }
            if (captureTimer < 1.5f) {
                // Ainda não inicia a animação da mão.
                return;
            }
            float alpha;
            if (captureTimer < 3.0f) {
                float fadeInDuration = 0.5f;
                alpha = MathUtils.clamp((captureTimer - 1.5f) / fadeInDuration, 0, 1);
            } else {
                alpha = 1;
            }
            batch.setColor(1, 1, 1, alpha);
            batch.draw(handTexture, handX, handY);
            batch.setColor(1, 1, 1, 1);
        }
    }

    /**
     * Desenha o feedback visual das mãos na mesa com base no centro do HungryBoy para o eixo X,
     * utilizando um valor manual para o eixo Y.
     * Esse feedback é desenhado somente se não houver fade-out iniciado (handFadeOutTimer == 0)
     * e enquanto captureTimer < 1.5s.
     */
    public void drawTableHands(SpriteBatch batch, float customY) {
        if (!active) return;
        if (state == State.CAPTURAR && captureTimer < 1.5f && handFadeOutTimer == 0) {
            float fadeInDuration = 0.5f;
            float alpha = (captureTimer < fadeInDuration) ? MathUtils.clamp(captureTimer / fadeInDuration, 0, 1) : 1f;
            batch.setColor(1, 1, 1, alpha);
            float centerX = x + texture.getWidth() / 2f;
            float tableHandsX = centerX - tableHandsTexture.getWidth() / 2f;
            batch.draw(tableHandsTexture, tableHandsX, customY);
            batch.setColor(1, 1, 1, 1);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public void dispose() {
        texture.dispose();
        handTexture.dispose();
        tableHandsTexture.dispose();
    }
}
