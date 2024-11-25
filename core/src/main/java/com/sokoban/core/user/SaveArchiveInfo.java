package com.sokoban.core.user;

import java.util.Map;

/**
 * 存档信息类
 * @author Life_Checkpoint
 */
public class SaveArchiveInfo {
    public enum MapStatue {
        Unreached,
        Playing,
        Success,
        Unknow
    }

    // TODO gameMap 类型
    private Map<String, MapStatue> mapsStatue;
    private Map<String, Object> records;

    public Map<String, MapStatue> getMapsStatue() {
        return mapsStatue;
    }

    public void setMapsStatue(Map<String, MapStatue> mapsStatue) {
        this.mapsStatue = mapsStatue;
    }

    public Map<String, Object> getRecords() {
        return records;
    }

    public void setRecords(Map<String, Object> records) {
        this.records = records;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SaveArchiveInfo) {
            SaveArchiveInfo anothorSaveArchive = (SaveArchiveInfo) obj;
            if (!mapsStatue.equals(anothorSaveArchive.getMapsStatue())) return false;
            if (!records.equals(anothorSaveArchive.getRecords())) return false;

            return true;

        } else return false;
    }

}
