package com.example.final_project.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Gem {

    public double x, y;
    public final double w = 16, h = 16;
    public boolean collected = false;

    private final boolean isRed; // true=火男孩拿，false=水女孩拿

    public Gem(double x, double y, boolean isRed) {
        this.x = x;
        this.y = y;
        this.isRed = isRed;
    }

    public boolean isRed() { return isRed; }

    public void draw(GraphicsContext gc) {
        if (collected) return;

        double bob = Math.sin(System.currentTimeMillis() / 500.0) * 2;
        double cx = x + w / 2;
        double cy = y + h / 2 + bob;

        gc.save();
        gc.translate(cx, cy);

        // 寶石本體（六角形）
        if (isRed) gc.setFill(Color.web("#ff4444"));
        else       gc.setFill(Color.web("#44aaff"));

        gc.beginPath();
        gc.moveTo(0, -8);
        gc.lineTo(6, -3);
        gc.lineTo(6,  3);
        gc.lineTo(0,  8);
        gc.lineTo(-6, 3);
        gc.lineTo(-6,-3);
        gc.closePath();
        gc.fill();

        // 光澤
        gc.setFill(Color.rgb(255, 255, 255, 0.3));
        gc.beginPath();
        gc.moveTo(0, -8);
        gc.lineTo(6, -3);
        gc.lineTo(0,  0);
        gc.closePath();
        gc.fill();

        gc.restore();
    }
}