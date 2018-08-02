package io.github.TcFoxy.ArenaTOW.NMS.v1_12_R1;

import net.minecraft.server.v1_13_R1.*;

import java.lang.reflect.Field;

class MyEntityGuardian extends EntityGuardianElder{


	public MyEntityGuardian(World world) {
		super(world);

	}

	@Override
	protected void n(){
		NMSUtils.clearBehavior(goalSelector, targetSelector);

		this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, true));
		this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		this.goalSelector.a(4, new PathfinderGoalGuardianAttack(this, 16F));

		//We have to include these for the beam particles
		this.goalRandomStroll = new PathfinderGoalRandomStroll(this, 1.0, 80);
		this.goalRandomStroll.a(3);
	}

	@Override
	public void initAttributes()
	{
		super.initAttributes();
		getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(10.0D);
		getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(16.0D);
		getAttributeInstance(GenericAttributes.maxHealth).setValue(300.0D);
	}

	@Override
	public void move(EnumMoveType type, double d0, double d1, double d2){
	}


	/*
	 * This gets the DataWatcherObject<Integer> called bG
	 * in the entityguardian class which is private
	 */
	@SuppressWarnings("unchecked")
	public DataWatcherObject<Integer> getFieldB() throws Exception{
		Field f=EntityGuardian.class.getDeclaredField("bG");
		f.setAccessible(true);
		DataWatcherObject<Integer> temp = (DataWatcherObject<Integer>) f.get((EntityGuardian)this);
		return temp;
	}

	private void a(final int n) {
		DataWatcherObject<Integer> bG;
		try {
			bG = getFieldB();
			this.datawatcher.set(bG, n);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int cooldown() {
		return 30; //20 ticks per second
	}


	static class PathfinderGoalGuardianAttack extends PathfinderGoal
	{
		private final MyEntityGuardian a;
		private int b;
		private final boolean c;
		private float damage;

		public PathfinderGoalGuardianAttack(final MyEntityGuardian a, float damage) {
			this.a = a;
			this.c = (a instanceof EntityGuardianElder);
			this.a(3);
			this.damage = damage;
		}

		@Override
		public boolean a() {
			final EntityLiving goalTarget = this.a.getGoalTarget();
			return goalTarget != null && goalTarget.isAlive();
		}

		@Override
		public boolean b() {
			return super.b() && (this.c || this.a.h(this.a.getGoalTarget()) > 9.0);
		}

		@Override
		public void c() {
			this.b = -10;
			this.a.getNavigation().q();
			this.a.getControllerLook().a(this.a.getGoalTarget(), 90.0f, 90.0f);
			this.a.impulse = true;
		}

		@Override
		public void d() {
			this.a.a(0);
			this.a.setGoalTarget(null);
			this.a.goalRandomStroll.i();
		}

		@Override
		public void e() {
			final EntityLiving goalTarget = this.a.getGoalTarget();
			this.a.getNavigation().q();
			this.a.getControllerLook().a(goalTarget, 90.0f, 90.0f);
			if (!this.a.hasLineOfSight(goalTarget)) {
				this.a.setGoalTarget(null);
				return;
			}
			++this.b;
			if (this.b == 0) {
				this.a.a(this.a.getGoalTarget().getId());
				this.a.world.broadcastEntityEffect(this.a, (byte)21);
			}
			else if (this.b >= this.a.cooldown()) {
				float f = damage;
				goalTarget.damageEntity(DamageSource.c(this.a, this.a), f);
				goalTarget.damageEntity(DamageSource.mobAttack(this.a), (float)this.a.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue());
				this.a.setGoalTarget(null);
			}
			super.e();
		}
	}
}







































