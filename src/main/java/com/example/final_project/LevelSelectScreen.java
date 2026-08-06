package com.example.final_project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class LevelSelectScreen {

    private int selected = -1;
    private static final int TOTAL = 10;

    private static final int CARD_W = 110;
    private static final int CARD_H = 140;
    private static final int CARD_GAP = 20;
    private static final int TRACK_W = TOTAL * (CARD_W + CARD_GAP);

    private double offsetX = 0;
    private double targetOffsetX = 0;
    private double dragStartX = 0;
    private double dragOffsetStart = 0;
    private boolean dragging = false;

    private double time = 0;

    // 滑鼠事件（由 GameApp 呼叫）
    public void onMousePressed(double x, double y) {
        dragStartX = x;
        dragOffsetStart = offsetX;
        dragging = true;
    }

    public void onMouseDragged(double x, double y) {
        if (!dragging) return;
        offsetX = dragOffsetStart + (x - dragStartX);
        clampOffset();
    }

    public void onMouseReleased(double x, double y, int screenW) {
        dragging = false;
        // 點擊（拖曳距離很小）才算選擇
        if (Math.abs(x - dragStartX) < 8) {
            int cardAreaX = (screenW - Math.min(TOTAL * (CARD_W + CARD_GAP), screenW)) / 2;
            for (int i = 0; i < TOTAL; i++) {
                double cx = cardAreaX + i * (CARD_W + CARD_GAP) + offsetX;
                double cy = 160;
                if (x >= cx && x <= cx + CARD_W && y >= cy && y <= cy + CARD_H) {
                    if (i < 9) selected = i;
                    break;
                }
            }
        }
        snapToNearest(screenW);
    }

    private void clampOffset() {
        double maxOffset = 0;
        double minOffset = -(TRACK_W - 640 + CARD_GAP);
        if (minOffset > 0) minOffset = 0;
        offsetX = Math.max(minOffset, Math.min(maxOffset, offsetX));
    }

    private void snapToNearest(int screenW) {
        // 平滑靠齊最近的卡片
        int nearest = (int) Math.round(-offsetX / (CARD_W + CARD_GAP));
        nearest = Math.max(0, Math.min(TOTAL - 1, nearest));
        targetOffsetX = -nearest * (CARD_W + CARD_GAP);
        clampOffset();
    }

    public void update(double dt) {
        time += dt;
        // 平滑滑動
        offsetX += (targetOffsetX - offsetX) * 0.15;
    }

    public int getSelected() { return selected; }

    public void handleKey(KeyCode code) {
        if (selected == -1) selected = 0;
        switch (code) {
            case RIGHT -> selected = Math.min(selected + 1, 8);
            case LEFT  -> selected = Math.max(selected - 1, 0);
            default -> {}
        }
        // 鍵盤選擇時滾動到對應卡片
        targetOffsetX = -selected * (CARD_W + CARD_GAP);
        clampOffset();
    }

    public boolean confirm(KeyCode code) {
        return code == KeyCode.ENTER && selected >= 0 && selected < 9;
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
        gc.setFill(Color.web("#ff440012"));
        gc.fillOval(w * 0.1, h * 0.4, 220, 220);
        gc.setFill(Color.web("#0055ff12"));
        gc.fillOval(w * 0.55, h * 0.4, 220, 220);

        // 標題
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.web("#ffffff"));
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        gc.fillText("選擇關卡", w / 2.0, 70);

        // 副標題
        gc.setFill(Color.web("#aaaaaa"));
        gc.setFont(Font.font("monospace", 11));
        gc.fillText("拖曳瀏覽  ·  點擊選擇  ·  Enter 進入  ·  ESC 返回", w / 2.0, 95);

        // 卡片軌道起始 X
        double startX = CARD_GAP + offsetX;
        double cardsY = 155;

        // 裁切區域（讓卡片不超出畫面）
        gc.save();
        gc.beginPath();
        gc.rect(0, cardsY - 10, w, CARD_H + 30);
        gc.clip();

        for (int i = 0; i < TOTAL; i++) {
            double cx = startX + i * (CARD_W + CARD_GAP);
            double cy = cardsY;

            boolean isSel = (i == selected);
            boolean isLocked = (i == 9);

            // 卡片浮起動畫
            double bob = isSel ? Math.sin(time * 3) * 4 - 6 : 0;
            cy += bob;

            // 卡片背景
            if (isLocked) {
                gc.setFill(Color.web("#1a1a2a"));
            } else if (isSel) {
                gc.setFill(i % 2 == 0 ? Color.web("#2a1505") : Color.web("#051525"));
            } else {
                gc.setFill(i % 2 == 0 ? Color.web("#1a0e05") : Color.web("#050f1a"));
            }
            gc.fillRoundRect(cx, cy, CARD_W, CARD_H, 12, 12);

            // 卡片邊框
            if (isSel) {
                gc.setStroke(i % 2 == 0 ? Color.web("#ff7040") : Color.web("#40b8ff"));
                gc.setLineWidth(2.5);
            } else {
                gc.setStroke(Color.web("#ffffff18"));
                gc.setLineWidth(1);
            }
            gc.strokeRoundRect(cx, cy, CARD_W, CARD_H, 12, 12);

            gc.setTextAlign(TextAlignment.CENTER);
            if (isLocked) {
                gc.setFill(Color.web("#555555"));
                gc.setFont(Font.font("monospace", 11));
                gc.fillText("✦ 敬請期待 ✦", cx + CARD_W / 2.0, cy + CARD_H / 2.0 + 5);
            } else {
                // 關卡數字
                gc.setFill(isSel
                        ? (i % 2 == 0 ? Color.web("#ff7040") : Color.web("#40b8ff"))
                        : Color.web("#cccccc"));
                gc.setFont(Font.font("Georgia", FontWeight.BOLD, 42));
                gc.fillText("" + (i + 1), cx + CARD_W / 2.0, cy + 72);

                // LEVEL 文字
                gc.setFill(Color.web("#999999"));
                gc.setFont(Font.font("monospace", 10));
                gc.fillText("LEVEL", cx + CARD_W / 2.0, cy + 92);

                // 選中時顯示關卡名稱
                if (isSel) {
                    String[] names = {"入門之道","雙橋試煉","階梯迷宮","螺旋攀升",
                            "火水交錯","高塔挑戰","峽谷飛躍","雙塔之謎","終極試煉"};
                    gc.setFill(Color.web("#aaaaaa"));
                    gc.setFont(Font.font("monospace", 10));
                    gc.fillText(names[i], cx + CARD_W / 2.0, cy + 118);
                }
            }
        }
        gc.restore();

        // 底部小圓點
        double dotY = cardsY + CARD_H + 22;
        for (int i = 0; i < TOTAL; i++) {
            double dotX = w / 2.0 - (TOTAL * 10) / 2.0 + i * 10;
            gc.setFill(i == selected ? Color.web("#ff7040") : Color.web("#333333"));
            gc.fillOval(dotX, dotY, 6, 6);
        }

        // 選中後顯示進入提示
        if (selected >= 0 && selected < 9) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(Color.web("#cccccc"));
            gc.setFont(Font.font("monospace", 12));
            gc.fillText("按 Enter 進入第 " + (selected + 1) + " 關", w / 2.0, dotY + 30);
        }
    }
}