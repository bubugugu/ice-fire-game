package com.example.final_project.world;

public class LevelManager {

    private int current = 0;
    private static final int TOTAL = 9;

    public int getCurrent() { return current; }
    public int getTotal()   { return TOTAL; }

    public boolean hasNext() { return current < TOTAL - 1; }

    public void next() {
        if (hasNext()) current++;
    }

    public void reset() { current = 0; }
    public void setLevel(int level) { current = level; }

    public Level buildLevel() {
        return switch (current) {
            case 0 -> buildLevel1();
            case 1 -> buildLevel2();
            case 2 -> buildLevel3();
            case 3 -> buildLevel4();
            case 4 -> buildLevel5();
            case 5 -> buildLevel6();
            case 6 -> buildLevel7();
            case 7 -> buildLevel8();
            case 8 -> buildLevel9();
            default -> buildLevel1();
        };
    }

    // ── 關卡 1：入門，平台很好跳 ──────────────────
    private Level buildLevel1() {
        Level lv = new Level(60, 354, 180, 354); //兩人的初始位置
        lv.addBorder(20, 13);

        // ⭐ 終極無縫交錯：讓左右平台在 X 軸直接交接重疊，空中的橫向距離變成 0，絕對連得上一路暢通！
        lv.addPlatform(1,  12, 4); // 第一層（左）：X = 1 ~ 5，高度 12
        lv.addPlatform(4,  10, 4); // 第二層（右）：X = 4 ~ 8，高度 10 (與第一層重疊，輕鬆上)
        lv.addPlatform(1,   8, 4); // 第三層（左）：X = 1 ~ 5，高度 8  (與第二層重疊)
        lv.addPlatform(4,   6, 4); // 第四層（右）：X = 4 ~ 8，高度 6  (與第三層重疊)

        // ⭐ 後續連接埠與大門流暢過渡
        lv.addPlatform(8,   6, 4); // 連接平台：X = 8 ~ 12，高度 6 (與第四層無縫相連)
        lv.addPlatform(12,  6, 4); // 延伸平台：X = 12 ~ 16，高度 6
        lv.addPlatform(16,  6, 4); // 終點大門平台：X = 16 ~ 20，高度 6

        // 💎 寶石位置同步更新（精準擺在平台上）
        lv.addGem(2,  11, true);  lv.addGem(3,  11, false);
        lv.addGem(5,   9, true);  lv.addGem(6,   9, false);
        lv.addGem(2,   7, true);  lv.addGem(3,   7, false);
        lv.addGem(5,   5, true);  lv.addGem(6,   5, false);

        // 終點衝刺路段寶石
        lv.addGem(10,  5, true);  lv.addGem(11,  5, false);
        lv.addGem(14,  5, true);  lv.addGem(15,  5, false);

        lv.addDoors(16, 4); // 門完美落在高度 6 的終點平台上
        return lv;
    }

    // ── 關卡 2 ────────────────────────────────────  開始有火池水池
    private Level buildLevel2() {
        Level lv = new Level(60, 354, 400, 354);
        lv.addBorder(20, 13);
        lv.addPlatform(3, 11, 2);
        lv.addPlatform(6,  9, 2);
        lv.addPlatform(9,  7, 2);
        lv.addPlatform(12, 5, 2);
        lv.addPlatform(3,  7, 2);
        lv.addPlatform(6,  5, 2);
        lv.addPlatform(14, 4, 4); // 出口底座往下移到row4
        //lv.addFirePool(12, 5, 1); // 岩漿
        lv.addFirePool(7, 9, 1); // 岩漿
        lv.addFirePool(9, 12, 1); // 岩漿
        lv.addWaterPool(3,7,1);
        lv.addGem(3, 9, true);  lv.addGem(4, 10, false);
        lv.addGem(6,  8, true);  lv.addGem(7,  7, false);
        lv.addGem(9,  6, true);  lv.addGem(6,  4, false);
        lv.addGem(3,  6, true);  lv.addGem(12, 4, false);
        lv.addDoors(14, 2);
        return lv;
    }
// test 阿恆
    // ── 關卡 3 ────────────────────────────────────
     private Level buildLevel3() {


             // 出生點：站在低地平台(row=10)頂面y=320，角色高30，所以y=290
             Level lv = new Level(60, 290, 110, 290);
             lv.addBorder(20, 13);

             // 地面
             lv.addPlatform(1, 12, 18);

             // 低地平台（row=10）
             lv.addPlatform(1, 11, 6);

             // 矮牆：從 row=7 連到 row=11（緊貼地面），擋住直接跑過去
             // 高地在row=6，矮牆頂在row=7，差1格=32px
             // 單人站低地跳不上高地(row=6)，雙人踩頭可以 ✓
             lv.addTile(7, 7);
             lv.addTile(7, 8);
             lv.addTile(7, 9);
             lv.addTile(7, 10);
             lv.addTile(7, 11);

             // 右側高地（row=6，頂面y=192）
             lv.addPlatform(8, 6, 11);

             // 按鈕：放在高地平台上（row=6）
             lv.addButton(12, 6);

             // 升降梯：bottomY=308（停在低地頂面），topY=180（停在高地頂面）
              lv.addElevator(5, 10, 5);

             // 低地寶石（row=9，浮在低地上方）
             lv.addGem(2, 9, true);
             lv.addGem(4, 9, false);

             // 高地寶石（row=5，浮在高地上方）
             lv.addGem(10, 5, true);
             lv.addGem(14, 5, false);
             lv.addGem(16, 5, true);
             lv.addGem(17, 5, false);

             // 出口門（row=5，門底部剛好在高地頂面上方）
             lv.addDoors(17, 5);
             return lv;
         }

