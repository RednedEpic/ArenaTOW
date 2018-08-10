package io.github.TcFoxy.ArenaTOW.v1_12_R1;


import io.github.TcFoxy.ArenaTOW.API.TOWEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Color;


class MyRedGolem extends MyEntityGolem implements TOWEntity {
	public MyRedGolem(World world) {
		super(world);
	    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<MyBlueZombie>(this, MyBlueZombie.class, true));
	}

	@Override
	public Color getTeam() {
	    return Color.RED;
	}

//	public boolean damageEntity(DamageSource damagesource, float f){
//		if(damagesource.getEntity() != null){
//			if(damagesource.getEntity().getClass().getName() == NMSConstants.entityPlayer){
//				Player p = (Player) damagesource.getEntity().getBukkitEntity();
//				ArenaPlayer ap = BattleArena.toArenaPlayer(p);
//				String arenateam = ap.getTeam().getDisplayName();
//				if(arenateam.equals(Utils.toSimpleColor(Color.RED))){
//					return false;
//				}else{ super.damageEntity(damagesource, f);}
//			} else {super.damageEntity(damagesource, f);}
//		}super.damageEntity(damagesource, f);
//		return true;
//	}
}
