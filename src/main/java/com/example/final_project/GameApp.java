package com.example.final_project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameApp extends Application {

    public static final int WIDTH  = 640;
    public static final int HEIGHT = 500;

    public enum State { TITLE, SELECT, PLAYING }
    private State state = State.TITLE;

    private TitleScreen titleScreen;
    private LevelSelectScreen selectScreen;
    private GameWorld world;
    private GraphicsContext gc;
    private BGM bgm = new BGM();

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();

        titleScreen = new TitleScreen();
        selectScreen = new LevelSelectScreen();

        // ======== 👇 新增：建立透明 UI 覆蓋層 ========
        javafx.scene.layout.Pane uiLayer = new javafx.scene.layout.Pane();
        uiLayer.setPickOnBounds(false);

        // 1. 製作彈出的小框框 (VBox 排列)
        javafx.scene.layout.VBox volumePopup = new javafx.scene.layout.VBox(10);
        volumePopup.setStyle("-fx-background-color: rgba(40, 40, 40, 0.9); -fx-padding: 15; -fx-background-radius: 8; -fx-alignment: center;");
        volumePopup.setVisible(false); // 預設隱藏

        // 🌟 核心修改 A：只要點擊到小視窗內部，就把事件吃掉，防止觸發外面的關閉邏輯
        volumePopup.setOnMousePressed(e -> e.consume());
        volumePopup.setOnMouseClicked(e -> e.consume());

        // 2. 製作滑桿 (Slider) 和 靜音按鈕
        javafx.scene.control.Slider volSlider = new javafx.scene.control.Slider(0, 1, 0.5);
        javafx.scene.control.Button muteBtn = new javafx.scene.control.Button("🔊");
        muteBtn.setStyle("-fx-font-size: 18; -fx-background-color: #444; -fx-text-fill: white; -fx-background-radius: 50; -fx-min-width: 40; -fx-min-height: 40;");

        volumePopup.getChildren().addAll(new javafx.scene.text.Text("音量"), volSlider, muteBtn);
        ((javafx.scene.text.Text)volumePopup.getChildren().get(0)).setFill(javafx.scene.paint.Color.WHITE);

        // 3. 製作右下角的主音量按鈕
        javafx.scene.control.Button volBtn = new javafx.scene.control.Button("音量");
        volBtn.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #d35400; -fx-background-radius: 15; -fx-padding: 8 16;");

        // 🌟 核心修改 B：點擊音量按鈕本身也要吃掉事件，不然它會跟外面的關閉點擊打架
        volBtn.setOnMousePressed(e -> e.consume());

        // 4. 設定座標位置
        volBtn.setLayoutX(WIDTH - 75);
        volBtn.setLayoutY(HEIGHT - 45);
        volumePopup.setLayoutX(WIDTH - 150);
        volumePopup.setLayoutY(HEIGHT - 170);

        volBtn.setFocusTraversable(false);
        volSlider.setFocusTraversable(false);
        muteBtn.setFocusTraversable(false);

        // 5. 綁定按鈕功能
        volBtn.setOnAction(e -> volumePopup.setVisible(!volumePopup.isVisible()));
        // 綁定按鈕功能 (升級 MediaPlayer 後，直接連動就不會 Lag 了！)
        volSlider.valueProperty().addListener((obs, oldVal, newVal) -> bgm.setVolume(newVal.doubleValue()));

        muteBtn.setOnAction(e -> {
            boolean isMuted = muteBtn.getText().equals("🔊");
            bgm.setMute(isMuted);
            muteBtn.setText(isMuted ? "🔇" : "🔊");
        });

        uiLayer.getChildren().addAll(volumePopup, volBtn);
        StackPane root = new StackPane(canvas, uiLayer);
        root.setPrefSize(WIDTH, HEIGHT);
        root.setMinSize(WIDTH, HEIGHT);
        root.setMaxSize(WIDTH, HEIGHT);
        // ======== 👆 UI 層新增結束 ========

        // ======== 👇 新增：外層容器，讓視窗可以自由縮放/最大化，畫面自動等比例置中縮放 ========
        StackPane outer = new StackPane(root);
        outer.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(outer, WIDTH, HEIGHT);

        javafx.beans.value.ChangeListener<Number> scaleListener = (obs, oldV, newV) -> {
            double scale = Math.min(scene.getWidth() / WIDTH, scene.getHeight() / HEIGHT);
            if (scale <= 0) return;
            root.setScaleX(scale);
            root.setScaleY(scale);
        };
        scene.widthProperty().addListener(scaleListener);
        scene.heightProperty().addListener(scaleListener);
        // ======== 👆 外層容器新增結束 ========

        scene.setOnKeyPressed(e -> handleKey(e.getCode()));
        scene.setOnKeyReleased(e -> {
            if (state == State.PLAYING && world != null) {
                world.getInput().keyReleased(e.getCode());
            }
        });

        // 滑鼠事件（視窗縮放後，要把 scene 座標換算回 canvas 640x500 的原始座標）
        scene.setOnMousePressed(e -> {
            // 🌟 核心修改 D：只要點擊到遊戲畫面的任意空白處，就把音量小視窗關閉
            if (volumePopup.isVisible()) {
                volumePopup.setVisible(false);
            }

            if (state == State.SELECT) {
                javafx.geometry.Point2D p = canvas.sceneToLocal(e.getSceneX(), e.getSceneY());
                selectScreen.onMousePressed(p.getX(), p.getY());
            }
        });
        scene.setOnMouseDragged(e -> {
            if (state == State.SELECT) {
                javafx.geometry.Point2D p = canvas.sceneToLocal(e.getSceneX(), e.getSceneY());
                selectScreen.onMouseDragged(p.getX(), p.getY());
            }
        });
        scene.setOnMouseReleased(e -> {
            if (state == State.SELECT) {
                javafx.geometry.Point2D p = canvas.sceneToLocal(e.getSceneX(), e.getSceneY());
                selectScreen.onMouseReleased(p.getX(), p.getY(), WIDTH);
            }
        });

        // 雙擊事件
        scene.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                if (state == State.TITLE) {
                    state = State.SELECT;
                    bgm.stop();
                    bgm.startSelect();

                } else if (state == State.SELECT) {
                    int level = selectScreen.getSelected();
                    if (level >= 0 && level < 9) {
                        world = new GameWorld(gc, level);
                        state = State.PLAYING;
                        bgm.stop();
                    }
                }
            }
        });

        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            long lastTime = 0;
            @Override
            public void handle(long now) {
                if (lastTime == 0) { lastTime = now; return; }
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                if (dt > 0.05) dt = 0.05;
                update(dt);
                render();
            }
        };
        timer.start();

        stage.setTitle("Fire & Water Game");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(WIDTH / 2.0);
        stage.setMinHeight(HEIGHT / 2.0 + 40);
        stage.show();
        root.requestFocus();

        bgm.startTitle();  // 啟動標題BGM
    }

    private void handleKey(KeyCode code) {
        if (state == State.TITLE) {
            if (titleScreen.handleKey(code)) {
                state = State.SELECT;
                bgm.stop();
                bgm.startSelect();  // 切換到選關BGM
            }

        } else if (state == State.SELECT) {
            selectScreen.handleKey(code);
            if (selectScreen.confirm(code)) {
                int level = selectScreen.getSelected();
                world = new GameWorld(gc, level);
                state = State.PLAYING;
                bgm.stop();  // 進遊戲停止BGM
            }
            if (code == KeyCode.ESCAPE) {
                state = State.TITLE;
                bgm.stop();
                bgm.startTitle();  // 切換回標題BGM
            }

        } else if (state == State.PLAYING && world != null) {
            world.getInput().keyPressed(code);
            if (code == KeyCode.ESCAPE) {
                state = State.SELECT;
                world = null;
                bgm.stop();
                bgm.startSelect();  // 切換到選關BGM
            }
        }
    }

    private void update(double dt) {
        if (state == State.TITLE) {
            titleScreen.update(dt);
        } else if (state == State.SELECT) {
            selectScreen.update(dt);
        } else if (state == State.PLAYING && world != null) {
            GameWorld.Signal sig = world.update(dt);
            if (sig == GameWorld.Signal.BACK_TO_SELECT) {
                state = State.SELECT;
                world = null;
                bgm.stop();
                bgm.startSelect();  // 通關回選單切換BGM
            }
        }
    }

    private void render() {
        if (state == State.TITLE) {
            titleScreen.draw(gc, WIDTH, HEIGHT);
        } else if (state == State.SELECT) {
            selectScreen.draw(gc, WIDTH, HEIGHT);
        } else if (world != null) {
            world.render();
        }
    }
}