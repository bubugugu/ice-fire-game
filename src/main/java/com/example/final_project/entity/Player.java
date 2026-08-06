package com.example.final_project.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.BoundingBox;
public class Player {
    public boolean onElevator = false;
    // 位置與大小
    public double x, y;
    public final double w = 22, h = 30;

    // 速度
    public double vx = 0, vy = 0;

    // 狀態
    public boolean onGround = false;
    public boolean jumpPressed = false;

    // 物理常數
    public static final double GRAVITY   = 0.7;
    public static final double JUMP_VY   = -10.0;
    public static final double WALK_SPD  = 2.4;
    public static final double FRICTION  = 0.45;

    // 出生點（掉出地圖後重生用）
    private final double spawnX, spawnY;

    // 顏色（火=橘，水=藍）
    private final boolean isFire;

    public Player(double x, double y, boolean isFire) {
        this.x = x;
        this.y = y;
        this.spawnX = x;
        this.spawnY = y;
        this.isFire = isFire;
    }

    // 移動 X 軸
    public void moveX(double dx) {
        x += dx;
    }

    // 移動 Y 軸
    public void moveY(double dy) {
        y += dy;
    }

    // 掉出地圖時重生
    public void respawn() {
        x = spawnX;
        y = spawnY;
        vx = 0;
        vy = 0;
        onGround = false;
    }


    // 畫角色（暫時用方塊，之後換成圖片）
    public void draw(GraphicsContext gc) {
        double cx = x + w / 2;
        double cy = y + h / 2;

        gc.save();
        gc.translate(cx, cy);
        if (vx < -0.2) gc.scale(-1, 1); // 往左走時翻轉

        if (isFire) {
            // 身體下半
            gc.setFill(Color.web("#aa2200"));
            gc.fillOval(-8, -1, 16, 20);
            // 身體上半
            gc.setFill(Color.web("#ff6b35"));
            gc.fillOval(-7, -11, 14, 20);
            // 火焰
            double t = System.currentTimeMillis() / 180.0;
            gc.setFill(Color.web("#ff9944"));
            gc.beginPath();
            gc.moveTo(-4, -10);
            gc.quadraticCurveTo(-2, -18 + Math.sin(t) * 2, 0, -14);
            gc.quadraticCurveTo(2,  -18 + Math.cos(t) * 2, 4, -10);
            gc.fill();
            gc.setFill(Color.web("#ffee00"));
            gc.beginPath();
            gc.moveTo(-2, -10);
            gc.quadraticCurveTo(0, -16 + Math.sin(t + 1) * 1.5, 2, -10);
            gc.fill();
            // 眼睛
            gc.setFill(Color.web("#ffcc00"));
            gc.fillOval(-5, -5, 5, 5);
            gc.fillOval(1,  -5, 5, 5);

        } else {
            // 身體下半
            gc.setFill(Color.web("#005588"));
            gc.fillOval(-8, -1, 16, 20);
            // 身體上半
            gc.setFill(Color.web("#4fc3f7"));
            gc.fillOval(-7, -11, 14, 22);
            // 頭髮
            gc.setStroke(Color.web("#aaeeff"));
            gc.setLineWidth(1.5);
            gc.beginPath();
            gc.moveTo(-3, -10);
            gc.quadraticCurveTo(-5, -18, -2, -20);
            gc.stroke();
            gc.beginPath();
            gc.moveTo(3, -10);
            gc.quadraticCurveTo(5, -18, 2, -20);
            gc.stroke();
            // 眼白
            gc.setFill(Color.WHITE);
            gc.fillOval(-5, -5, 5, 5);
            gc.fillOval(1,  -5, 5, 5);
            // 瞳孔
            gc.setFill(Color.web("#003366"));
            gc.fillOval(-4, -4, 3, 3);
            gc.fillOval(2,  -4, 3, 3);
        }

        gc.restore();
    }
}