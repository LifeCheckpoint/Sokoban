package com.sokoban.polygon;

import com.sokoban.Main;
import com.sokoban.manager.APManager.SpineAssets;

/**
 * 盒子类，专用于盒子对象的显示
 * @author Life_Checkpoint
 */
public class BoxObject extends SpineObject {
    
    /** 盒子 Spine 资源枚举 */
    public enum BoxType {
        CornerRightDown(SpineAssets.BoxCornerRightDown),
        DarkBlueBack(SpineAssets.BoxDarkBlueBack);

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
    }

}
