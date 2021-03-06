package greymerk.roguelike.dungeon.rooms.prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import greymerk.roguelike.dungeon.Dungeon;
import greymerk.roguelike.dungeon.base.DungeonBase;
import greymerk.roguelike.dungeon.rooms.RoomSetting;
import greymerk.roguelike.dungeon.settings.LevelSettings;
import greymerk.roguelike.theme.ITheme;
import greymerk.roguelike.treasure.loot.ChestType;
import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IBlockFactory;
import greymerk.roguelike.worldgen.IStair;
import greymerk.roguelike.worldgen.MetaBlock;
import greymerk.roguelike.worldgen.WorldEditor;
import greymerk.roguelike.worldgen.blocks.BlockType;
import greymerk.roguelike.worldgen.shapes.RectHollow;
import greymerk.roguelike.worldgen.shapes.RectSolid;

import static greymerk.roguelike.worldgen.Cardinal.UP;
import static greymerk.roguelike.worldgen.Cardinal.DIRECTIONS;

public class BrickRoom extends DungeonBase {

  public BrickRoom(RoomSetting roomSetting) {
    super(roomSetting);
  }

  public DungeonBase generate(WorldEditor editor, Random rand, LevelSettings settings, Coord origin, List<Cardinal> entrances) {

    int x = origin.getX();
    int y = origin.getY();
    int z = origin.getZ();

    ITheme theme = settings.getTheme();

    IStair stair = theme.getPrimary().getStair();
    IBlockFactory blocks = theme.getPrimary().getWall();
    IBlockFactory pillar = theme.getPrimary().getPillar();
    MetaBlock air = BlockType.get(BlockType.AIR);

    // fill air inside
    RectSolid.fill(editor, rand, new Coord(x - 3, y, z - 3), new Coord(x + 3, y + 3, z + 3), air);
    RectSolid.fill(editor, rand, new Coord(x - 1, y + 4, z - 1), new Coord(x + 1, y + 4, z + 1), air);

    // shell
    RectHollow.fill(editor, rand, new Coord(x - 4, y - 1, z - 4), new Coord(x + 4, y + 4, z + 4), blocks, false, true);

    RectSolid.fill(editor, rand, new Coord(x - 4, y - 1, z - 4), new Coord(x + 4, y - 1, z + 4), theme.getPrimary().getFloor(), false, true);

    Coord start;
    Coord end;
    Coord cursor;


    cursor = new Coord(x, y, z);
    cursor.translate(UP, 5);
    air.set(editor, cursor);
    cursor.translate(UP, 1);
    blocks.set(editor, rand, cursor);

    // Chests
    List<Coord> potentialChestLocations = new ArrayList<>();

    for (Cardinal dir : DIRECTIONS) {

      // top
      cursor = new Coord(x, y, z);
      cursor.translate(dir, 1);
      cursor.translate(UP, 5);
      stair.setOrientation(dir.reverse(), true);
      stair.set(editor, rand, cursor, false, true);
      cursor.translate(dir.antiClockwise(), 1);
      blocks.set(editor, rand, cursor, false, true);

      cursor = new Coord(x, y, z);
      cursor.translate(dir, 2);
      cursor.translate(UP, 4);
      air.set(editor, cursor);
      cursor.translate(UP, 1);
      blocks.set(editor, rand, cursor, false, true);

      // pillar
      cursor = new Coord(x, y, z);
      cursor.translate(dir, 3);
      cursor.translate(dir.antiClockwise(), 3);
      start = new Coord(cursor);
      cursor.translate(UP, 2);
      end = new Coord(cursor);
      RectSolid.fill(editor, rand, start, end, pillar, true, true);
      cursor.translate(UP, 1);
      blocks.set(editor, rand, cursor);

      // pillar stairs
      for (Cardinal orth : dir.orthogonal()) {
        cursor = new Coord(x, y, z);
        cursor.translate(dir, 3);
        cursor.translate(orth, 2);
        cursor.translate(UP, 3);
        stair.setOrientation(orth.reverse(), true);
        stair.set(editor, rand, cursor);
      }

      // layer above pillars
      cursor = new Coord(x, y, z);
      cursor.translate(dir, 2);
      cursor.translate(dir.antiClockwise(), 2);
      cursor.translate(UP, 4);
      blocks.set(editor, rand, cursor, false, true);

      for (Cardinal orth : dir.orthogonal()) {
        cursor = new Coord(x, y, z);
        cursor.translate(UP, 4);
        cursor.translate(dir, 2);
        cursor.translate(orth, 1);
        stair.setOrientation(orth.reverse(), true);
        stair.set(editor, rand, cursor, false, true);
      }

      cursor = new Coord(x, y, z);
      cursor.translate(dir, 1);
      cursor.translate(dir.antiClockwise(), 1);
      cursor.translate(UP, 5);
      blocks.set(editor, rand, cursor, false, true);

      for (Cardinal orth : dir.orthogonal()) {
        cursor = new Coord(x, y, z);
        cursor.translate(dir, 3);
        cursor.translate(orth, 2);
        potentialChestLocations.add(cursor);
      }
    }

    List<Coord> chestLocations = chooseRandomLocations(rand, 1, potentialChestLocations);
    int level = Dungeon.getLevel(origin.getY());
    editor.treasureChestEditor.createChests(level, chestLocations, false, getRoomSetting().getChestType().orElse(ChestType.chooseRandomType(rand, ChestType.COMMON_TREASURES)));

    Coord spawnerLocation = new Coord(x, y, z);
    generateSpawner(editor, rand, spawnerLocation, Dungeon.getLevel(origin.getY()), settings.getSpawners());
    return this;
  }

  public int getSize() {
    return 6;
  }
}
