package com.example.final_project;

import com.example.final_project.entity.Player;
import com.example.final_project.world.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.List;

public class GameWorld {

    private final GraphicsContext gc;
    private final InputHandler input;
    private List<Button>   buttons   = new ArrayList<>();
    private List<Elevator> elevators = new ArrayList<>();
    private Player fireboy;
    private Player watergirl;

    private List<Tile> tiles;
    private List<Gem>  gems;
    private List<Door> doors;
    private List<Tile> hazards;
    private Physics physics;

    private final LevelManager levelManager = new LevelManager();
    private boolean won = false;
    private boolean fireboyEntered = false; //分開看進門了沒
    private boolean watergirlEntered = false;
    private double timeElapsed = 0;
    private int fireboyDeaths = 0;   // 🌟 紀錄火男孩死亡次數
    private int watergirlDeaths = 0; // 🌟 紀錄水女孩死亡次數
    public static final int T = 32;

    // 新增一個 Signal enum，讓 GameApp 知道要做什麼
    public enum Signal { NONE, BACK_TO_SELECT }

    public GameWorld(GraphicsContext gc, int startLevel) {
        this.gc    = gc;// 👇 新增這兩行，確保每一關開始兩人都還沒進門
        fireboyEntered = false;
        watergirlEntered = false;
        this.input = new InputHandler();
        levelManager.setLevel(startLevel);
        loadLevel();
    }

    // 載入當前關卡
    private void loadLevel() {
        Level lv = levelManager.buildLevel();

        tiles = lv.tiles;
        gems  = lv.gems;
        doors = lv.doors;
        hazards = lv.hazards;

        fireboy   = new Player(lv.fireboyStartX,   lv.fireboyStartY,   true);
        watergirl = new Player(lv.watergirlStartX, lv.watergirlStartY, false);

        physics = new Physics(tiles);
        won = false;
        // 👇 新增這兩行，確保每一關開始兩人都還沒進門
        fireboyEntered = false;
        watergirlEntered = false;
        timeElapsed = 0;// 每次進關卡，時間歸零
        fireboyDeaths = 0;   // 🌟 重置
        watergirlDeaths = 0; // 🌟 重置
        buttons   = lv.buttons;
        elevators = lv.elevators;
    }

    public InputHandler getInput() { return input; }

    public Signal update(double dt) {
        if (won) {
            if (input.isPressed(javafx.scene.input.KeyCode.ENTER)) {
                if (levelManager.hasNext()) {
                    levelManager.next();
                    loadLevel();
                } else {
                    // 最後一關通關，回選關畫面
                    return Signal.BACK_TO_SELECT;
                }
            }
            return Signal.NONE;
        }

        timeElapsed += dt; //計時器
        updateElevators(dt);
        physics.update(fireboy,
                input.fireLeft(), input.fireRight(), input.fireJump(), watergirl);
        physics.update(watergirl,
                input.waterLeft(), input.waterRight(), input.waterJump(), fireboy);

        if (fireboy.y   > GameApp.HEIGHT + 60) { fireboy.respawn(); fireboyDeaths++; }
        if (watergirl.y > GameApp.HEIGHT + 60) { watergirl.respawn(); watergirlDeaths++; }

        checkHazards();
        checkGems();
        checkButtons();

        checkDoors();
        return Signal.NONE;
    }

    private void checkGems() {
        for (Gem g : gems) {
            if (g.collected) continue;
            if ( g.isRed() && overlaps(fireboy,   g)) g.collected = true;
            if (!g.isRed() && overlaps(watergirl, g)) g.collected = true;
        }
    }


    private void checkDoors() {
        boolean allRed  = gems.stream()
                .filter(Gem::isRed).allMatch(g -> g.collected);
        boolean allBlue = gems.stream()
                .filter(g -> !g.isRed()).allMatch(g -> g.collected);

        doors.get(0).open = allRed;
        doors.get(1).open = allBlue;

        // 分別判斷兩人是否碰到門
        if (allRed && !fireboyEntered && overlaps(fireboy, doors.get(0))) {
            fireboyEntered = true;
        }
        if (allBlue && !watergirlEntered && overlaps(watergirl, doors.get(1))) {
            watergirlEntered = true;
        }

        // 當兩個人都進門了，遊戲才算獲勝
        if (fireboyEntered && watergirlEntered) {
            won = true;
        }
    }

