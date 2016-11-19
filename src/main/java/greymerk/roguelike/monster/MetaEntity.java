package greymerk.roguelike.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class MetaEntity implements IEntity {

	private Entity mob;
	
	public MetaEntity(Entity mob){
		this.mob = mob;
	}
	
	@Override
	public void setSlot(EntityEquipmentSlot slot, ItemStack item) {
		mob.setItemStackToSlot(slot, item);
	}

	@Override
	public void setMobClass(MobType type, boolean clear) {
		
		EntityLiving oldMob = (EntityLiving)this.mob;
		EntityLiving newMob = (EntityLiving)MobType.getEntity(this.mob.worldObj, type);

		newMob.copyLocationAndAnglesFrom(oldMob);
		
		this.mob = (Entity)newMob;
		
		if(oldMob instanceof EntityZombie){
			((EntityZombie)newMob).setChild(((EntityZombie)oldMob).isChild());
		}
		
		for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()){
			ItemStack toTrade = oldMob.getItemStackFromSlot(slot);
			newMob.setItemStackToSlot(slot, toTrade);
		}
		
		
		oldMob.worldObj.removeEntity(oldMob);
		newMob.worldObj.spawnEntityInWorld(newMob);
	}

	@Override
	public void setChild(boolean child) {
		if(!(this.mob instanceof EntityZombie)) return;
		
		((EntityZombie)this.mob).setChild(child);
	}

	@Override
	public boolean instance(Class<?> type) {
		return type.isInstance(this.mob);
	}

}
