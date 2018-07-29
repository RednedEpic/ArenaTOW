package io.github.TcFoxy.ArenaTOW.BattleArena.controllers.plugins;


import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import io.github.TcFoxy.ArenaTOW.BattleArena.objects.ArenaPlayer;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.MatchParams;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.WinLossDraw;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.stats.ArenaStat;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.stats.BlankArenaStat;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.teams.ArenaTeam;
import io.github.TcFoxy.ArenaTOW.BattleArena.util.BTInterface;

public class TrackerController {
	static boolean enabled;
	final MatchParams mp;
	public static final BlankArenaStat BLANK_STAT = BlankArenaStat.BLANK_STAT;

	public TrackerController(MatchParams mp) {
		this.mp = mp;
	}

	public static boolean enabled() {
		return enabled;
	}

	public static void setPlugin(Plugin plugin) {
		BTInterface.setTrackerPlugin(plugin);
		enabled = true;
	}

	public static boolean hasInterface(MatchParams mp) {
        return enabled && BTInterface.hasInterface(mp);
    }

	public static void resumeTracking(ArenaPlayer p) {
		if (enabled)
			BTInterface.resumeTracking(p);
	}

	public static void stopTracking(ArenaPlayer p) {
		if (enabled)
			BTInterface.stopTracking(p);
	}

	public static void stopTrackingMessages(ArenaPlayer p) {
		if (enabled)
			BTInterface.stopTrackingMessages(p);
	}
	public static void resumeTrackingMessages(ArenaPlayer p) {
		if (enabled)
			BTInterface.resumeTrackingMessages(p);
	}

	public void addRecord(ArenaPlayer victor,ArenaPlayer loser, WinLossDraw wld) {
		if (!enabled)
			return;
		BTInterface.addRecord(mp, victor, loser, wld);
	}

	public void addRecord(Set<ArenaTeam> victors,Set<ArenaTeam> losers,
			Set<ArenaTeam> drawers, WinLossDraw wld, boolean teamRating) {
		if (!enabled)
			return;

		BTInterface.addRecord(mp, victors, losers, drawers, wld, teamRating);
	}

	public ArenaStat loadRecord(ArenaTeam t) {
		return loadRecord(mp,t);
	}

	public static ArenaStat loadRecord(MatchParams mp, ArenaTeam t) {
		if (!enabled || mp == null) return BLANK_STAT;
		return BTInterface.loadRecord(mp.getDBTableName(),t);
	}

	public ArenaStat loadRecord(ArenaPlayer ap) {
		return loadRecord(mp,ap);
	}

	public static ArenaStat loadRecord(MatchParams mp, ArenaPlayer ap) {
		if (!enabled) return BLANK_STAT;
		return BTInterface.loadRecord(mp.getDBTableName(),ap);
	}

	public void resetStats() {
		if (!enabled) return;
		BTInterface bti = new BTInterface(mp);
		if (bti.isValid())
			 bti.resetStats();
	}

	public boolean setRating(OfflinePlayer player, int rating) {
		BTInterface bti = new BTInterface(mp);
		return bti.isValid() && bti.setRating(player, rating);
	}

	public String getRankMessage(OfflinePlayer player) {
		BTInterface bti = new BTInterface(mp);
		return bti.isValid() ? bti.getRankMessage(player) : "";
	}

	public void printTopX(CommandSender sender, int x, int minTeamSize,String headerMsg, String bodyMsg) {
		BTInterface bti = new BTInterface(mp);
		if (!bti.isValid())
			return;
		bti.printTopX(sender, x, minTeamSize, headerMsg, bodyMsg);
	}

}
