package io.github.TcFoxy.ArenaTOW.v1_10_R1;

import io.github.TcFoxy.ArenaTOW.API.TOWEntity;
import net.minecraft.server.v1_10_R1.*;

public class MyPathfinderGoalHurtByTarget extends PathfinderGoalHurtByTarget {

    TOWEntity attacker;

    public MyPathfinderGoalHurtByTarget(EntityCreature entitycreature, boolean flag, Class<?>[] aclass) {
        super(entitycreature, flag, aclass);

        this.attacker = (TOWEntity) entitycreature;
    }

    @Override
    public boolean b() {
        final EntityLiving goalTarget = this.e.getLastDamager();
        if (goalTarget == null) {
            return false;
        }
        if (!goalTarget.isAlive()) {
            return false;
        }
        if(goalTarget instanceof TOWEntity && this.attacker.isSameTeam(goalTarget)) {
            return  false;
        }
        return true;
    }

}