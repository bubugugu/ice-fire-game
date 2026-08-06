package com.example.final_project.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Elevator {
    public double x;
    public double y;           // 目前 Y 位置（像素）
    public final double w = 64, h = 12;

    private final double topY;    // 升到最高點的 Y
    private final double bottomY; // 起始最低點的 Y
    private boolean activated = false;

    public Elevator(double x, double bottomY, double topY) {
        this.x       = x;
        this.y       = bottomY;
        this.bottomY = bottomY;
        this.topY    = topY;
    }

    // 🌟 改變狀態：傳入 true 就上升，false 就下降
    public void setActivated(boolean state) {
        this.activated = state;
    }

    public void update(double dt) {
        if (activated) {

            if (y > topY) {
                y -= 80 * dt;
                if (y < topY) y = topY;
            }
        } else {

            if (y < bottomY) {
                y += 80 * dt;
                if (y > bottomY) y = bottomY;
            }
        }
    }

    public void draw(GraphicsContext gc) {
        // 升降梯平台
        gc.setFill(Color.web("#c0932a"));
        gc.fillRect(x, y, w, h);
        // 表面線條（讓它看起來像平台）
        gc.setFill(Color.web("#e8b84b"));
        gc.fillRect(x, y, w, 3);
        // 軌道（細線）
        gc.setStroke(Color.web("#555555"));
        gc.setLineWidth(2);
        gc.strokeLine(x + w / 2, y + h, x + w / 2, bottomY + h);
    }
}