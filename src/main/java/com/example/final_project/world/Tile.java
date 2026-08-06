package com.example.final_project.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tile {

    public final double x, y, w, h;
    public final boolean hasSurface;
    public final int type; // 🌟 新增：0=一般地板, 1=火池(橘紅), 2=水池(藍)

    // 🌟 原本的建構子 (保留這個，你原本的地圖就不會報錯)
    public Tile(double x, double y, double w, double h, boolean hasSurface) {
        this(x, y, w, h, hasSurface, 0); // 預設都是一般地板
    }

    // 🌟 新增的建構子 (給火池和水池用)
    public Tile(double x, double y, double w, double h, boolean hasSurface, int type) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.hasSurface = hasSurface;
        this.type = type;
    }

    public void draw(GraphicsContext gc) {
        // 如果是透明方塊，什麼都不畫 (如果你已經不用 type 3 了，這段留著也不影響)
        if (type == 3) return;

        if (type == 1) {
            // 🔥 火池：畫在方塊的最上方 (y)，總厚度設定為 8 像素
            gc.setFill(Color.web("#d32f2f"));
            gc.fillRect(x, y, w, 8);

            // 火池表面：一樣在最上方，厚度 3 像素
            gc.setFill(Color.web("#ff9800"));
            gc.fillRect(x, y, w, 3);

        } else if (type == 2) {
            // 💧 水池：畫在方塊的最上方 (y)，總厚度設定為 8 像素
            gc.setFill(Color.web("#1976d2"));
            gc.fillRect(x, y, w, 8);

            // 水池表面：厚度 3 像素
            gc.setFill(Color.web("#4fc3f7"));
            gc.fillRect(x, y, w, 3);

        } else {
            // 🟫 一般地板 (實體)
            gc.setFill(Color.web("#5c3d1e"));
            gc.fillRect(x, y, w, h);
            if (hasSurface) {
                gc.setFill(Color.web("#3d6a2e"));
                gc.fillRect(x, y, w, 5);
            }
        }
    }
}