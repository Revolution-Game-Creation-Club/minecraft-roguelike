package greymerk.roguelike.dungeon.rooms.prototype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import greymerk.roguelike.dungeon.base.DungeonBase;
import greymerk.roguelike.dungeon.base.IDungeonRoom;
import greymerk.roguelike.dungeon.rooms.RoomSetting;
import greymerk.roguelike.dungeon.settings.LevelSettings;
import greymerk.roguelike.theme.ITheme;
import greymerk.roguelike.util.DyeColor;
import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IBlockFactory;
import greymerk.roguelike.worldgen.IStair;
import greymerk.roguelike.worldgen.IWorldEditor;
import greymerk.roguelike.worldgen.MetaBlock;
import greymerk.roguelike.worldgen.blocks.BlockType;
import greymerk.roguelike.worldgen.blocks.ColorBlock;
import greymerk.roguelike.worldgen.shapes.RectHollow;
import greymerk.roguelike.worldgen.shapes.RectSolid;

import static greymerk.roguelike.treasure.Treasure.MUSIC;
import static greymerk.roguelike.treasure.Treasure.createChests;

public class DungeonsMusic extends DungeonBase {

  public DungeonsMusic(RoomSetting roomSetting) {
    super(roomSetting);
  }

  public IDungeonRoom generate(IWorldEditor editor, Random rand, LevelSettings settings, Coord origin, Cardinal[] entrances) {
    ITheme theme = settings.getTheme();
    IBlockFactory wall = theme.getPrimary().getWall();
    IStair stair = theme.getSecondary().getStair();
    IBlockFactory panel = theme.getSecondary().getWall();
    IBlockFactory pillar = theme.getSecondary().getPillar();
    MetaBlock air = BlockType.get(BlockType.AIR);
    IBlockFactory floor = theme.getSecondary().getFloor();

    Coord start;
    Coord end;
    Coord cursor;

    List<Coord> chests = new ArrayList<>();

    start = new Coord(origin);
    end = new Coord(origin);
    start.add(new Coord(-6, -1, -6));
    end.add(new Coord(6, 5, 6));
    RectHollow.fill(editor, rand, start, end, wall, false, true);

    start = new Coord(origin);
    end = new Coord(origin);
    start.add(new Coord(-6, 4, -6));
    end.add(new Coord(6, 5, 6));
    RectSolid.fill(editor, rand, start, end, panel, true, true);

    start = new Coord(origin);
    end = new Coord(origin);
    start.add(new Coord(-3, 4, -3));
    end.add(new Coord(3, 4, 3));
    RectSolid.fill(editor, rand, start, end, air);

    start = new Coord(origin);
    end = new Coord(origin);
    start.add(new Coord(-3, -1, -3));
    end.add(new Coord(3, -1, 3));
    RectSolid.fill(editor, rand, start, end, floor, true, true);

    List<DyeColor> colors = Arrays.asList(DyeColor.values());
    Collections.shuffle(colors, rand);
    for (int i = 2; i >= 0; --i) {
      start = new Coord(origin);
      end = new Coord(origin);
      start.add(new Coord(-i - 1, 0, -i - 1));
      end.add(new Coord(i + 1, 0, i + 1));
      MetaBlock carpet = ColorBlock.get(ColorBlock.CARPET, colors.get(i));
      RectSolid.fill(editor, rand, start, end, carpet);
    }

    for (Cardinal dir : Cardinal.directions) {

      cursor = new Coord(origin);
      cursor.add(dir, 5);
      cursor.add(Cardinal.UP, 3);
      panel.set(editor, rand, cursor);
      cursor.add(dir.reverse());
      stair.setOrientation(dir.reverse(), true).set(editor, cursor);

      cursor = new Coord(origin);
      cursor.add(dir, 5);
      cursor.add(dir.left(), 5);
      pillar(editor, rand, settings, cursor);

      start = new Coord(origin);
      start.add(Cardinal.UP, 4);
      start.add(dir, 3);
      end = new Coord(start);
      start.add(dir.left(), 3);
      end.add(dir.right(), 3);
      RectSolid.fill(editor, rand, start, end, pillar, true, true);

      cursor = new Coord(origin);
      cursor.add(Cardinal.UP, 4);
      cursor.add(dir);
      stair.setOrientation(dir, true).set(editor, cursor);
      cursor.add(dir);
      stair.setOrientation(dir.reverse(), true).set(editor, cursor);

      for (Cardinal o : dir.orthogonal()) {
        cursor = new Coord(origin);
        cursor.add(dir, 5);
        cursor.add(o, 2);
        pillar(editor, rand, settings, cursor);

        cursor = new Coord(origin);
        cursor.add(dir, 4);
        cursor.add(Cardinal.UP, 3);
        cursor.add(o);
        stair.setOrientation(dir.reverse(), true).set(editor, cursor);

        cursor = new Coord(origin);
        cursor.add(dir, 5);
        cursor.add(o, 3);
        cursor.add(Cardinal.UP);
        chests.add(new Coord(cursor));
        cursor.add(o);
        chests.add(new Coord(cursor));

        cursor = new Coord(origin);
        cursor.add(dir, 5);
        cursor.add(o, 3);
        stair.setOrientation(dir.reverse(), true).set(editor, cursor);
        cursor.add(o);
        stair.setOrientation(dir.reverse(), true).set(editor, cursor);
        cursor.add(Cardinal.UP, 2);
        stair.setOrientation(o.reverse(), true).set(editor, cursor);
        cursor.add(o.reverse());
        stair.setOrientation(o, true).set(editor, cursor);
        cursor.add(Cardinal.UP);
        panel.set(editor, rand, cursor);
        cursor.add(o);
        panel.set(editor, rand, cursor);
        cursor.add(dir.reverse());
        stair.setOrientation(dir.reverse(), true).set(editor, cursor);
        cursor.add(o.reverse());
        stair.setOrientation(dir.reverse(), true).set(editor, cursor);

      }
    }

    BlockType.get(BlockType.JUKEBOX).set(editor, origin);

    cursor = new Coord(origin);
    cursor.add(Cardinal.UP, 4);
    BlockType.get(BlockType.GLOWSTONE).set(editor, cursor);

    List<Coord> chestLocations = chooseRandomLocations(rand, 1, chests);
    createChests(editor, rand, settings.getDifficulty(origin), chestLocations, false, MUSIC);

    return this;
  }

  private void pillar(IWorldEditor editor, Random rand, LevelSettings settings, Coord origin) {
    ITheme theme = settings.getTheme();
    IStair stair = theme.getSecondary().getStair();
    IBlockFactory panel = theme.getSecondary().getWall();
    IBlockFactory pillar = theme.getSecondary().getPillar();

    Coord start;
    Coord end;
    Coord cursor;

    start = new Coord(origin);
    end = new Coord(start);
    end.add(Cardinal.UP, 2);
    RectSolid.fill(editor, rand, start, end, pillar);
    for (Cardinal dir : Cardinal.directions) {
      cursor = new Coord(end);
      cursor.add(dir);
      stair.setOrientation(dir, true).set(editor, rand, cursor, true, false);
      cursor.add(Cardinal.UP);
      panel.set(editor, rand, cursor);
    }
  }

  public int getSize() {
    return 7;
  }

}