package com.example.final_project.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Button {
    public double x, y;
    public final double w = 24, h = 8;
    public boolean pressed = false;

    public Button(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void draw(GraphicsContext gc) {
        // 底座
        gc.setFill(Color.web("#555555"));
        gc.fillRect(x - 4, y + h, w + 8, 5);
        // 按鈕本體（未按=綠，已按=灰）
        gc.setFill(pressed ? Color.web("#888888") : Color.web("#44dd44"));
        gc.fillRoundRect(x, y, w, h, 4, 4);
        // 文字
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("monospace", 7));
        gc.fillText(pressed ? "ON" : "OFF", x + 5, y + 6);
    }
}
