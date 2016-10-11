package io.github.TcFoxy.ArenaTOW.BattleArena.objects.spawns;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class SpawnGroup extends SpawnInstance implements Spawnable{
	String name;
	List<SpawnInstance> spawns = new ArrayList<SpawnInstance>();
	public SpawnGroup(String name) {
		super(null);
		this.name = name;
	}

	public SpawnGroup(SpawnInstance spawn) {
		super(null);
		spawns.add(spawn);
	}

	public void addSpawns(List<SpawnInstance> _spawns) {
//		System.out.println(" adding all spawns " + _spawns);
		spawns.addAll(_spawns);
	}

	public String getName(){
		return name;
	}

	@Override
    public void despawn() {
		for (Spawnable spawn: spawns){
			spawn.despawn();
		}
	}

	@Override
    public void spawn() {
		for (Spawnable spawn: spawns){
			spawn.spawn();
		}
	}

	@Override
	public void setLocation(Location l) {
		this.loc = l;
		for (SpawnInstance spawn: spawns){
			spawn.setLocation(l);
		}
	}

	public String toString(){
		StringBuilder sb = new StringBuilder("[SpawnGroup "+name +" ");
		boolean first = true;
		for (Spawnable spawn: spawns){
			if (!first) sb.append(", ");
			else first = false;
			sb.append(spawn.toString());
		}
		sb.append("]");
		return sb.toString();
	}


}
