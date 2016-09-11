package io.github.TcFoxy.ArenaTOW.Serializable;

import io.github.TcFoxy.ArenaTOW.nms.v1_10_R1.interfaces.NMSUtils;
import net.minecraft.server.v1_10_R1.Entity;

import org.bukkit.Color;
import org.bukkit.Location;

public class Tower extends PersistInfo {

	public Tower(String key, Color teamColor, Location loc, String info) {
		super(key, teamColor, loc, info);
	}
	
	@Override
	public Entity spawnMob(){
		setMob(NMSUtils.spawnTeamGolem(getSpawnLoc().getWorld(), getSpawnLoc().getX(), getSpawnLoc().getY(), getSpawnLoc().getZ(), getTeamColor()));
		return getMob();
	}
	
}