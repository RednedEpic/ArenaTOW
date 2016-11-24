package io.github.TcFoxy.ArenaTOW.BattleArena.controllers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.TcFoxy.ArenaTOW.BattleArena.events.players.ArenaPlayerLeaveEvent;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.ArenaPlayer;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.MatchParams;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.teams.ArenaTeam;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.teams.FormingTeam;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.teams.TeamFactory;



public enum TeamController implements Listener {
	INSTANCE;

	static final boolean DEBUG = false;

	/** Teams that are created through players wanting to be teams up, or an admin command */
	final Map<UUID, ArenaTeam> selfFormedTeams = Collections.synchronizedMap(new HashMap<UUID, ArenaTeam>());

	/** Teams that are still being created, these aren't "real" teams yet */
	final Set<FormingTeam> formingTeams = Collections.synchronizedSet(new HashSet<FormingTeam>());

	/**
	 * A valid team should either be currently being "handled" or is selfFormed
	 * @param player ArenaPlayer
	 * @return Team
	 */
	public static ArenaTeam getTeam(ArenaPlayer player) {
		ArenaTeam at = INSTANCE.selfFormedTeams.get(player.getID());
        return at;
    }

    public boolean inSelfFormedTeam(ArenaPlayer player){
        return (INSTANCE.selfFormedTeams.containsKey(player.getID()));
    }

	public ArenaTeam getSelfFormedTeam(ArenaPlayer player) {
		return getTeam(player);
	}

    public static Collection<ArenaTeam> getSelfFormedTeams() {
        return INSTANCE.selfFormedTeams.values();
    }

	public boolean removeSelfFormedTeam(ArenaTeam team) {
        List<UUID> l = new ArrayList<UUID>();
        for (Map.Entry<UUID, ArenaTeam> entry : selfFormedTeams.entrySet()) {
            if (entry.getValue().equals(team)){
                l.add(entry.getKey());
            }
        }
        for (UUID p: l) {
            selfFormedTeams.remove(p);
        }
        return !l.isEmpty();
    }

	public void addSelfFormedTeam(ArenaTeam team) {
        for (ArenaPlayer ap: team.getPlayers()){
            selfFormedTeams.put(ap.getID(), team);
        }
	}

	private void leaveSelfTeam(ArenaPlayer p) {
        if (inFormingTeam(p)){
            FormingTeam t = getFormingTeam(p);
            if (t != null && formingTeams.remove(t)){
                t.sendMessage("&cYou're team has been disbanded as &6" + p.getDisplayName()+"&c has left minecraft");
                return;
            }
        }
        if (inSelfFormedTeam(p)){
            ArenaTeam t = getSelfFormedTeam(p);
            if (t != null && this.removeSelfFormedTeam(t)){
                t.sendMessage("&cYou're team has been disbanded as &6" + p.getDisplayName()+"&c has left minecraft");
            }
        }
	}

	@EventHandler
	public void onPlayerLeave(ArenaPlayerLeaveEvent event) {
		leaveSelfTeam(event.getPlayer());
	}

	public boolean inFormingTeam(ArenaPlayer p) {
		for (FormingTeam ft: formingTeams){
			if (ft.hasMember(p)){
				return true;}
		}
		return false;
	}

	public FormingTeam getFormingTeam(ArenaPlayer p) {
		for (FormingTeam ft: formingTeams){
			if (ft.hasMember(p)){
				return ft;}
		}
		return null;
	}

	public void addFormingTeam(FormingTeam ft) {
		formingTeams.add(ft);
	}

	public boolean removeFormingTeam(FormingTeam ft) {
		return formingTeams.remove(ft);
	}
	public static ArenaTeam createTeam(MatchParams mp, ArenaPlayer p) {
		if (DEBUG) System.out.println("------- createTeam sans handler " + p.getName());
		return TeamFactory.createCompositeTeam(-1, mp, p);
	}

	@Override
	public String toString(){
		return "[TeamController]";
	}
}