    // ── 關卡 4 ────────────────────────────────────
    private Level buildLevel4() {
        Level lv = new Level(250, 354, 180, 354);
        lv.addBorder(20, 13);

        // ⭐ 修正版：左側階梯（改為交錯排列，避免垂直重疊撞頭）
        lv.addPlatform(2,  11, 3); // 第一層：偏左
        lv.addPlatform(4,   9, 3); // 第二層：往右錯開，頭頂有空間
        lv.addPlatform(2,   7, 3); // 第三層：再往左錯開

        // ⭐ 修正版：右側階梯（同樣改為交錯排列）
        lv.addPlatform(12, 10, 3); // 第一層：偏右
        lv.addPlatform(10,  8, 3); // 第二層：往左錯開
        lv.addPlatform(12,  6, 3); // 第三層：再往右錯開

        // 中間橋接
        lv.addPlatform(6,   6, 5);

        // 終點平台
        lv.addPlatform(14,  4, 3);

        // 💎 寶石位置對應優化（配合新平台位置稍微調整 X 軸，確保都在平台上）
        // 左側階梯寶石
        lv.addGem(3,  10, true);   lv.addGem(4,  10, false); // 第一層上方
        lv.addGem(5,   8, true);   lv.addGem(6,   8, false); // 第二層上方
        lv.addGem(3,   6, true);   lv.addGem(4,   6, false); // 第三層上方

        // 右側階梯寶石
        lv.addGem(13,  9, true);   lv.addGem(14,  9, false); // 第一層上方
        lv.addGem(11,  7, true);   lv.addGem(12,  7, false); // 第二層上方
        lv.addGem(13,  5, true);   lv.addGem(14,  5, false); // 第三層上方

        // 中間與終點寶石
        lv.addGem(7,   5, true);   lv.addGem(9,   5, false);

        lv.addDoors(14, 2);
        return lv;
    }
    // ── 關卡 5：機關重重，雙人合作解謎 ──────────────────
    private Level buildLevel5() {
        Level lv = new Level(60, 354, 190, 354); // 兩人初始位置靠左
        lv.addBorder(20, 13);

        // --- 1樓 (Ground) ---
        lv.addPlatform(1, 12, 6);   // 左側一樓地板
        lv.addFirePool(7, 12, 3);   // 火池 (擋住水女孩的去路)
        lv.addPlatform(10, 12, 9);  // 右側一樓地板

        // --- 升降梯 (水女孩在此等候) ---
        lv.addElevator(3, 11, 6);   // 從 1樓(row 12) 升到 2樓(row 6)

        // --- 火男孩專屬的右側階梯 ---
        lv.addPlatform(12, 10, 2);  // 階梯第一階
        lv.addPlatform(15,  8, 2);  // 階梯第二階

        // --- 2樓 (Upper Level) ---
        //lv.addPlatform(2, 6, 2);    // 二樓左側平台 (水女孩搭電梯上來抵達的地方)
        lv.addWaterPool(6, 6, 1);   // 二樓水池 (擋住火男孩往左走)
        lv.addPlatform(5, 6, 3);
        lv.addPlatform(9, 6, 10);   // 二樓右側大平台 (火男孩爬階梯上來抵達的地方)

        // --- 關鍵機關按鈕 ---
        // 火男孩爬上二樓右側後，按下這個按鈕，左邊的電梯就會升起！
        lv.addButton(13, 10);
        lv.addButton(11, 6);


        // --- 寶石配置 ---
        lv.addGem(8, 10, true);     // 火池上方 (火男孩跳躍時順便拿)
        lv.addGem(14, 10, true);    // 一樓右側角落
        lv.addGem(7, 4, false);     // 二樓水池上方 (水女孩過橋時拿)
        lv.addGem(3, 4, false);     // 二樓左側電梯旁

        // --- 終點門 ---
        lv.addDoors(16, 4);         // 設在二樓最右側，等兩人會合

        return lv;
    }
    //保留一下舊的 上面新的是AI做的
//    // ── 關卡 5 ────────────────────────────────────
//    private Level buildLevel5() {
//        Level lv = new Level(60, 354, 180, 354);
//        lv.addBorder(20, 13);
//        lv.addPlatform(2, 10, 2);
//        lv.addPlatform(7, 10, 2);
//        lv.addPlatform(12,10, 2);
//        lv.addPlatform(4,  8, 2);
//        lv.addPlatform(9,  8, 2);
//        lv.addPlatform(14, 8, 2);
//        lv.addPlatform(2,  6, 2);
//        lv.addPlatform(7,  6, 2);
//        lv.addPlatform(12, 6, 2);
//        // 出口階梯：一步一步往上
//        lv.addPlatform(15,10, 2); // 階梯1
//        lv.addPlatform(15, 8, 2); // 階梯2
//        lv.addPlatform(15, 6, 2); // 階梯3
//        lv.addPlatform(15, 4, 2); // 出口底座
//        lv.addGem(2,  9, true);  lv.addGem(7,   9, false);
//        lv.addGem(12, 9, true);  lv.addGem(4,   7, false);
//        lv.addGem(9,  7, true);  lv.addGem(14,  7, false);
//        lv.addGem(2,  5, true);  lv.addGem(7,   5, false);
//        lv.addDoors(15, 2);
//        return lv;
//    }

