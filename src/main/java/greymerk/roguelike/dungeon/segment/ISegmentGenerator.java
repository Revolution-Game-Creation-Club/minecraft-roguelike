package greymerk.roguelike.dungeon.segment;

import java.util.Random;

import greymerk.roguelike.dungeon.IDungeonLevel;
import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.WorldEditor;

public interface ISegmentGenerator {
	
	public void genSegment(WorldEditor editor, Random rand, IDungeonLevel level, Cardinal dir, Coord pos);
	
}
