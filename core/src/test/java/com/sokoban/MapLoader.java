package com.sokoban;

import java.io.*;
import java.util.*;

/**
 * 普通测试用例的地图加载器<br><br>
 * <b>地图格式</b><br><br>
 * ————————————<br><br>
 * 地图<br><br>操作<br><br>...<br><br>操作<br><br>地图<br><br>
 * ————————————<br><br>
 * 地图为二维字串，使用符号表进行编制，后续将会映射实际逻辑中的数字
 */
public class MapLoader {

    /**
     * 加载并解析测试用例文件
     * @param file 测试用例文件
     * @return 测试用例列表
     * @throws IOException 文件读取异常
     * @throws InvalidMapException 地图格式无效
     */
    public static List<MapTestCase> loadTestCases(File file) throws IOException, InvalidMapException {
        List<MapTestCase> testCases = new ArrayList<>();

        // 读取流
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder currentMapStr = new StringBuilder();
            List<String> moves = new ArrayList<>();
            List<String> fullMapStr = new ArrayList<>();
            String line;
            int lineCount = 0;
            boolean mapReadingFlag = false;

            // 文件非空
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
                if (line.matches("^[UDLR]+$")) {
                    // 行长必须为 1
                    if (line.length() != 1) throw new InvalidMapException(String.format("Invalid map format at line %d: %s", lineCount, line));
                    moves.add(line);
                    continue;
                }
                
                // 处理地图行
                if (isValidMapLine(line)) {
                    mapReadingFlag = true;
                    currentMapStr.append(line).append("\n");
                    continue;
                }

                // 异常数据
                throw new InvalidMapException(String.format("Invalid map format at line %d: %s", lineCount, line));
            }

            // 如果没有末尾空行 / 注释，最后一个地图需要额外一次存储
            if (mapReadingFlag) fullMapStr.add(currentMapStr.toString());

            // 转换地图集为测试用例对象
            if (currentMapStr.length() > 0) {
                testCases.add(createTestCase(fullMapStr, moves));
            }
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
    private static MapTestCase createTestCase(List<String> mapStr, List<String> moves) throws InvalidMapException {
        List<char[][]> charMaps = new ArrayList<>();
        
        // 转换每个地图
        for (String map : mapStr) {
            charMaps.add(parseMap(map));
            validateMap(charMaps.getLast(), false);
        }

        // 转换操作序列
        List<Move> parsedMoves = parseMoves(moves);

        return new MapTestCase(charMaps, parsedMoves);
    }

    /**
     * 解析地图字符串为二维字符数组
     * @param mapStr 地图字符串
     * @return 二维字符数组表示的地图
     * @throws InvalidMapException 地图格式无效
     */
    private static char[][] parseMap(String mapStr) throws InvalidMapException {
        String[] lines = mapStr.trim().split("\n");

        // 检查地图是否为空
        if (lines.length == 0) {
            throw new InvalidMapException("Empty map");
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
            // 用空格填充剩余部分
            for (int j = lineChars.length; j < maxWidth; j++) map[i][j] = MapSignMapping.EMPTY_C;
        }

        return map;
    }

    /**
     * 验证地图的有效性
     * @param map 二维字符数组表示的地图
     * @param boxTargetCountCheck 检查箱子 / 目标数量是否相等
     * @throws InvalidMapException 地图格式无效
     */
    private static void validateMap(char[][] map, boolean boxTargetCountCheck) throws InvalidMapException {
        int playerCount = 0;
        int boxCount = 0;
        int targetCount = 0;

        for (char[] row : map) {
            for (char cell : row) {

                // 匹配目标，有些目标有多种匹配
                switch (cell) {
                    case MapSignMapping.PLAYER_C:
                    case MapSignMapping.PLAYER_ON_TARGET_C:
                        playerCount++;
                        break;
                    case MapSignMapping.BOX_C:
                    case MapSignMapping.BOX_ON_TARGET_C:
                        boxCount++;
                        break;
                    case MapSignMapping.TARGET_C:
                        targetCount++;
                        break;
                    case MapSignMapping.WALL_C:
                    case MapSignMapping.EMPTY_C:
                        break;
                    default:
                        throw new InvalidMapException("Invalid character in map: " + cell);
                }
            }
        }

        // 玩家数量检查
        if (playerCount != 1) throw new InvalidMapException("Invalid player count: " + playerCount + " (should be 1)");

        // 非零箱子检查
        if (boxCount == 0) throw new InvalidMapException("No boxes in map");

        // 箱子目标数对应检查
        int targetNumber = targetCount + countCharInMap(map, MapSignMapping.BOX_ON_TARGET_C) + countCharInMap(map, MapSignMapping.PLAYER_ON_TARGET_C);
        if (targetNumber != boxCount) throw new InvalidMapException("Mismatch between box count and target count");
    }

    /**
     * 计算地图中特定字符的出现次数
     * @param map 二维字符数组表示的地图
     * @param target 目标字符
     * @return 字符出现次数
     */
    private static int countCharInMap(char[][] map, char target) {
        int count = 0;
        for (char[] row : map) {
            for (char cell : row) {
                if (cell == target) count++;
            }
        }
        return count;
    }

    /**
     * 解析移动序列
     * @param moveStrs 移动序列字符串列表
     * @return 移动对象列表
     * @throws InvalidMapException 移动序列格式无效
     */
    private static List<Move> parseMoves(List<String> moveStrs) throws InvalidMapException {
        List<Move> moves = new ArrayList<>();
        char c;

        for (String moveStr : moveStrs) {
            c = moveStr.toCharArray()[0];
            if (!MapSignMapping.isValidMoveChar(c)) throw new InvalidMapException("Invalid move character: " + c);

            switch (c) {
                case MapSignMapping.UP_C:
                    moves.add(new Move(0, -1));
                case MapSignMapping.DOWN_C:
                    moves.add(new Move(0, 1));
                case MapSignMapping.LEFT_C:
                    moves.add(new Move(-1, 0));
                case MapSignMapping.RIGHT_C:
                    moves.add(new Move(1, 0));
            }
        }

        return moves;
    }

    /**
     * 检查地图行的有效性
     * 
     * @param line 地图行字符串
     * @return 是否是有效的地图行
     */
    private static boolean isValidMapLine(String line) {
        if (line == null) return false;
        
        // 允许空行
        if (line.isEmpty()) return true;
        
        return line.chars().allMatch(ch -> MapSignMapping.isValidMapChar((char) ch));
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

/**
 * 移动类，表示一次移动的方向
 */
class Move {
    private final int dx;
    private final int dy;

    public Move(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }

    @Override
    public String toString() {
        if (dx == 0 && dy == -1) return "U";
        if (dx == 0 && dy == 1) return "D";
        if (dx == -1 && dy == 0) return "L";
        if (dx == 1 && dy == 0) return "R";
        return "Invalid";
    }
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
 * 无效地图格式异常
 */
class InvalidMapException extends Exception {
    public InvalidMapException(String message) {
        super(message);
    }
}