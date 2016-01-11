package greymerk.roguelike.dungeon.rooms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import greymerk.roguelike.dungeon.base.DungeonBase;
import greymerk.roguelike.dungeon.settings.LevelSettings;
import greymerk.roguelike.theme.ITheme;
import greymerk.roguelike.treasure.Treasure;
import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IBlockFactory;
import greymerk.roguelike.worldgen.IStair;
import greymerk.roguelike.worldgen.IWorldEditor;
import greymerk.roguelike.worldgen.MetaBlock;
import greymerk.roguelike.worldgen.blocks.BlockType;
import greymerk.roguelike.worldgen.blocks.ColorBlock;
import greymerk.roguelike.worldgen.blocks.DyeColor;

public class DungeonsMusic extends DungeonBase {

	public boolean generate(IWorldEditor editor, Random rand, LevelSettings settings, Cardinal[] entrances, Coord origin) {
		ITheme theme = settings.getTheme();
		IBlockFactory wall = theme.getPrimaryWall();
		IStair stair = theme.getSecondaryStair();
		IBlockFactory panel = theme.getSecondaryWall();
		IBlockFactory pillar = theme.getSecondaryPillar();
		MetaBlock air = BlockType.get(BlockType.AIR);
		IBlockFactory floor = theme.getSecondaryFloor();
		
		Coord start;
		Coord end;
		Coord cursor;
		
		List<Coord> chests = new ArrayList<Coord>();
		
		start = new Coord(origin);
		end = new Coord(origin);
		start.add(new Coord(-6, -1, -6));
		end.add(new Coord(6, 5, 6));
		wall.fillRectHollow(editor, rand, start, end, false, true);

		start = new Coord(origin);
		end = new Coord(origin);
		start.add(new Coord(-6, 4, -6));
		end.add(new Coord(6, 5, 6));
		panel.fillRectSolid(editor, rand, start, end, true, true);
		
		start = new Coord(origin);
		end = new Coord(origin);
		start.add(new Coord(-3, 4, -3));
		end.add(new Coord(3, 4, 3));
		air.fillRectSolid(editor, rand, start, end, true, true);
		
		start = new Coord(origin);
		end = new Coord(origin);
		start.add(new Coord(-3, -1, -3));
		end.add(new Coord(3, -1, 3));
		floor.fillRectSolid(editor, rand, start, end, true, true);
		
		List<DyeColor> colors = Arrays.asList(DyeColor.values());
		Collections.shuffle(colors, rand);
		for(int i = 2; i >= 0; --i){
			start = new Coord(origin);
			end = new Coord(origin);
			start.add(new Coord(-i - 1, 0, -i - 1));
			end.add(new Coord(i + 1, 0, i + 1));
			MetaBlock carpet = ColorBlock.get(ColorBlock.CARPET, colors.get(i));
			carpet.fillRectSolid(editor, rand, start, end, true, true);
		}
		
		for(Cardinal dir : Cardinal.directions){
			Cardinal[] orth = Cardinal.orthogonal(dir);
			
			cursor = new Coord(origin);
			cursor.add(dir, 5);
			cursor.add(Cardinal.UP, 3);
			panel.setBlock(editor, rand, cursor);
			cursor.add(Cardinal.reverse(dir));
			stair.setOrientation(Cardinal.reverse(dir), true).setBlock(editor, cursor);
			
			cursor = new Coord(origin);
			cursor.add(dir, 5);
			cursor.add(orth[0], 5);
			pillar(editor, rand, settings, cursor);
			
			start = new Coord(origin);
			start.add(Cardinal.UP, 4);
			start.add(dir, 3);
			end = new Coord(start);
			start.add(orth[0], 3);
			end.add(orth[1], 3);
			pillar.fillRectSolid(editor, rand, start, end, true, true);
			
			cursor = new Coord(origin);
			cursor.add(Cardinal.UP, 4);
			cursor.add(dir);
			stair.setOrientation(dir, true).setBlock(editor, cursor);
			cursor.add(dir);
			stair.setOrientation(Cardinal.reverse(dir), true).setBlock(editor, cursor);
			
			for(Cardinal o : orth){
				cursor = new Coord(origin);
				cursor.add(dir, 5);
				cursor.add(o, 2);
				pillar(editor, rand, settings, cursor);
				
				cursor = new Coord(origin);
				cursor.add(dir, 4);
				cursor.add(Cardinal.UP, 3);
				cursor.add(o);
				stair.setOrientation(Cardinal.reverse(dir), true).setBlock(editor, cursor);
				
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
				stair.setOrientation(Cardinal.reverse(dir), true).setBlock(editor, cursor);
				cursor.add(o);
				stair.setOrientation(Cardinal.reverse(dir), true).setBlock(editor, cursor);
				cursor.add(Cardinal.UP, 2);
				stair.setOrientation(Cardinal.reverse(o), true).setBlock(editor, cursor);
				cursor.add(Cardinal.reverse(o));
				stair.setOrientation(o, true).setBlock(editor, cursor);
				cursor.add(Cardinal.UP);
				panel.setBlock(editor, rand, cursor);
				cursor.add(o);
				panel.setBlock(editor, rand, cursor);
				cursor.add(Cardinal.reverse(dir));
				stair.setOrientation(Cardinal.reverse(dir), true).setBlock(editor, cursor);
				cursor.add(Cardinal.reverse(o));
				stair.setOrientation(Cardinal.reverse(dir), true).setBlock(editor, cursor);
			
			}
		}
		
		BlockType.get(BlockType.JUKEBOX).setBlock(editor, origin);
		
		cursor = new Coord(origin);
		cursor.add(Cardinal.UP, 4);
		BlockType.get(BlockType.GLOWSTONE).setBlock(editor, cursor);
		
		Treasure.generate(editor, rand, chests, Treasure.MUSIC, settings.getDifficulty(origin));
		
		return false;
	}
	
	private void pillar(IWorldEditor editor, Random rand, LevelSettings settings, Coord origin){
		ITheme theme = settings.getTheme();
		IStair stair = theme.getSecondaryStair();
		IBlockFactory panel = theme.getSecondaryWall();
		IBlockFactory pillar = theme.getSecondaryPillar();
		
		Coord start;
		Coord end;
		Coord cursor;
		
		start = new Coord(origin);
		end = new Coord(start);
		end.add(Cardinal.UP, 2);
		pillar.fillRectSolid(editor, rand, start, end, true, true);
		for(Cardinal dir : Cardinal.directions){
			cursor = new Coord(end);
			cursor.add(dir);
			stair.setOrientation(dir, true).setBlock(editor, rand, cursor, true, false);
			cursor.add(Cardinal.UP);
			panel.setBlock(editor, rand, cursor);
		}
	}

	public int getSize(){
		return 7;
	}
	
}
