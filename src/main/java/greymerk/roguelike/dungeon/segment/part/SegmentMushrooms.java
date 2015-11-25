package greymerk.roguelike.dungeon.segment.part;

import greymerk.roguelike.dungeon.IDungeonLevel;
import greymerk.roguelike.theme.ITheme;
import greymerk.roguelike.worldgen.BlockWeightedRandom;
import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IStair;
import greymerk.roguelike.worldgen.MetaBlock;
import greymerk.roguelike.worldgen.WorldEditor;
import greymerk.roguelike.worldgen.blocks.BlockType;
import greymerk.roguelike.worldgen.blocks.FlowerPot;

import java.util.Random;

public class SegmentMushrooms extends SegmentBase {

	private BlockWeightedRandom mushrooms;
	
	
	@Override
	protected void genWall(WorldEditor editor, Random rand, IDungeonLevel level, Cardinal wallDirection, ITheme theme, int x, int y, int z) {
		
		IStair stair = theme.getSecondaryStair();
		MetaBlock air = BlockType.get(BlockType.AIR);
		
		mushrooms = new BlockWeightedRandom();
		mushrooms.addBlock(FlowerPot.getFlower(FlowerPot.REDMUSHROOM), 3);
		mushrooms.addBlock(FlowerPot.getFlower(FlowerPot.BROWNMUSHROOM), 3);
		mushrooms.addBlock(air, 10);
		
		Coord cursor;
		Coord start;
		Coord end;
		
		Cardinal[] orth = Cardinal.getOrthogonal(wallDirection);
		start = new Coord(x, y, z);
		start.add(wallDirection, 2);
		end = new Coord(start);
		start.add(orth[0], 1);
		end.add(orth[1], 1);
		end.add(Cardinal.UP, 1);
		editor.fillRectSolid(rand, start, end, air, true, true);
		start.add(Cardinal.DOWN, 1);
		end.add(Cardinal.DOWN, 2);
		
		editor.fillRectSolid(rand, start, end, BlockType.get(BlockType.MYCELIUM), true, true);
		start.add(Cardinal.UP, 1);
		end.add(Cardinal.UP, 1);
		editor.fillRectSolid(rand, start, end, mushrooms, true, true);
		
		for(Cardinal d : orth){
			cursor = new Coord(x, y, z);
			cursor.add(wallDirection, 2);
			cursor.add(d, 1);
			cursor.add(Cardinal.UP, 1);
			stair.setOrientation(Cardinal.reverse(d), true);
			editor.setBlock(rand, cursor, stair, true, true);
		}

	}
}
