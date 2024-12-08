package com.sokoban.core.map;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sokoban.core.game.Logger;
import com.sokoban.core.logic.Pos;

/**
 * 移动信息解析类
 * <br><br>
 * 对核心向前端发送的对象移动信息进行解析，反演等的工具类
 * @author Life_Checkpoint
 */
public class MoveListParser {

    /**
     * 移动数据类
     */
    public static class MoveInfo {
        public int subMapIndex;
        public int layerIndex;
        public Pos origin;
        public Pos to;

        public MoveInfo(int subMapIndex, int layerIndex, Pos origin, Pos to) {
            this.subMapIndex = subMapIndex;
            this.layerIndex = layerIndex;
            this.origin = origin;
            this.to = to;
        }
    }

    /**
     * 对单个字符串指令进行反序列化解析
     * @param move 移动指令
     * @return 解析结果，失败返回 null
     */
    public static MoveInfo parseMove(String move) {
        String[] args = move.strip().split(" ");

        if (args.length != 6) {
            Logger.error("MoveListParser", "Can not parse moving info. Expected 6 members, get " + args.length);
            return null;
        }

        return new MoveInfo(
            Integer.parseInt(args[0]), // 子地图索引
            Integer.parseInt(args[1]), // 层数
            new Pos(Integer.parseInt(args[2]), Integer.parseInt(args[3])), // 原位置
            new Pos(Integer.parseInt(args[4]), Integer.parseInt(args[5])) // 新位置
        );
    }

    /**
     * 对单个标准指令进行序列化
     * @param move 指令
     * @return 解析结果，失败返回 null
     */
    public static String serializeMove(MoveInfo move) {
        try {
            return String.format(
                "%d %d %d %d %d %d",
                move.subMapIndex, move.layerIndex, move.origin.getX(), move.origin.getY(), move.to.getX(), move.to.getY()
            );
        } catch (Exception e) {
            Logger.error("MoveListParser", "Can not serialize move info. Because " + e.getMessage());
            return null;
        }
    }

    /**
     * 对所有字符串指令进行反序列化解析
     * @param moves 字符串指令 List
     * @return 解析结果
     */
    public static List<MoveInfo> parseMove(List<String> moves) {
        return moves.stream().map(MoveListParser::parseMove).toList(); // 解析每个对象
    }

    /**
     * 对所有标准指令进行序列化
     * @param moves 标准指令 List
     * @return 解析结果
     */
    public static List<String> serializeMove(List<MoveInfo> moves) {
        return moves.stream().map(MoveListParser::serializeMove).toList(); // 解析每个对象
    }

    /**
     * 反演移动信息
     * @param moves 原移动信息
     * @return 反演的移动信息
     */
    public static List<String> inverseMoves(List<String> moves) {
        List<MoveInfo> parsedMoves = parseMove(moves);
        List<MoveInfo> inversMoves = parsedMoves.stream().map(move -> new MoveInfo(
            move.subMapIndex, move.layerIndex, move.to.deepCopy(), move.origin.deepCopy()
        )).toList();
        return serializeMove(inversMoves);
    }

    public static Map<Integer, Map<Integer, List<MoveInfo>>> groupMoves(List<MoveInfo> moves) {
        return moves.stream().collect(Collectors.groupingBy(
            move -> move.subMapIndex, // 一级分组：subMapIndex
            Collectors.groupingBy(
                    move -> move.layerIndex // 二级分组：layerIndex
            )
        ));
    }
}
