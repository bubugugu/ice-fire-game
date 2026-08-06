package com.example.final_project;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {

    private final GameWorld world;
    private long lastTime = 0;

    public GameLoop(GameWorld world) {
        this.world = world;
    }

    @Override
    public void handle(long now) {
        // now 是系統傳進來的奈秒時間 (nanoseconds)
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        // 計算兩幀之間的時間差 dt (秒)，用來做平滑移動控速
        double dt = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        // 🔥 核心靈魂接線：每一幀都強迫世界進行邏輯更新與畫面重繪！
        world.update(dt);
        world.render();
    }
}