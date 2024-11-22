package com.sokoban.polygon;

import com.sokoban.Main;
import com.sokoban.manager.APManager.SpineAssets;

/**
 * 盒子类，专用于盒子对象的显示
 * <br><br>
 * 目标点等对象在游戏前端属于盒子
 * @author Life_Checkpoint
 */
public class BoxObject extends SpineObject {
    private Main gameMain;
    private BoxType boxType;
    private float size;
    
    /** 盒子 Spine 资源枚举 */
    public enum BoxType {
        CornerRightDown(SpineAssets.BoxCornerRightDown),
        DarkBlueBack(SpineAssets.BoxDarkBlueBack),
        DarkGrayBack(SpineAssets.BoxDarkGrayBack),
        GreenChest(SpineAssets.BoxGreenBox),
        GreenChestActive(SpineAssets.BoxGreenBoxLight),
        BlueChest(SpineAssets.BoxBlueBox),
        BoxTarget(SpineAssets.BoxBoxTarget),
        PlayerTarget(SpineAssets.BoxPlayerTarget);

        private final SpineAssets asset;
        BoxType(SpineAssets asset) {this.asset = asset;}
        public SpineAssets getBoxAsset() {return this.asset;}
    }

    /**
     * 创建盒子
     * @param gameMain 全局句柄
     * @param boxType 盒子类型
     * @param size 盒子大小
     * @param centralX 盒子左下 X
     * @param centralY 盒子左下 Y
     */
    public BoxObject(Main gameMain, BoxType boxType, float size, float x, float y) {
        super(gameMain, boxType.getBoxAsset());
        setSize(size, size);
        setPosition(x, y);
        this.gameMain = gameMain;
        this.boxType = boxType;
        this.size = size;
    }

    /**
     * 重置箱子类型
     * @param boxType 箱子类型
     * @return
     */
    public boolean resetBoxType(BoxType boxType) {
        boolean success = super.reset(gameMain, boxType.getBoxAsset());
        if (success) this.boxType = boxType;
        return success;
    }

    public BoxType getBoxType() {
        return boxType;
    }

    public float getSize() {
        return size;
    }

}
