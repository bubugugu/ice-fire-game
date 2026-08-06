package com.example.final_project;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class BGM {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    /**
     * 啟動並播放音檔（支援重複呼叫而不中斷，達成全局唯一播放）
     */
    public void playGlobalBGM() {
        if (isPlaying) {
            return;
        }

        try {
            // 使用 JavaFX 的 URL 方式讀取資源
            URL resource = getClass().getResource("/bgm.wav");
            if (resource == null) {
                System.err.println("❌ 錯誤：找不到 bgm.wav！");
                return;
            }

            Media media = new Media(resource.toString());
            mediaPlayer = new MediaPlayer(media);

            // 🌟 核心關鍵：設定為無限循環播放 (JavaFX 的寫法)
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            // 開始撥放
            mediaPlayer.play();
            isPlaying = true;
            System.out.println("🎵 遊戲 BGM 已成功升級為 MediaPlayer 並開始播放。");

        } catch (Exception e) {
            System.err.println("❌ 播放 BGM 時發生錯誤：");
            e.printStackTrace();
        }
    }

    public void startTitle()  { playGlobalBGM(); }
    public void startSelect() { playGlobalBGM(); }
    public void start()       { playGlobalBGM(); }

    public void stop() {
        // 維持留空，防止 GameApp 主動去切斷音樂。
    }

    /**
     * 設定音量大小 (0.0 ~ 1.0)
     */
    public void setVolume(double value) {
        if (mediaPlayer != null) {
            // MediaPlayer 直接吃 0.0 ~ 1.0，超級方便！
            mediaPlayer.setVolume(value);
        }
    }

    /**
     * 設定是否靜音
     */
    public void setMute(boolean mute) {
        if (mediaPlayer != null) {
            // MediaPlayer 內建靜音開關！
            mediaPlayer.setMute(mute);
        }
    }
}