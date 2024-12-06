package com.sokoban.core.user;

import java.util.HashMap;
import java.util.Map;

import com.sokoban.core.game.Logger;
import com.sokoban.core.map.gamedefault.SokobanMaps;
import com.sokoban.utils.DeepClonable;

/**
 * 存档信息类
 * @author Life_Checkpoint
 */
public class SaveArchiveInfo implements DeepClonable<SaveArchiveInfo> {
    public enum MapStatue {
        Unreached,
        Playing,
        Success,
        Unknown
    }

    public static class TimeRecordInfo {
        public long bestRecord;
        public long currentRecord;
        public long delta;

        public TimeRecordInfo(long bestRecord, long currentRecord, long delta) {
            this.bestRecord = bestRecord;
            this.currentRecord = currentRecord;
            this.delta = delta;
        }

        public boolean success() {
            return currentRecord < bestRecord;
        }
    }

    public static class StepRecordInfo {
        public int bestRecord;
        public int currentRecord;
        public int delta;

        public StepRecordInfo(int bestRecord, int currentRecord, int delta) {
            this.bestRecord = bestRecord;
            this.currentRecord = currentRecord;
            this.delta = delta;
        }

        public boolean success() {
            return currentRecord < bestRecord;
        }
    }

    private Map<SokobanMaps, MapStatue> mapStatue = new HashMap<SokobanMaps, MapStatue>();
    private Map<SokobanMaps, Long> bestTimeRecords = new HashMap<SokobanMaps, Long>();
    private Map<SokobanMaps, Integer> bestStepRecords = new HashMap<SokobanMaps, Integer>();

    /**
     * 更新地图解锁状态
     * @param map 地图
     * @param statue 地图状态
     */
    public void updateMapStaute(SokobanMaps map, MapStatue statue) {
        if (!mapStatue.containsKey(map)) {
            Logger.info("SaveArchiveInfo", String.format("New map statue will be created: %s - %s", map.getMapName(), statue));
            mapStatue.put(map, statue);
        } else {
            Logger.info("SaveArchiveInfo", String.format("Map statue will be changed: %s - %s -> %s", map.getMapName(), mapStatue.get(map), statue));
            mapStatue.replace(map, statue);
        }
    }

    /**
     * 获得指定地图解锁状态
     * @param map 地图
     * @return MapStatue 解锁状态，未找到返回 Unknown
     */
    public MapStatue getMapStatue(SokobanMaps map) {
        if (!mapStatue.containsKey(map)) return MapStatue.Unknown;
        return mapStatue.get(map); 
    }

    /**
     * 尝试更新最佳时间记录
     * @param map 地图
     * @param time 当前用时 (ms)
     * @return 时间记录信息
     */
    public TimeRecordInfo updateTimeRecords(SokobanMaps map, long time) {

        if (!bestTimeRecords.containsKey(map)) {

            Logger.info("SaveArchiveInfo", String.format(
                "New map time record will be created: %s - %.3fs", 
                map.getMapName(), ((double) time) / 1000.0
            ));
            bestTimeRecords.put(map, time);

            return new TimeRecordInfo(time, time, 0);

        } else {

            long lastBestRecord = bestTimeRecords.get(map);
            if (lastBestRecord <= time) {
                
                // 挑战更短时间失败
                Logger.info("SaveArchiveInfo", String.format(
                    "Challenge best time record failed: %s - best %.3fs / current %.3fs (+%.3fs)",
                    map.getMapName(), ((double) lastBestRecord) / 1000.0, ((double) time) / 1000.0, ((double) (time - lastBestRecord)) / 1000.0
                ));
                return new TimeRecordInfo(lastBestRecord, time, time - lastBestRecord);

            } else {

                // 挑战更短时间成功
                Logger.info("SaveArchiveInfo", String.format(
                    "Challenge best time record success: %s - best %.3fs / current %.3fs (-%.3fs)",
                    map.getMapName(), ((double) lastBestRecord) / 1000.0, ((double) time) / 1000.0, ((double) (lastBestRecord - time)) / 1000.0
                ));
                bestTimeRecords.replace(map, time);
                return new TimeRecordInfo(lastBestRecord, time, lastBestRecord - time);
                
            }

        }
    }

