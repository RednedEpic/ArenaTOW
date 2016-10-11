package io.github.TcFoxy.ArenaTOW.scoreboard.scoreboard;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import io.github.TcFoxy.ArenaTOW.scoreboard.scoreboard.api.SEntry;
import io.github.TcFoxy.ArenaTOW.scoreboard.scoreboard.api.STeam;

public class Handler {
	static int ids = 0;
	HashMap<Integer,SEntry> row = new HashMap<Integer,SEntry>();
	HashMap<String,Integer> idmap = new HashMap<String,Integer>();

	public SEntry getOrCreateEntry(OfflinePlayer p) {
		return getOrCreateEntry(p,p.getName());
	}

	public SEntry getOrCreateEntry(OfflinePlayer p, String displayName) {
        SEntry e = getEntry(p);
        if (e == null){
			Integer realid = ids++;
			idmap.put(p.getName(), realid);
			e = new SAPIPlayerEntry(p,displayName);
			row.put(realid, e);
		}
        return e;
	}

	public void registerEntry(SEntry entry){
		if (!contains(entry.getID())){
			Integer realid = ids++;
			idmap.put(entry.getID(), realid);
			row.put(realid, entry);
		}
	}

	public SEntry getOrCreateEntry(String id, String displayName) {
		if (!contains(id)){
			Integer realid = ids++;
			idmap.put(id, realid);
			Player p = Bukkit.getPlayerExact(id);
			SEntry l = p == null ? new SAPIEntry(id,displayName) : new SAPIPlayerEntry(p,displayName);
			row.put(realid, l);
			return l;
		} else {
			return getEntry(id);
		}
	}

	public SEntry removeEntry(SEntry e) {
		Integer id = idmap.remove(e.getID());
		if (id != null){
			return row.remove(id);
		}
		return null;
	}

	public SEntry removeEntry(Player p) {
		Integer id = idmap.remove(p.getName());
		if (id != null){
			return row.remove(id);
		}
		return null;
	}

	public STeam getTeamEntry(String id) {
		SEntry e = getEntry(id);
		return e == null || !(e instanceof STeam) ? null : (STeam) e;
	}

    public boolean contains(String id) {
        return idmap.containsKey(id) && row.containsKey(idmap.get(id));
    }

    public boolean contains(OfflinePlayer p) {
        return idmap.containsKey(p.getName()) && row.containsKey(idmap.get(p.getName()));
    }

	public SEntry getEntry(OfflinePlayer p) {
		return (!idmap.containsKey(p.getName())) ? null : row.get(idmap.get(p.getName()));
	}

	public SEntry getEntry(String id) {
		return (!idmap.containsKey(id)) ? null : row.get(idmap.get(id));
	}

	public Collection<SEntry> getEntries() {
		return new ArrayList<SEntry>(row.values());
	}



}
