package com.example.final_project.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Door {

    public final double x, y, w, h;
    public boolean open = false;
    private final boolean isFireDoor;

    public Door(double x, double y, boolean isFireDoor) {
        this.x = x;
        this.y = y;
        this.w = 26;
        this.h = 58;
        this.isFireDoor = isFireDoor;
    }

    public void draw(GraphicsContext gc) {
        // 門框
        if (open) {
            gc.setFill(isFireDoor ? Color.web("#ff6b35") : Color.web("#4fc3f7"));
        } else {
            gc.setFill(Color.web("#444455"));
        }
        gc.fillRect(x, y, w, h);

        // 門窗
        gc.setFill(open
                ? Color.rgb(255, 255, 255, 0.2)
                : Color.rgb(255, 255, 255, 0.07));
        gc.fillRect(x + 3, y + 5, w - 6, h * 0.55);

        // 門框邊線
        gc.setStroke(open
                ? (isFireDoor ? Color.web("#ff6b35") : Color.web("#4fc3f7"))
                : Color.web("#555566"));
        gc.setLineWidth(2);
        gc.strokeRect(x + 1, y + 1, w - 2, h - 2);

        // 文字
        gc.setFill(open ? Color.WHITE : Color.rgb(255, 255, 255, 0.25));
        gc.setFont(javafx.scene.text.Font.font(8));
        gc.fillText(open ? "OPEN" : "LOCK", x + 4, y + h * 0.52);
    }
}