    // ── 關卡 6 ────────────────────────────────────
    private Level buildLevel6() {
        Level lv = new Level(60, 354, 180, 354);
        lv.addBorder(20, 13);
        lv.addPlatform(1,  8, 3); lv.addPlatform(5,  6, 3);
        lv.addPlatform(9,  8, 3); lv.addPlatform(13, 6, 3);
        lv.addPlatform(3, 10, 3); lv.addPlatform(1,  4, 3);
        lv.addPlatform(9,  4, 3); lv.addPlatform(15, 4, 3);
        lv.addGem(1,  7, true);  lv.addGem(3,  7, false);
        lv.addGem(5,  5, true);  lv.addGem(7,  5, false);
        lv.addGem(9,  7, true);  lv.addGem(11, 5, false);
        lv.addGem(1,  3, true);  lv.addGem(15, 3, false);
        lv.addDoors(17, 2);
        return lv;
    }

    // ── 關卡 7 ────────────────────────────────────
    private Level buildLevel7() {
        Level lv = new Level(300, 354, 450, 354);
        lv.addBorder(20, 13);
        // 所有平台不靠牆，至少從col=3開始
        lv.addPlatform(3, 10, 2);
        lv.addPlatform(9, 10, 2);
        lv.addPlatform(15,10, 2);
        lv.addPlatform(6,  8, 2);
        lv.addPlatform(12, 8, 2);
        lv.addPlatform(3,  6, 2);
        lv.addPlatform(9,  6, 2);
        lv.addPlatform(15, 6, 2);
        lv.addPlatform(6,  4, 2);
        lv.addPlatform(12, 4, 2);
        lv.addPlatform(15, 4, 2); // 出口底座
        lv.addGem(3,  9, true);  lv.addGem(9,   9, false);
        lv.addGem(15, 9, true);  lv.addGem(6,   7, false);
        lv.addGem(12, 7, true);  lv.addGem(3,   5, false);
        lv.addGem(9,  5, true);  lv.addGem(15,  5, false);
        lv.addGem(6,  3, true);  lv.addGem(12,  3, false);
        lv.addDoors(15, 2);
        return lv;
    }
//ASA我老婆
    // ── 關卡 8 ────────────────────────────────────

    private Level buildLevel8() {

        Level lv = new Level(60, 354, 180, 354);

        lv.addBorder(20, 13);

        lv.addPlatform(1, 11, 3); lv.addPlatform(1,  9, 3);

        lv.addPlatform(1,  7, 3); lv.addPlatform(1,  5, 3);

        lv.addPlatform(6, 10, 3); lv.addPlatform(10, 8, 3);

        lv.addPlatform(6,  6, 3); lv.addPlatform(10, 4, 3);

        lv.addPlatform(15,11, 3); lv.addPlatform(15, 9, 3);

        lv.addPlatform(15, 7, 3); lv.addPlatform(16, 5, 3);

        lv.addGem(4, 10, true);  lv.addGem(5, 10, false);

        lv.addGem(8,  7, true);  lv.addGem(12, 7, false);

        lv.addGem(4,  4, true);  lv.addGem(12, 3, false);

        lv.addDoors(17, 3);

        return lv;

    }
    // ── 關卡 9：最終關 ────────────────────────────
    private Level buildLevel9() {
        Level lv = new Level(60, 354, 180, 354);
        lv.addBorder(20, 13);
        lv.addPlatform(1, 10, 2);
        lv.addPlatform(4,  8, 2);
        lv.addPlatform(7,  6, 2);
        lv.addPlatform(10, 4, 2);
        lv.addPlatform(13, 6, 2);
        lv.addPlatform(16, 8, 2);
        lv.addPlatform(13, 4, 2);
        lv.addPlatform(4,  4, 2);
        lv.addPlatform(7, 10, 2);
        lv.addPlatform(10, 8, 2);
        lv.addPlatform(15, 6, 2); // 出口底座（往下移一層）
        lv.addGem(1,  9, true);  lv.addGem(4,   7, false);
        lv.addGem(7,  5, true);  lv.addGem(10,  3, false);
        lv.addGem(13, 5, true);  lv.addGem(16,  7, false);
        lv.addGem(4,  3, true);  lv.addGem(13,  3, false);
        lv.addGem(7,  9, true);  lv.addGem(10,  7, false);
        lv.addDoors(15, 4);
        return lv;
    }
}