    // 🌟 新增：檢查有沒有踩到致命水池或火池
    private void checkHazards() {
        for (Tile t : hazards) {
            if (t.type == 1) {
                if (!watergirlEntered && overlaps(watergirl, t)) {
                    watergirl.respawn();
                    watergirlDeaths++; // 🌟 水女被燒死
                }
            } else if (t.type == 2) {
                if (!fireboyEntered && overlaps(fireboy, t)) {
                    fireboy.respawn();
                    fireboyDeaths++;   // 🌟 火男被淹死
                }
            }
        }
    }
    private void checkButtons() {
        boolean anyPressed = false;

        for (Button btn : buttons) {
            // 檢查火男孩是否踩著 (如果已經進門了，就不算踩著)
            boolean fbOn = !fireboyEntered &&
                    fireboy.x < btn.x + btn.w && fireboy.x + fireboy.w > btn.x &&
                    fireboy.y + fireboy.h >= btn.y && fireboy.y + fireboy.h <= btn.y + btn.h + 4;

            // 檢查水女孩是否踩著
            boolean wgOn = !watergirlEntered &&
                    watergirl.x < btn.x + btn.w && watergirl.x + watergirl.w > btn.x &&
                    watergirl.y + watergirl.h >= btn.y && watergirl.y + watergirl.h <= btn.y + btn.h + 4;

            // 只要其中一人踩著，這個按鈕就處於「按下」狀態
            btn.pressed = (fbOn || wgOn);

            if (btn.pressed) {
                anyPressed = true; // 只要全地圖有任何一個按鈕被按下，就觸發機關
            }
        }

        // 把狀態同步給所有的升降梯
        for (Elevator e : elevators) {
            e.setActivated(anyPressed);
        }
    }

    private void updateElevators(double dt) {
        for (Elevator e : elevators) {
            e.update(dt);
            checkElevatorCarry(fireboy,   e);
            checkElevatorCarry(watergirl, e);
        }
    }

    private void checkElevatorCarry(Player p, Elevator e) {
        boolean onTop = p.x + p.w > e.x && p.x < e.x + e.w
                && p.y + p.h >= e.y && p.y + p.h <= e.y + e.h + 6
                && p.vy >= 0;
        if (onTop) {
            p.y = e.y - p.h;
            p.vy = 0;
            p.onElevator = true;
        } else {
            p.onElevator = false;
        }
    }

    // 🌟 修改：讓 overlaps 支援精準的陷阱 Hitbox，並且兩側內縮更寬容
    private boolean overlaps(Player p, Object obj) {
        double bx, by, bw, bh;
        if      (obj instanceof Gem  g) { bx=g.x; by=g.y; bw=g.w; bh=g.h; }
        else if (obj instanceof Door d) { bx=d.x; by=d.y; bw=d.w; bh=d.h; }
        else if (obj instanceof Tile t) {
            if (t.type == 1 || t.type == 2) {
                // 🔥 水火池的「寬容判定框」
                bx = t.x + 4;  // 🌟 左邊往內縮 4 像素 (起點往右移)
                by = t.y - 2;  // (維持) 上面偷偷往上抬 2 像素抓腳底
                bw = t.w - 8;  // 🌟 總寬度減少 8 像素 (因為左邊扣 4、右邊也要扣 4)
                bh = 8;        // (維持) 厚度 8
            } else {
                // 一般磚塊的判定維持原本的一整格 (32x32)
                bx=t.x; by=t.y; bw=t.w; bh=t.h;
            }
        }
        else return false;

        return p.x < bx+bw && p.x+p.w > bx && p.y < by+bh && p.y+p.h > by;
    }

