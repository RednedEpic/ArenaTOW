package io.github.TcFoxy.ArenaTOW.v1_12_R1;


import io.github.TcFoxy.ArenaTOW.Plugin.Utils;
import mc.alk.arena.BattleArena;
import mc.alk.arena.objects.ArenaPlayer;
import net.minecraft.server.v1_13_R1.DamageSource;
import net.minecraft.server.v1_13_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R1.World;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class MyRedZombie extends MyEntityZombie {
	
	private String zombieteam = "Red";
	
	public MyRedZombie(World world) {
		super(world);
	}
	
	@Override
	protected void n(){
		super.n();
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<MyBlueZombie>(this, MyBlueZombie.class, true));
		this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<MyBlueGolem>(this, MyBlueGolem.class, true));
		this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<MyBlueGuardian>(this, MyBlueGuardian.class, true));
	}
	
	@Override
	public void whereTo(Location directions) {
		super.whereTo(directions);
	}
	
	public boolean damageEntity(DamageSource damagesource, float f){
		if(damagesource.getEntity() != null){
			if(damagesource.getEntity().getClass().getName() == NMSConstants.entityPlayer){
				Player p = (Player) damagesource.getEntity().getBukkitEntity();
				ArenaPlayer ap = BattleArena.toArenaPlayer(p);
				String arenateam = ap.getTeam().getDisplayName();
				if(arenateam.equals(Utils.toSimpleColor(Color.RED))){
					return false;
				}else{ super.damageEntity(damagesource, f);}
			} else {super.damageEntity(damagesource, f);}
		}super.damageEntity(damagesource, f);
		return true;
	}

	public String getTeam(){
		return zombieteam;
	}
}