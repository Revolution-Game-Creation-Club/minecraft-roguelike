package greymerk.roguelike.dungeon.segment.part;

import java.util.Random;

import greymerk.roguelike.dungeon.DungeonLevel;
import greymerk.roguelike.theme.ITheme;
import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IBlockFactory;
import greymerk.roguelike.worldgen.MetaBlock;
import greymerk.roguelike.worldgen.WorldEditor;
import greymerk.roguelike.worldgen.blocks.BlockType;
import greymerk.roguelike.worldgen.shapes.RectSolid;

public class SegmentSquareArch extends SegmentBase {

  @Override
  protected void genWall(WorldEditor editor, Random rand, DungeonLevel level, Cardinal dir, ITheme theme, Coord origin) {

    Coord start;
    Coord end;

    MetaBlock air = BlockType.get(BlockType.AIR);
    IBlockFactory pillar = level.getSettings().getTheme().getPrimary().getPillar();

    start = new Coord(origin);
    start.translate(dir, 2);
    end = new Coord(start);
    end.translate(Cardinal.UP, 2);
    RectSolid.fill(editor, rand, start, end, air);

    start = new Coord(origin);
    start.translate(dir, 3);
    end = new Coord(start);
    end.translate(Cardinal.UP, 2);
    RectSolid.fill(editor, rand, start, end, pillar);

    for (Cardinal orth : dir.orthogonal()) {
      start = new Coord(origin);
      start.translate(orth, 1);
      start.translate(dir, 2);
      end = new Coord(start);
      end.translate(Cardinal.UP, 2);
      RectSolid.fill(editor, rand, start, end, pillar);
    }
  }
}
