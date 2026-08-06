package com.example.final_project;

import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

public class InputHandler {

    private final Set<KeyCode> pressed = new HashSet<>();

    public void keyPressed(KeyCode code)  { pressed.add(code); }
    public void keyReleased(KeyCode code) { pressed.remove(code); }
    public boolean isPressed(KeyCode code){ return pressed.contains(code); }

    public boolean fireLeft()  { return isPressed(KeyCode.A); }
    public boolean fireRight() { return isPressed(KeyCode.D); }
    public boolean fireJump()  { return isPressed(KeyCode.W); }

    public boolean waterLeft()  { return isPressed(KeyCode.LEFT); }
    public boolean waterRight() { return isPressed(KeyCode.RIGHT); }
    public boolean waterJump()  { return isPressed(KeyCode.UP); }
}