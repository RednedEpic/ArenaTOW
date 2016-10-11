package io.github.TcFoxy.ArenaTOW.BattleArena.controllers.messaging;



import java.util.Collection;
import java.util.List;

import io.github.TcFoxy.ArenaTOW.BattleArena.competition.match.Match;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.ArenaPlayer;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.MatchState;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.messaging.AnnouncementOptions;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.messaging.Channel;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.messaging.Channels;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.messaging.MatchMessageHandler;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.teams.ArenaTeam;
import io.github.TcFoxy.ArenaTOW.BattleArena.util.Log;



public class MatchMessager {
	MatchMessageHandler impl;
	final AnnouncementOptions bos;
	boolean silent = false;

	public MatchMessager(Match match){
		this.impl = new MatchMessageImpl(match);
		this.bos = match.getParams().getAnnouncementOptions();
	}

	private Channel getChannel(MatchState state) {
		if (silent) return Channels.NullChannel;
		return bos != null && bos.hasOption(true,state) ? bos.getChannel(true,state) : AnnouncementOptions.getDefaultChannel(true,state);
	}

	public void sendOnBeginMsg(List<ArenaTeam> teams) {
		try{impl.sendOnBeginMsg(getChannel(MatchState.ONBEGIN), teams);}catch(Exception e){Log.printStackTrace(e);}
	}

	public void sendOnPreStartMsg(List<ArenaTeam> teams) {
		sendOnPreStartMsg(teams, getChannel(MatchState.ONPRESTART));
	}

	public void sendOnPreStartMsg(List<ArenaTeam> teams, Channel serverChannel) {
		try{impl.sendOnPreStartMsg(serverChannel, teams);}catch(Exception e){Log.printStackTrace(e);}
	}

	public void sendOnStartMsg(List<ArenaTeam> teams) {
		try{impl.sendOnStartMsg(getChannel(MatchState.ONSTART), teams);}catch(Exception e){Log.printStackTrace(e);}
	}

	public void sendOnVictoryMsg(Collection<ArenaTeam> winners, Collection<ArenaTeam> losers) {
		try{impl.sendOnVictoryMsg(getChannel(MatchState.ONVICTORY), winners,losers);}catch(Exception e){Log.printStackTrace(e);}
	}

	public void sendOnDrawMessage(Collection<ArenaTeam> drawers, Collection<ArenaTeam> losers) {
		try{impl.sendOnDrawMsg(getChannel(MatchState.ONVICTORY), drawers, losers);}catch(Exception e){Log.printStackTrace(e);}
	}

	public void sendYourTeamNotReadyMsg(ArenaTeam t1) {
		try{impl.sendYourTeamNotReadyMsg(t1);}catch(Exception e){Log.printStackTrace(e);}
	}

	public void sendOtherTeamNotReadyMsg(ArenaTeam t1) {
		try{impl.sendOtherTeamNotReadyMsg(t1);}catch(Exception e){Log.printStackTrace(e);}
	}

	public void sendOnIntervalMsg(int remaining, Collection<ArenaTeam> currentLeaders) {
		try{impl.sendOnIntervalMsg(getChannel(MatchState.ONMATCHINTERVAL), currentLeaders, remaining);}catch(Exception e){Log.printStackTrace(e);}
	}

	public void sendTimeExpired() {
		try{impl.sendTimeExpired(getChannel(MatchState.ONMATCHTIMEEXPIRED));}catch(Exception e){Log.printStackTrace(e);}
	}

	public void setMessageHandler(MatchMessageHandler mc) {
		this.impl = mc;
	}

	public MatchMessageHandler getMessageHandler() {
		return impl;
	}
	public void setSilent(boolean silent){
		this.silent = silent;
	}

	public void sendAddedToTeam(ArenaTeam team, ArenaPlayer player) {
		try{impl.sendAddedToTeam(team,player);}catch(Exception e){Log.printStackTrace(e);}
	}

	public void sendCountdownTillPrestart(int remaining) {
		try{impl.sendCountdownTillPrestart(getChannel(MatchState.ONCOUNTDOWNTOEVENT), remaining);}
		catch(Exception e){Log.printStackTrace(e);}

	}

}
