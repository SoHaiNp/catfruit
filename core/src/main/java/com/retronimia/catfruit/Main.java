package com.retronimia.catfruit;

import com.badlogic.gdx.Game;
import com.retronimia.catfruit.screens.GameScreen;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
