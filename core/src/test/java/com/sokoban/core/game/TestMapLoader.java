package com.sokoban.core.game;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import com.sokoban.core.Logger;

/**
 * 移动类，表示多次移动的方向
 */
class Move {
    private List<Integer> dx;
    private List<Integer> dy;

    public Move() {
        dx = new ArrayList<>();
        dy = new ArrayList<>();
    }

    public void move(int x, int y) {
        dx.add(x);
        dy.add(y);
    }

    public List<Integer> getDxs() { return dx; }
    public List<Integer> getDys() { return dy; }
}

/**
 * 测试用例类，包含地图和移动序列
 */
class MapTestCase {
    private final List<char[][]> map;
    private final List<Move> moves;

    public MapTestCase(List<char[][]> map, List<Move> moves) {
        this.map = map;
        this.moves = moves;
    }

    public List<char[][]> getMap() {
        return map;
    }
    public List<Move> getMoves() {
        return moves;
    }
}

/**
 * 普通测试用例的地图加载器
 * <br><br>
 * <b>地图格式</b>
 * <br><br>
 * ————————————
 * <br><br>
 * 地图
 * <br><br>
 * 操作
 * <br><br>
 * ...
 * <br><br>
 * 操作
 * <br><br>
 * 地图
 * <br><br>
 * ————————————
 * <br><br>
 * 地图为二维字串，使用符号表进行编制，后续将会映射实际逻辑中的数字
 * <br><br>
 * 暂时未启用
 * @author Life_Checkpoint
 * @author Claude
 */
public class TestMapLoader {

    /**
     * 加载并解析测试用例文件
     * @param file 测试用例文件
     * @return 测试用例列表，失败返回空
     */
    @SuppressWarnings("all")
    public static List<MapTestCase> loadTestCases(File file) {
        List<MapTestCase> testCases = new ArrayList<>();

        // 读取流
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuilder currentMapStr = new StringBuilder();
            List<String> moves = new ArrayList<>();
            List<String> fullMapStr = new ArrayList<>();
            String line;
            int lineCount = 0;
            boolean mapReadingFlag = false;

            // 文件非空，读行
            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim();
                
                // 跳过注释 空行
                if (line.startsWith("//") || line.isEmpty()) {
                    // 如果发现读取当前地图行已完成，加入列表
                    if (mapReadingFlag) {
                        mapReadingFlag = false;
                        fullMapStr.add(currentMapStr.toString());
                        currentMapStr = new StringBuilder();
                    }
                    continue;
                }

                // 处理移动序列
                if (line.matches("^[UDLRudlr]+$")) {
                    moves.add(line);
                    continue;
                }
                
                // 处理地图行
                if (line != "") {
                    mapReadingFlag = true;
                    currentMapStr.append(line).append("\n");
                    continue;
                }

            }

            // 如果没有末尾空行 / 注释，最后一个地图需要额外一次存储
            if (mapReadingFlag) fullMapStr.add(currentMapStr.toString());

            // 转换地图集为测试用例对象
            if (currentMapStr.length() > 0) {
                testCases.add(createTestCase(fullMapStr, moves));
            }

            reader.close();
            
        } catch (Exception e) {
            Logger.error("MapLoaderTest", "Can't load map because: " + e.getMessage());
            return null;
        }

        return testCases;
    }

    /**
     * 创建测试用例对象
     * @param mapStr 地图字符串列表
     * @param moves 移动序列列表
     * @return 测试用例对象
     * @throws InvalidMapException 地图格式无效
     */
    private static MapTestCase createTestCase(List<String> mapStr, List<String> moves) {
        List<char[][]> charMaps = new ArrayList<>();
        
        // 转换每个地图
        for (String map : mapStr) {
            charMaps.add(parseMap(map));
            validateMap(charMaps.getLast(), false);
        }

        // 转换每个操作序列
        List<Move> parsedMoves = parseMoves(moves);

        return new MapTestCase(charMaps, parsedMoves);
    }

    /**
     * 解析地图字符串为二维字符数组
     * @param mapStr 地图字符串
     * @return 二维字符数组表示的地图，失败返回空
     */
    private static char[][] parseMap(String mapStr) {
        String[] lines = mapStr.trim().split("\n");

        // 检查地图是否为空
        if (lines.length == 0) {
            return null;
        }

        // 最大宽度
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, line.length());
        }

        // 地图字符数组
        char[][] map = new char[lines.length][maxWidth];

        // 填充地图，较短的行用空格补齐
        for (int i = 0; i < lines.length; i++) {
            char[] lineChars = lines[i].toCharArray();
            // 复制实际字符
            System.arraycopy(lineChars, 0, map[i], 0, lineChars.length);
            // 用 - 填充剩余部分
            for (int j = lineChars.length; j < maxWidth; j++) map[i][j] = '-';
        }

        return map;
    }

    /**
     * 验证地图的有效性
     * @param map 二维字符数组表示的地图
     * @param boxTargetCountCheck 检查箱子 / 目标数量是否相等
     * @throws InvalidMapException 地图格式无效
     */
    private static boolean validateMap(char[][] map, boolean boxTargetCountCheck) {
        int playerCount = 0;

        for (char[] row : map) {
            for (char cell : row) {
                // 玩家数量检查
                switch (MapMapper.MapCharToType(cell)) {
                    case ObjectType.PLAYER:
                    case ObjectType.PlayerGetPLpos:
                    case ObjectType.PlayerGetBoxpos:
                        playerCount++;
                    default:
                }
            }
        }

        // 玩家数量检查
        if (playerCount != 1) {
            Logger.error("MapLoaderTest", String.format("Player num get %d, expect %d", playerCount, 1));
            return false;
        }

        return true;
    }

    /**
     * 解析移动序列
     * @param moveStrs 移动序列字符串列表
     * @return 移动对象列表
     */
    private static List<Move> parseMoves(List<String> moveStrs) {
        List<Move> moves = new ArrayList<>();

        for (String moveStrLine : moveStrs) {
            List<Character> moveChars = moveStrLine.toUpperCase().chars().mapToObj(c -> (char) c).collect(Collectors.toList());
            Move movings = new Move();
            for (char moveChar : moveChars) {
                int dx = switch (moveChar) {case 'L' -> -1; case 'R' -> 1; default -> 0;};
                int dy = switch (moveChar) {case 'D' -> -1; case 'U' -> 1; default -> 0;};
                movings.move(dx, dy);
            }
            moves.add(movings);
        }

        return moves;
    }

    /**
     * 获取地图的字符串表示
     * @param map 二维字符数组表示的地图
     * @return 地图的字符串表示
     */
    public static String mapToString(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : map) {
            sb.append(new String(row)).append("\n");
        }
        return sb.toString();
    }
}
