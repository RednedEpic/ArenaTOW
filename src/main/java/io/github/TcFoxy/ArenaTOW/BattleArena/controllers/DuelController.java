package io.github.TcFoxy.ArenaTOW.BattleArena.controllers;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.TcFoxy.ArenaTOW.BattleArena.BattleArena;
import io.github.TcFoxy.ArenaTOW.BattleArena.Defaults;
import io.github.TcFoxy.ArenaTOW.BattleArena.competition.match.Match;
import io.github.TcFoxy.ArenaTOW.BattleArena.matches.MatchCancelledEvent;
import io.github.TcFoxy.ArenaTOW.BattleArena.matches.MatchCompletedEvent;
import io.github.TcFoxy.ArenaTOW.BattleArena.matches.MatchCreatedEvent;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.ArenaPlayer;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.Duel;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.MatchResult;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.MatchState;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.Matchup;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.arenas.Arena;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.arenas.ArenaListener;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.events.ArenaEventHandler;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.joining.MatchTeamQObject;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.options.DuelOptions.DuelOption;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.options.JoinOptions;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.teams.ArenaTeam;
import io.github.TcFoxy.ArenaTOW.BattleArena.util.MessageUtil;


public class DuelController implements ArenaListener{
	List<Duel> formingDuels = new CopyOnWriteArrayList<Duel>();
	HashMap<UUID, Long> rejectTimers = new HashMap<UUID,Long>();
	HashMap<Matchup,Duel> ongoingDuels = new HashMap<Matchup,Duel>();
	Map<Match, Matchup> matchups = Collections.synchronizedMap(new HashMap<Match,Matchup>());

	public void addOutstandingDuel(Duel duel) {
		formingDuels.add(duel);
	}

	public Duel accept(ArenaPlayer player) {
		Duel d = getChallengedDuel(player);
		if (d != null){
			d.accept(player);
			if (d.isReady()){
				/// Do a final check to see if they have completed all the duel options
				if (!checkWager(d))
					return null;

				ArenaTeam t = d.getChallengerTeam();
				ArenaTeam t2 = d.makeChallengedTeam();
				List<ArenaTeam> teams = new ArrayList<ArenaTeam>();
				teams.add(t);
				teams.add(t2);
				JoinOptions jo = new JoinOptions();
                jo.setMatchParams(d.getMatchParams());
                jo.setJoinLocation(player.getLocation());
                if (d.getOptions().hasOption(DuelOption.ARENA)){
					jo.setArena((Arena) d.getOptions().getOptionValue(DuelOption.ARENA));
				}
				Matchup m = new Matchup(d.getMatchParams(),teams, jo);
				m.addArenaListener(this);
				formingDuels.remove(d);
				ongoingDuels.put(m, d);
                MatchTeamQObject mo = new MatchTeamQObject(m);
                BattleArena.getBAController().addMatchup(mo);
			}
		}
		return d;
	}

	@ArenaEventHandler
	public void matchCancelled(MatchCancelledEvent event){
		Match match = event.getMatch();
		Matchup matchup = matchups.remove(match);
		if (matchup == null)
			return;
		Duel d = ongoingDuels.remove(matchup);
		if (d == null)
			return;
		Double money = (Double) d.getDuelOptionValue(DuelOption.MONEY);
		if (money != null){
			refundMoney(money, match.getTeams());}
	}

	private void refundMoney(Double money, Collection<ArenaTeam> teams) {
		for (ArenaTeam t: teams){
			for (ArenaPlayer ap: t.getPlayers()){
				MessageUtil.sendMessage(ap,"&4[Duel] &6"+money+" "+Defaults.MONEY_STR+"&e has been refunded");
				MoneyController.add(ap.getName(), money);
			}
		}
	}

