package io.github.TcFoxy.ArenaTOW.BattleArena.objects.spawns;


import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.github.TcFoxy.ArenaTOW.BattleArena.BattleArena;
import io.github.TcFoxy.ArenaTOW.BattleArena.util.SerializerUtil;


public class TimedSpawn implements Spawnable{
	static int count=0;
	SpawnInstance sg;
	final int id = count++;

	Long firstSpawnTime, respawnInterval, timeToDespawn;
    BukkitTask despawnTimer;

	public TimedSpawn(long firstSpawnTime, long respawnTime, long timeToDespawn, SpawnInstance sg){
		this.firstSpawnTime = firstSpawnTime;
		this.sg = sg;
		this.respawnInterval = respawnTime;
		this.timeToDespawn = timeToDespawn;

	}

	public Long getRespawnTime() {
		return respawnInterval;
	}

	public void setRespawnTime(Long timeToNext) {
		this.respawnInterval = timeToNext;
	}

	public Long getFirstSpawnTime() {
		return firstSpawnTime;
	}

	public void setFirstSpawnTime(Long timeToStart) {
		this.firstSpawnTime = timeToStart;
	}

	public int getId(){
		return id;
	}

	public SpawnInstance getSpawn() {
		return sg;
	}

	public Long getTimeToDespawn() {
		return timeToDespawn;
	}

    @Override
    public void despawn() {
        sg.despawn();
    }

    @Override
    public void spawn() {
        sg.spawn();
        if (timeToDespawn > 0){
            if (despawnTimer != null)
                Bukkit.getScheduler().cancelTask(despawnTimer.getTaskId());
            despawnTimer = new BukkitRunnable() {
                @Override
                public void run() {
                    despawn();
                }
            }.runTaskLater(BattleArena.getSelf(),timeToDespawn*20L);

        }
    }

    @Override
	public String toString() {
        return "[" + id + " loc=" + SerializerUtil.getBlockLocString(sg.getLocation()) + " s=" + sg + " fs=" +
                firstSpawnTime + " rs=" + respawnInterval + " ds=" + timeToDespawn+"]";
    }
    public String getDisplayName() {
        return "&e["+ sg+" loc=&6" + SerializerUtil.getBlockLocString(sg.getLocation()) + "&e fs=&5" +
                firstSpawnTime + "&6 rs=" + respawnInterval + "&7 ds=" + timeToDespawn+"]";
    }
}
