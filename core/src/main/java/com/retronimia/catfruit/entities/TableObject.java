package com.retronimia.catfruit.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class TableObject {

    public enum Type {
        MILK_BOX, JUICE_GLASS, EMPTY_GLASS
    }

    private Texture texture;
    private float x, y;
    private Rectangle bounds;
    public static final float WIDTH = 128;
    public static final float HEIGHT = 128;
    private static final float OBJECT_Y_POSITION = 135; // Posição fixa na mesa
    private Type type;

    public TableObject(float x, Type type) {
        this.x = x;
        this.y = OBJECT_Y_POSITION;
        this.type = type;

        switch (type) {
            case MILK_BOX:
                this.texture = new Texture(Gdx.files.internal("caixa_de_leite.png"));
                break;
            case JUICE_GLASS:
                this.texture = new Texture(Gdx.files.internal("copo_cheio.png"));
                break;
            case EMPTY_GLASS:
                this.texture = new Texture(Gdx.files.internal("copo_vazio.png"));
                break;
        }

        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta, float moveDirection, float speed) {
        x += moveDirection * speed * 0.9f * delta;
        bounds.setPosition(x, y);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, texture.getWidth(), texture.getHeight());
    }

    public Type getType() {
        return type;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isOutOfBounds(float limit) {
        return x < -limit; // Remove o objeto quando ultrapassa `VIRTUAL_WIDTH * 2`
    }

    public void dispose() {
        texture.dispose();
    }
}