	@ArenaEventHandler
	public void matchComplete(MatchCompletedEvent event){
		Match match = event.getMatch();
		Matchup matchup = matchups.remove(match);
		if (matchup == null)
			return;
		Duel d = ongoingDuels.remove(matchup);
		if (d == null)
			return;
		MatchResult mr = match.getResult();

		Double money = (Double) d.getDuelOptionValue(DuelOption.MONEY);
		if (money != null){
			if (mr.hasVictor()){
				Collection<ArenaTeam> winningTeams = mr.getVictors();
				int winningSize = 0;
				for (ArenaTeam winTeam : winningTeams){
					winningSize += winTeam.size();}
				final double split = d.getTotalMoney() / winningSize;
				for (ArenaTeam winTeam : winningTeams){
					for (ArenaPlayer ap: winTeam.getPlayers()){
						MessageUtil.sendMessage(ap,"&4[Duel] &eYou have won &6" + split +" "+Defaults.MONEY_STR+"&e for your victory!");
						MoneyController.add(ap.getName(), split);
					}
				}
			} else {
				refundMoney(money, mr.getDrawers());
			}
		}
	}

    @ArenaEventHandler(begin = MatchState.ONCREATE, end = MatchState.ONOPEN)
    public void onMatchCreatedEvent(MatchCreatedEvent event) {
        Matchup matchup = (((MatchTeamQObject) event.getOriginalObject().getOriginalQueuedObject()).getMatchup());
        matchups.put(event.getMatch(), matchup);
    }

	private boolean checkWager(Duel d) {
		Double wager = (Double) d.getDuelOptionValue(DuelOption.MONEY);
		if (wager == null)
			return true;
		HashSet<ArenaPlayer> players = new HashSet<ArenaPlayer>(d.getChallengedPlayers());
		players.addAll(d.getChallengerTeam().getPlayers());
		for (ArenaPlayer ap: players){
			if (MoneyController.balance(ap.getName()) < wager){
				MessageUtil.sendMessage(ap,"&4[Duel] &cYou don't have enough money to accept the wager!");
				cancelFormingDuel(d, "&4[Duel]&6" + ap.getDisplayName()+" didn't have enough money for the wager");
				return false;
			}
		}
		for (ArenaPlayer ap: players){
			MessageUtil.sendMessage(ap,"&4[Duel] &6"+wager+" "+Defaults.MONEY_STR+"&e has been subtracted from your account");
			MoneyController.subtract(ap.getName(), wager);
		}
		d.setTotalMoney((wager) * players.size());
		return true;
	}

	public Duel reject(ArenaPlayer player) {
		Duel d = getChallengedDuel(player);
		if (d != null){
			formingDuels.remove(d);
			rejectTimers.put(player.getID(), System.currentTimeMillis());
		}
		return d;
	}

	public boolean hasChallenger(ArenaPlayer player) {
		for (Duel d: formingDuels){
			if (d.hasChallenger(player)){
				return true;}
		}
		return false;
	}

	public Duel getDuel(ArenaPlayer player) {
		for (Duel d: formingDuels){
			if (d.hasChallenger(player)){
				return d;}
		}
		return getChallengedDuel(player);
	}

	public Duel getChallengedDuel(ArenaPlayer player) {
		for (Duel d: formingDuels){
			if (d.isChallenged(player)){
				return d;}
		}
		return null;
	}

	public boolean isChallenged(ArenaPlayer ap) {
		for (Duel d: formingDuels){
			if (d.isChallenged(ap))
				return true;
		}
		return false;
	}

	public Duel rescind(ArenaPlayer player) {
		Duel d = getDuel(player);
		if (d != null){
			formingDuels.remove(d);
		}
		return d;
	}


	public Long getLastRejectTime(ArenaPlayer ap) {
		Long t = rejectTimers.get(ap.getID());
		if (t == null)
			return null;
		if (Defaults.DUEL_CHALLENGE_INTERVAL*1000 < System.currentTimeMillis() - t){
			rejectTimers.remove(ap.getID());}
		return t;
	}


	public void cancelFormingDuel(Duel d, String msg) {
		formingDuels.remove(d);
		Collection<ArenaPlayer> players = d.getChallengedPlayers();
		for (ArenaPlayer p: players){
			MessageUtil.sendMessage(p, msg);
		}
		ArenaTeam t = d.getChallengerTeam();
		t.sendMessage(msg);
	}

}
