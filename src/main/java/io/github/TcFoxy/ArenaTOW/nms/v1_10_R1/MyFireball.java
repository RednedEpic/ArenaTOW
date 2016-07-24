package io.github.TcFoxy.ArenaTOW.nms.v1_10_R1;

import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.EntitySmallFireball;
import net.minecraft.server.v1_10_R1.World;

public class MyFireball extends EntitySmallFireball{

	private MyEntityIronGolem golem;
	
	public MyFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
		super(world, entityliving, d0, d1, d2);
		this.golem = (MyEntityIronGolem) entityliving;
	}
	
	public MyEntityIronGolem getGolem(){
		return this.golem;
	}

}
