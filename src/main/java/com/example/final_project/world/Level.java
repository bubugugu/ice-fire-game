package com.example.final_project.world;

import java.util.ArrayList;
import java.util.List;

public class Level {

    public final List<Tile> tiles = new ArrayList<>();
    public final List<Gem>  gems  = new ArrayList<>();
    public final List<Door> doors = new ArrayList<>();
    public final List<Tile> hazards = new ArrayList<>(); // 水池跟火池為液體
    public final double fireboyStartX, fireboyStartY;
    public final double watergirlStartX, watergirlStartY;
    public final List<Button>   buttons   = new ArrayList<>();
    public final List<Elevator> elevators = new ArrayList<>();
    private static final int T = 32;

    public Level(double fsx, double fsy, double wsx, double wsy) {
        this.fireboyStartX   = fsx;
        this.fireboyStartY   = fsy;
        this.watergirlStartX = wsx;
        this.watergirlStartY = wsy;
    }

    // 加一格地磚
    public void addTile(int col, int row) {
        tiles.add(new Tile(col * T, row * T, T, T, false)); // 邊框：純棕色
    }

    public void addPlatform(int col, int row, int len) {
        for (int i = 0; i < len; i++) {
            tiles.add(new Tile((col + i) * T, row * T, T, T, true)); // 平台：有草地
        }
    }
    // 加火池 (type 1)
    public void addFirePool(int col, int row, int len) {
        for (int i = 0; i < len; i++) {
            // 1. 把液體加到 hazards 陣列（不放進 tiles 就不會擋住人）
            hazards.add(new Tile((col + i) * T, row * T, T, T, false, 1));
            // 2. 偷偷在它「正下方 (row + 1)」墊一塊普通實體地板，讓角色不會掉出地圖
            //tiles.add(new Tile((col + i) * T, (row + 1) * T, T, T, false, 3));
        }
    }

    // 加水池 (type 2)
    public void addWaterPool(int col, int row, int len) {
        for (int i = 0; i < len; i++) {
            hazards.add(new Tile((col + i) * T, row * T, T, T, false, 2));
            //tiles.add(new Tile((col + i) * T, (row + 1) * T, T, T, false, 3));
        }
    }

    // 加邊框（左右牆、地板、天花板）
    public void addBorder(int cols, int rows) {
        for (int r = 0; r < rows; r++) addTile(0, r);
        for (int r = 0; r < rows; r++) addTile(cols - 1, r);
        for (int c = 0; c < cols; c++) addTile(c, rows - 1);
        for (int c = 0; c < cols; c++) addTile(c, 0);
    }

    // 加寶石
    public void addGem(int col, int row, boolean isRed) {
        gems.add(new Gem(col * T + 8, row * T + 6, isRed));
    }

    // 加出口門（固定放右上角）
    public void addDoors(int col, int row) {
        doors.add(new Door(col * T,      row * T, true));
        doors.add(new Door(col * T + 30, row * T, false));
    }
    public void addButton(int col, int row) {
        buttons.add(new Button(col * T + 4, row * T));
    }

    // bottomRow = 梯子初始位置（低處），topRow = 升到的高度
    public void addElevator(int col, int bottomRow, int topRow) {
        elevators.add(new Elevator(col * T, bottomRow * T, topRow * T));
    }
}