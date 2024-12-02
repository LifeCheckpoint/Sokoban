package com.sokoban.core.game;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sokoban.core.map.MapData;
import com.sokoban.core.map.MapFileParser;
import com.sokoban.core.map.MapFileReader;

/**
 * 测试游戏逻辑正确性
 * <br><br>
 * 游戏测试地图放置于 ./test-files/下
 */
public class PlayerCoreTest {
    /** 储存测试样例的数据结构 */
    public static class ParsedData {
        private final List<char[][]> maps; // 存储所有地图
        private final List<List<Character>> moves; // 存储所有操作序列

        public ParsedData(List<char[][]> maps, List<List<Character>> moves) {
            this.maps = maps;
            this.moves = moves;
        }

        public List<char[][]> getMaps() {
            return maps;
        }

        public List<List<Character>> getMoves() {
            return moves;
        }
    }

    /** 数组沿水平轴反转 */
    public void flipHorizontal(char[][] matrix) {
        int rows = matrix.length;
        for (int i = 0; i < rows / 2; i++) {
            char[] temp = matrix[i];
            matrix[i] = matrix[rows - 1 - i];
            matrix[rows - 1 - i] = temp;
        }
    }

    /** 测试样例储存 */
    public List<List<MapData>> mapSequences = new ArrayList<>(); // 样例 - 地图序列 - 地图
    public List<List<List<Direction>>> operatorSequences = new ArrayList<>(); // 样例 - 多轮操作序列 - 单轮操作序列 - 单个操作

    /** 转换测试数据 */
    public ParsedData parseMapSequence(String path) {
        // 读取文件内容
        String fileContent = new MapFileReader().readMapByPath(path);
        String[] lines = fileContent.split("\n"); // 按行分割文件内容

        List<char[][]> maps = new ArrayList<>(); // 存储所有地图
        List<List<Character>> moves = new ArrayList<>();  // 存储所有操作序列

        List<char[]> currentMap = new ArrayList<>(); // 临时存储单张地图

        for (String line : lines) {
            line = line.trim(); // 去除行首尾空白符

            if (line.isEmpty()) { // 空行表示分隔符
                if (!currentMap.isEmpty()) {
                    maps.add(currentMap.toArray(new char[0][])); // 保存当前地图
                    flipHorizontal(maps.getLast()); // 由于读入地图是从上往下，所以需要翻转地图
                    currentMap.clear(); // 重置当前地图
                }

            } else if (line.matches("[UDLRudlr]+")) { // 判断是否为操作序列
                List<Character> lineList = new ArrayList<>();
                for (char op : line.toCharArray()) lineList.add(op);
                moves.add(lineList); // 保存操作序列

            } else { // 否则为地图行
                currentMap.add(line.toCharArray()); // 保存地图行
            }
        }

        // 文件末尾可能还有未保存的地图
        if (!currentMap.isEmpty()) {
            maps.add(currentMap.toArray(new char[0][]));
            flipHorizontal(maps.getLast()); // 由于读入地图是从上往下，所以需要翻转地图
        }

        // 验证地图数量是否比操作序列多 1
        if (maps.size() != moves.size() + 1) {
            throw new IllegalArgumentException(String.format("%s 地图数量 %d 应比操作序列数量 %d 多 1", path, maps.size(), moves.size()));
        }

        return new ParsedData(maps, moves);
    }

    /** 准备测试数据 */
    @BeforeClass
    public void prepareData() {
        // 测试 12 个地图
        for (int testIndex = 1; testIndex <= 12; testIndex++){
            ParsedData currentTest = parseMapSequence(String.format("./test-files/test-level/normal/map%d.cmap", testIndex));

            // 将地图序列中的每个地图转换为标准地图数据
            List<MapData> mapData = new ArrayList<>();
            List<List<Direction>> operatorData = new ArrayList<>();

            for (int index = 0; index < currentTest.maps.size(); index++) {
                // 地图数据转换，将每个地图映射到标准类型后储存
                mapData.add(MapFileParser.parseMapData(currentTest.maps.get(index)));
            }
            for (int index = 0; index < currentTest.moves.size(); index++) {
                // 操作序列数据转换，将每一轮操作数据都映射到标准类型后储存
                operatorData.add(currentTest.moves.get(index).stream().map(op -> MapFileParser.parseDirectionChar(op)).toList());
            }

            // 将完成转换的地图加入数据集
            mapSequences.add(mapData);
            operatorSequences.add(operatorData);
        }
    }

    @Test
    public void generalTest() {
        // 对于每轮测试
        for (int testIndex = 0; testIndex < mapSequences.size(); testIndex++) {
            PlayerCore playerCore = new PlayerCore(); // 初始化逻辑核心
            int playerLayer = playerCore.setMap(mapSequences.get(testIndex).getFirst()); // 设置初始地图
            Assert.assertNotEquals(playerLayer, -1, "Player's layer index should exists!");

            // 循环测试每步操作是否正确执行
            for (int step = 0; step < operatorSequences.get(testIndex).size(); step++) {
                MapData previousData = playerCore.getMap(); // 保存前一 step 数据以供测试检查
                for (Direction operator : operatorSequences.get(testIndex).get(step)) playerCore.move(0, operator);
                boolean result = playerCore.getMap().equals(mapSequences.get(testIndex).get(step + 1));
                
                // 如果与测试不一致
                if (!result) {
                    MapFileReader fileHandler = new MapFileReader();

                    // 输出错误比较文件
                    fileHandler.createMapWithContent(
                        String.format("./test-files/wrongResult-testIndex-%d-step-%d.map", testIndex, step - 1), 
                        previousData.toString()
                    );
                    fileHandler.createMapWithContent(
                        String.format("./test-files/wrongResult-testIndex-%d-step-%d.map", testIndex, step), 
                        playerCore.getMap().toString()
                    );
                    fileHandler.createMapWithContent(
                        String.format("./test-files/expectResult-testIndex-%d-step-%d.map", testIndex, step), 
                        mapSequences.get(testIndex).get(step + 1).toString()
                    );

                    Assert.fail(String.format("Two Map should be true! At testIndex = %d, step = %d, check the output map file", testIndex, step));
                }
            }
        }
    }
}
