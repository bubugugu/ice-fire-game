package com.example.final_project.world;

import com.example.final_project.entity.Player;
import java.util.List;

public class Physics {

    private final List<Tile> tiles;

    public Physics(List<Tile> tiles) {
        this.tiles = tiles;
    }

    private boolean overlap(double ax, double ay, double aw, double ah,
                            double bx, double by, double bw, double bh) {
        return ax < bx + bw && ax + aw > bx &&
                ay < by + bh && ay + ah > by;
    }

    public void moveX(Player p, double dx) {
        p.x += dx;
        for (Tile t : tiles) {
            if (!overlap(p.x, p.y, p.w, p.h, t.x, t.y, t.w, t.h)) continue;
            if (dx > 0) p.x = t.x - p.w;
            else        p.x = t.x + t.w;
            p.vx = 0;
        }
    }

    public void moveY(Player p, double dy) {
        p.y += dy;
        for (Tile t : tiles) {
            if (!overlap(p.x, p.y, p.w, p.h, t.x, t.y, t.w, t.h)) continue;
            if (dy > 0) {
                p.y = t.y - p.h;
                p.vy = 0;
                p.onGround = true;
            } else {
                p.y = t.y + t.h;
                p.vy = 0;
            }
        }
    }

    // ★ 新增：X 軸角色互推
    private void resolvePlayerX(Player p, Player other, double dx) {
        if (!overlap(p.x, p.y, p.w, p.h, other.x, other.y, other.w, other.h)) return;
        if (dx > 0) p.x = other.x - p.w;
        else if (dx < 0) p.x = other.x + other.w;
        p.vx = 0;
    }

    // ★ 新增：Y 軸角色站頭頂
    private void resolvePlayerY(Player p, Player other, double dy) {
        if (!overlap(p.x, p.y, p.w, p.h, other.x, other.y, other.w, other.h)) return;
        if (dy > 0) {
            // p 從上方踩到 other 頭頂
            p.y = other.y - p.h;
            p.vy = 0;
            p.onGround = true;
        } else {
            // p 從下方撞到 other 腳底
            p.y = other.y + other.h;
            p.vy = 0;
        }
    }

    // ★ 修改：update 多一個 other 參數
    public void update(Player p, boolean left, boolean right, boolean jump, Player other) {
        p.onGround = false;

        if (left)       p.vx = -Player.WALK_SPD;
        else if (right) p.vx =  Player.WALK_SPD;
        else            p.vx *= Player.FRICTION;

        p.vy += Player.GRAVITY;

        // X 軸：先對 tile，再對另一個角色
        moveX(p, p.vx);
        resolvePlayerX(p, other, p.vx);

        // Y 軸：先對 tile，再對另一個角色
        moveY(p, p.vy);
        resolvePlayerY(p, other, p.vy);

        if (jump && !p.jumpPressed && (p.onGround || p.onElevator)) {
            p.vy = Player.JUMP_VY;
            p.jumpPressed = true;
            p.onElevator = false;
        }
        if (!jump) p.jumpPressed = false;
    }
}