    /**
     * 尝试更新最佳步数记录
     * @param map 地图
     * @param step 当前步数
     * @return 步数记录信息
     */
    public StepRecordInfo updateStepRecords(SokobanMaps map, int step) {

        if (!bestStepRecords.containsKey(map)) {

            Logger.info("SaveArchiveInfo", String.format(
                "New map step record will be created: %s - %d steps", 
                map.getMapName(), step
            ));
            bestStepRecords.put(map, step);

            return new StepRecordInfo(step, step, 0);

        } else {

            int lastBestRecord = bestStepRecords.get(map);
            if (lastBestRecord <= step) {
                
                // 挑战更短步数失败
                Logger.info("SaveArchiveInfo", String.format(
                    "Challenge best step record failed: %s - best %d steps / current %d steps (+%d steps)",
                    map.getMapName(), lastBestRecord, step, step - lastBestRecord
                ));
                return new StepRecordInfo(lastBestRecord, step, step - lastBestRecord);

            } else {

                // 挑战更短时间成功
                Logger.info("SaveArchiveInfo", String.format(
                    "Challenge best step record success: %s - best %d steps / current %d steps (-%d steps)",
                    map.getMapName(), lastBestRecord, step, lastBestRecord - step
                ));
                bestStepRecords.replace(map, step);
                return new StepRecordInfo(lastBestRecord, step, lastBestRecord - step);
                
            }

        }
    }
 
    public Map<SokobanMaps, MapStatue> getMapStatue() {
        return mapStatue;
    }

    public void setMapStatue(Map<SokobanMaps, MapStatue> mapsStatue) {
        this.mapStatue = mapsStatue;
    }

    public Map<SokobanMaps, Long> getBestTimeRecords() {
        return bestTimeRecords;
    }

    public void setBestTimeRecords(Map<SokobanMaps, Long> bestTimeRecords) {
        this.bestTimeRecords = bestTimeRecords;
    }

    public Map<SokobanMaps, Integer> getBestStepRecords() {
        return bestStepRecords;
    }

    public void setBestStepRecords(Map<SokobanMaps, Integer> bestStepRecords) {
        this.bestStepRecords = bestStepRecords;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SaveArchiveInfo)) return false;
        SaveArchiveInfo anothorSaveArchive = (SaveArchiveInfo) obj;

        if (!mapStatue.equals(anothorSaveArchive.getMapStatue())) return false;
        if (!bestTimeRecords.equals(anothorSaveArchive.getBestTimeRecords())) return false;
        if (!bestStepRecords.equals(anothorSaveArchive.getBestStepRecords())) return false;

        return true;
    }

    @Override
    public SaveArchiveInfo deepCopy() {
        SaveArchiveInfo newArchive = new SaveArchiveInfo();

        Map<SokobanMaps, MapStatue> newMapStatue = new HashMap<SokobanMaps, MapStatue>();
        Map<SokobanMaps, Long> newBestTimeRecords = new HashMap<SokobanMaps, Long>();
        Map<SokobanMaps, Integer> newBestStepRecords = new HashMap<SokobanMaps, Integer>();

        for (Map.Entry<SokobanMaps, MapStatue> entry : mapStatue.entrySet()) {
            newMapStatue.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<SokobanMaps, Long> entry : bestTimeRecords.entrySet()) {
            newBestTimeRecords.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<SokobanMaps, Integer> entry : bestStepRecords.entrySet()) {
            newBestStepRecords.put(entry.getKey(), entry.getValue());
        }

        newArchive.setMapStatue(newMapStatue);
        newArchive.setBestTimeRecords(newBestTimeRecords);
        newArchive.setBestStepRecords(newBestStepRecords);

        return newArchive;
    }
}
