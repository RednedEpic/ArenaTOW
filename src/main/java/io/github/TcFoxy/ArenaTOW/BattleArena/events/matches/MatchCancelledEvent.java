package io.github.TcFoxy.ArenaTOW.BattleArena.events.matches;

import io.github.TcFoxy.ArenaTOW.BattleArena.competition.match.Match;

public class MatchCancelledEvent extends MatchEvent {
	public MatchCancelledEvent(Match match){
		super(match);
	}
}
