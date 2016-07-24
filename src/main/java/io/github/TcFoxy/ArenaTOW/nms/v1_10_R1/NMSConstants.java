package io.github.TcFoxy.ArenaTOW.nms.v1_10_R1;

import net.minecraft.server.v1_10_R1.Entity;

public class NMSConstants {


	public static final String 
	MyRedZombie = "io.github.TcFoxy.ArenaTOW.nms.v1_10_R1.MyRedZombie",
	MyBlueZombie = "io.github.TcFoxy.ArenaTOW.nms.v1_10_R1.MyBlueZombie",
	MyRedGolem = "io.github.TcFoxy.ArenaTOW.nms.v1_10_R1.MyRedGolem",
	MyBlueGolem = "io.github.TcFoxy.ArenaTOW.nms.v1_10_R1.MyBlueGolem",
	MyRedGuardian = "io.github.TcFoxy.ArenaTOW.nms.v1_10_R1.MyRedGuardian",
	MyBlueGuardian = "io.github.TcFoxy.ArenaTOW.nms.v1_10_R1.MyBlueGuardian",
	entityPlayer = "net.minecraft.server.v1_10_R1.EntityPlayer",
	spigotZombie = "org.bukkit.craftbukkit.v1_10_R1.entity.CraftZombie",
	spigotGolem = "org.bukkit.craftbukkit.v1_10_R1.entity.CraftIronGolem",
	spigotGuardian = "org.bukkit.craftbukkit.v1_10_R1.entity.CraftGuardian",
	entitySmallFireball = "net.minecraft.server.v1_10_R1.EntitySmallFireball",
	myFireball = "io.github.TcFoxy.ArenaTOW.nms.v1_10_R1.MyFireball",
	spigotFireball = "org.bukkit.craftbukkit.v1_10_R1.entity.CraftSmallFireball";
	
	public static boolean isSameTeam(Entity attacker, Entity target){
		String source = attacker.getClass().getName().toString();
		switch(source){
		case MyRedZombie:
			return isRedTeam(target);
		case MyRedGolem:
			return isRedTeam(target);
		case MyRedGuardian:
			return isRedTeam(target);
		case MyBlueZombie:
			return isBlueTeam(target);
		case MyBlueGolem:
			return isBlueTeam(target);
		case MyBlueGuardian:
			return isBlueTeam(target);
		default:
			return false;
		}
	}
	
	private static boolean isRedTeam(Entity target){
		String str = target.getClass().getName().toString();
		switch(str){
		case MyRedGolem:
			return true;
		case MyRedGuardian:
			return true;
		case MyRedZombie:
			return true;
		default:
			return false;
		}
	}
	
	private static boolean isBlueTeam(Entity target){
		String str = target.getClass().getName().toString();
		switch(str){
		case MyBlueGolem:
			return true;
		case MyBlueGuardian:
			return true;
		case MyBlueZombie:
			return true;
		default:
			return false;
		}
	}
}