    public void render() {
        // 1. 先把整個視窗填滿深藍（包含底部空白區）
        gc.setFill(Color.web("#0d0d1a"));
        gc.fillRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        // 2. 畫地圖、門、寶石、角色
        for (Elevator e : elevators) e.draw(gc);
        for (Tile t : tiles) t.draw(gc);
        for (Door d : doors) d.draw(gc);
        for (Gem  g : gems)  g.draw(gc);
        for (Tile t : tiles) t.draw(gc);
        for (Tile h : hazards) h.draw(gc); // 👇 新增：畫出液體陷阱
        for (Button btn : buttons) btn.draw(gc);
        // 只有還沒進門，才畫出角色
        if (!fireboyEntered) fireboy.draw(gc);
        if (!watergirlEntered) watergirl.draw(gc);

        // 3. 關卡編號（右上角）
        gc.setFill(Color.rgb(255, 255, 255, 0.5));
        gc.setFont(Font.font(13));
        gc.fillText("LEVEL " + (levelManager.getCurrent() + 1)
                        + " / " + levelManager.getTotal(),
                GameApp.WIDTH - 90, 22);

        // 3.5 畫出計時器與死亡次數（左上角）
        gc.setFill(Color.web("#000000", 0.6));
        gc.fillRoundRect(15, 10, 250, 30, 10, 10); // 黑底框框拉寬以容納新文字
        gc.setFill(Color.web("#ffffff"));
        gc.setFont(Font.font("monospace", 15));

        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        int totalDeaths = fireboyDeaths + watergirlDeaths;
        // 🌟 顯示：時間 | 火男死亡 | 水女死亡 | 總死亡
        String info = String.format("⏱ %.1f | 🔥:%d 💧:%d 💀:%d", timeElapsed, fireboyDeaths, watergirlDeaths, totalDeaths);
        gc.fillText(info, 140, 31);
        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);

        // 4. 底部提示區：先蓋一層深藍再寫字
        // 底部提示區：泥土色背景
        gc.setFill(Color.web("#333333"));
        gc.fillRect(0, 417, GameApp.WIDTH, GameApp.HEIGHT - 417);

// 上方草地線（跟地板磚塊一樣）
        gc.setFill(Color.web("#3d6a2e"));
        gc.fillRect(0, 417, GameApp.WIDTH, 5);

// 提示文字
        gc.setFill(Color.rgb(255, 255, 255, 0.85));
        gc.setFont(Font.font(14));
        gc.fillText("WASD 火男孩   /   方向鍵 水女孩   /   Esc 回選單",
                GameApp.WIDTH / 2.0 - 140, GameApp.HEIGHT - 10);

        // 5. 通關畫面（最後畫，蓋在所有東西上面）
        if (won) drawWinScreen();
    }

    private void drawWinScreen() {
        gc.setFill(Color.rgb(0, 0, 0, 0.75));
        gc.fillRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        // 🌟 讓這個畫面裡的所有文字都變成置中對齊
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);

// 算出場景正中央的 X 座標
        double centerX = GameApp.WIDTH / 2.0;

        gc.setFill(Color.web("#ffdd44"));
        gc.setFont(Font.font(36));
        gc.fillText("LEVEL " + (levelManager.getCurrent() + 1) + " CLEAR!", centerX, GameApp.HEIGHT / 2.0 - 60);

        int totalDeaths = fireboyDeaths + watergirlDeaths;
        String stars;
        // 🌟 新的評分機制：時間與死亡次數雙重考驗
        if (timeElapsed <= 30 && totalDeaths <= 3) {
            stars = "⭐⭐⭐";
        } else if (timeElapsed <= 60 && totalDeaths <= 6) {
            stars = "⭐⭐";  // 優秀通關：60秒內且死亡不超過3次
        } else {
            stars = "⭐";    // 普通通關
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(24));
        gc.fillText(String.format("通關時間: %.1f 秒", timeElapsed), centerX, GameApp.HEIGHT / 2.0 - 20);
        gc.fillText(String.format("總死亡次數: %d 次", totalDeaths), centerX, GameApp.HEIGHT / 2.0 + 15);
        gc.fillText("評價: " + stars, centerX, GameApp.HEIGHT / 2.0 + 50);

        gc.setFont(Font.font(16));
        // ... (下方保留原本的「按 Enter 進入下一關」等程式碼)
        if (levelManager.hasNext()) {
            gc.fillText("▶ 按 Enter 進入下一關", centerX, GameApp.HEIGHT / 2.0 + 80);
        } else {
            gc.setFill(Color.web("#ffdd44"));
            gc.fillText("🏆 恭喜通關所有關卡！", centerX, GameApp.HEIGHT / 2.0 + 70);
            gc.setFill(Color.WHITE);
            gc.fillText("按 Enter 重新開始", centerX, GameApp.HEIGHT / 2.0 + 100);
        }

        // 🌟 畫完後，務必把筆刷狀態切回預設的靠左對齊
        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
    }
}
