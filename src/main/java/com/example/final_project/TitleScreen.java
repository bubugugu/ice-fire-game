package com.example.final_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class TitleScreen {

    private double time = 0;

    public boolean handleKey(KeyCode code) {
        return code == KeyCode.ENTER || code == KeyCode.SPACE;
    }

    // 回傳 true 代表雙擊，要進入選關
    public boolean handleMouseClick(double x, double y, int clickCount) {
        return clickCount == 2;
    }

    public void update(double dt) {
        time += dt;
    }

    public void draw(GraphicsContext gc, int w, int h) {
        // 背景
        gc.setFill(Color.web("#0a0a0f"));
        gc.fillRect(0, 0, w, h);
        gc.setFill(Color.web("#1a0500"));
        gc.fillRect(0, 0, w / 2.0, h);
        gc.setFill(Color.web("#00060f"));
        gc.fillRect(w / 2.0, 0, w / 2.0, h);

        // 光暈
        gc.setFill(Color.web("#ff440020"));
        gc.fillOval(w * 0.05, h * 0.45, 220, 220);
        gc.setFill(Color.web("#0055ff20"));
        gc.fillOval(w * 0.55, h * 0.45, 220, 220);

        // 標題飄動
        double bob = Math.sin(time * 2) * 6;
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFont(Font.font("Georgia", 68));
        gc.setFill(Color.web("#ff6b2b"));
        gc.fillText("火", w / 2.0 - 90, 185 + bob);
        gc.setFill(Color.web("#888888"));
        gc.fillText("&", w / 2.0, 185 + bob);
        gc.setFill(Color.web("#40b8ff"));
        gc.fillText("冰", w / 2.0 + 90, 185 + bob);

        // 副標題（改亮）
        gc.setFont(Font.font("monospace", 13));
        gc.setFill(Color.web("#aaaaaa"));
        gc.fillText("冒險  ·  adventure", w / 2.0, 225);

        // 開始按鈕（閃爍）
        double alpha = 0.75 + 0.25 * Math.sin(time * 3);
        gc.setFill(Color.color(0.88, 0.31, 0.16, alpha));
        gc.fillRoundRect(w / 2.0 - 120, 268, 240, 58, 12, 12);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("monospace", 20));
        gc.fillText("▶  開始遊戲", w / 2.0, 305);

        // 提示文字（改亮）
        gc.setFont(Font.font("monospace", 12));
        gc.setFill(Color.web("#aaaaaa"));
        gc.fillText("按 Enter / Space 或雙擊開始", w / 2.0, 368);

        gc.setFill(Color.web("#999999"));
        gc.fillText("火男孩 WASD  ·  水女孩 方向鍵  ·  ESC 回選單", w / 2.0, 392);
    }
}