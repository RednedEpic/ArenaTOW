package io.github.TcFoxy.ArenaTOW.BattleArena.controllers.messaging;



import java.util.Collection;
import java.util.Map;
import java.util.Set;

import io.github.TcFoxy.ArenaTOW.BattleArena.competition.match.Match;
import io.github.TcFoxy.ArenaTOW.BattleArena.controllers.ParamController;
import io.github.TcFoxy.ArenaTOW.BattleArena.events.matches.MatchMessageEvent;
import io.github.TcFoxy.ArenaTOW.BattleArena.events.matches.messages.MatchIntervalMessageEvent;
import io.github.TcFoxy.ArenaTOW.BattleArena.events.matches.messages.MatchTimeExpiredMessageEvent;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.ArenaPlayer;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.MatchState;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.messaging.Channel;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.messaging.Channels;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.messaging.MatchMessageHandler;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.messaging.Message;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.messaging.MessageOptions.MessageOption;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.teams.ArenaTeam;
import io.github.TcFoxy.ArenaTOW.BattleArena.serializers.MessageSerializer;
import io.github.TcFoxy.ArenaTOW.BattleArena.util.MessageUtil;
import io.github.TcFoxy.ArenaTOW.BattleArena.util.TimeUtil;

/**
 *
 * @author alkarin
 *
 */
public class MatchMessageImpl extends MessageSerializer implements MatchMessageHandler {
	final Match match;
	final String typeName;
	final String typedot;

	public MatchMessageImpl(Match m){
		super(ParamController.getMatchParams(m.getParams()).getName(), m.getParams());
		this.match = m;
		typeName = mp.getName();
		typedot = "match.";
	}

	@Override
	public void sendOnBeginMsg(Channel serverChannel,Collection<ArenaTeam> teams) {
		sendMessageToTeams(serverChannel,teams,"onbegin","server_onbegin", mp.getSecondsTillMatch());
	}

	@Override
	public void sendOnPreStartMsg(Channel serverChannel,Collection<ArenaTeam> teams) {
		sendMessageToTeams(serverChannel,teams,"prestart","server_prestart", mp.getSecondsTillMatch());
	}

	@Override
	public void sendOnStartMsg(Channel serverChannel, Collection<ArenaTeam> teams) {
		sendMessageToTeams(serverChannel,teams,"start","server_start",null);
	}

	private void sendMessageToTeams(Channel serverChannel, Collection<ArenaTeam> teams, String path, String serverpath, Integer seconds){
		final String nTeamPath = getStringPathFromSize(teams.size());
		Message message = getNodeMessage(typedot+ nTeamPath+"."+path);
		Message serverMessage = getNodeMessage(typedot+ nTeamPath+"."+serverpath);
		Set<MessageOption> ops = message.getOptions();
		if (serverChannel != Channels.NullChannel){
			ops.addAll(serverMessage.getOptions());
		}
		MessageFormatter msgf = new MessageFormatter(this, match.getParams(), teams.size(), message, ops);

		msgf.formatCommonOptions(teams,seconds);
		for (ArenaTeam t: teams){
			msgf.formatTeamOptions(t,false);
			msgf.formatTeams(teams);
			String newmsg = msgf.getFormattedMessage(message);
			t.sendMessage(newmsg);
		}

		if (serverChannel != Channels.NullChannel){
			String msg = msgf.getFormattedMessage(serverMessage);
			serverChannel.broadcast(msg);
		}
	}

	@Override
	public void sendOnVictoryMsg(Channel serverChannel, Collection<ArenaTeam> victors, Collection<ArenaTeam> losers) {
		int size = (victors != null ? victors.size() : 0) + (losers != null ? losers.size() : 0);
		final String nTeamPath = getStringPathFromSize(size);
//		for (VictoryCondition vc: match.getVictoryConditions()){
//			if (vc instanceof DefinesLeaderRanking){
//				List<ArenaTeam> leaders = ((DefinesLeaderRanking)vc).getLeaders();
//				if (leaders==null)
//					continue;
//				int max = Math.min(leaders.size(), 4);
//				StringBuilder sb = new StringBuilder();
//				for (int i = 0;i<max;i++){
//					sb.append("&6").append(i + 1).append("&e : ").
//                            append(TeamUtil.formatName(leaders.get(i))).append("\n");
//				}
//				String leaderStr = sb.toString();
//				if (victors != null){
//					for (ArenaTeam t: victors){
//						t.sendMessage(leaderStr);}
//				}
//				if (losers != null){
//					for (ArenaTeam t: losers){
//						t.sendMessage(leaderStr);}
//				}
//				break;
//			}
//		}

		sendVictory(serverChannel,victors,losers,mp,typedot+nTeamPath+".victory",typedot+nTeamPath+".loss",
				typedot+nTeamPath+".server_victory");
	}

	@Override
	public void sendOnDrawMsg(Channel serverChannel, Collection<ArenaTeam> drawers, Collection<ArenaTeam> losers) {
		int size = (drawers != null ? drawers.size() : 0) + (losers != null ? losers.size() : 0);
		final String nTeamPath = getStringPathFromSize(size);
		sendVictory(serverChannel,null,drawers,mp,typedot+nTeamPath+".draw",typedot+nTeamPath+".draw",
				typedot+nTeamPath+".server_draw");
	}

	public void sendYourTeamNotReadyMsg(ArenaTeam t1) {
		Message message = getNodeMessage("match"+typeName+".your_team_not_ready");
		Set<MessageOption> ops = message.getOptions();

		MessageFormatter msgf = new MessageFormatter(this, match.getParams(), 1, message, ops);
		msgf.formatTeamOptions(t1, false);
		t1.sendMessage(msgf.getFormattedMessage(message));
	}

	public void sendOtherTeamNotReadyMsg(ArenaTeam t1) {
		Message message = getNodeMessage(typedot+typeName+".other_team_not_ready");
		Set<MessageOption> ops = message.getOptions();

		MessageFormatter msgf = new MessageFormatter(this, match.getParams(), 1, message, ops);
		msgf.formatTeamOptions(t1, false);
		t1.sendMessage(msgf.getFormattedMessage(message));
	}

	@Override
	public void sendAddedToTeam(ArenaTeam team, ArenaPlayer player) {
		Message message = getNodeMessage("common.added_to_team");
		Set<MessageOption> ops = message.getOptions();
		MessageFormatter msgf = new MessageFormatter(this, match.getParams(), 1, message, ops);
		msgf.formatTeamOptions(team, false);
		msgf.formatPlayerOptions(player);
		team.sendToOtherMembers(player,msgf.getFormattedMessage(message));
	}

	public void sendOnIntervalMsg(Channel serverChannel, Collection<ArenaTeam> currentLeaders, int remaining) {
		TimeUtil.testClock();
		String msg;
		Message message = getNodeMessage("match.interval_update");
		Set<MessageOption> ops = message.getOptions();
		MessageFormatter msgf = new MessageFormatter(this, match.getParams(),currentLeaders.size(), message, ops);
		msgf.formatCommonOptions(currentLeaders, remaining);
		msg = msgf.getFormattedMessage(message);
		if (currentLeaders == null || currentLeaders.isEmpty()){
//			msg = match.getParams().getPrefix()+"&e ends in &4" +timeStr;
		} else {
			if (currentLeaders.size() == 1){
				ArenaTeam currentLeader = currentLeaders.iterator().next();
				Message message2 = getNodeMessage("match.interval_update_winning");
				ops = message2.getOptions();
				msgf = new MessageFormatter(this, match.getParams(),currentLeaders.size(), message2, ops);
				msgf.formatCommonOptions(currentLeaders, remaining);
				msgf.formatTeamOptions(currentLeader, true);
				msg += msgf.getFormattedMessage(message2);
				if (msg.contains("{winnerpointsfor}"))
					msg = msg.replaceAll("\\{winnerpointsfor\\}", currentLeader.getNKills()+"");
				if (msg.contains("{winnerpointsagainst}"))
					msg = msg.replaceAll("\\{winnerpointsagainst\\}", currentLeader.getNDeaths()+"");
			} else {
				String teamStr = MessageUtil.joinTeams(currentLeaders,"&e and ");
				Message message2 = getNodeMessage("match.interval_update_tied");
				msgf = new MessageFormatter(this, match.getParams(),currentLeaders.size(), message2, ops);
				msgf.formatCommonOptions(currentLeaders, remaining);
				msg += msgf.getFormattedMessage(message2);
				if (msg.contains("{teams}"))
						msg = msg.replaceAll("\\{teams\\}", teamStr);
			}
		}
		MatchMessageEvent event = new MatchIntervalMessageEvent(match,MatchState.ONMATCHINTERVAL,
				serverChannel,"", msg,remaining);
		match.callEvent(event);
		String emessage = event.getMatchMessage();
		if (emessage != null && !emessage.isEmpty())
			match.sendMessage(emessage);
		emessage = event.getServerMessage();
		if (event.getServerChannel() != Channels.NullChannel && emessage != null && !emessage.isEmpty())
			event.getServerChannel().broadcast(emessage);
	}

	public void sendTimeExpired(Channel serverChannel) {
		MatchMessageEvent event = new MatchTimeExpiredMessageEvent(match,MatchState.ONMATCHTIMEEXPIRED,serverChannel,"", "");
		match.callEvent(event);
		String message = event.getMatchMessage();
		if (message != null && !message.isEmpty())
			match.sendMessage(message);
		message = event.getServerMessage();
		if (event.getServerChannel() != Channels.NullChannel && message != null && !message.isEmpty())
			event.getServerChannel().broadcast(message);
	}

	@Override
	public String getMessage(String node) {
		return getMessage(node,null);
	}

	@Override
	public String getMessage(String node, Map<String, String> params) {
		String text = this.getNodeText(node);
		return text == null ? null : format(text,params);
	}

	@Override
	public void sendMessage(String node) {
		sendMessage(node,null);
	}

	@Override
	public void sendMessage(String node, Map<String, String> params) {
		String msg = getMessage(node,params);
		if (msg != null && !msg.isEmpty())
			match.sendMessage(msg);
	}

	@Override
	public String format(String text, Map<String, String> params) {
		if (params == null || params.isEmpty())
			return text;
		String[] searchList =new String[params.size()];
		String[] replaceList =new String[params.size()];
		int i = 0;
		for(Map.Entry<String,String> entry : params.entrySet()){
			searchList[i] = entry.getKey();
			replaceList[i] = entry.getValue();
		    i++;
		}
		return MessageFormatter.replaceEach(text, searchList, replaceList);
	}

	@Override
	public void sendCountdownTillPrestart(Channel serverChannel, int seconds) {
		Message message = getNodeMessage("event.countdownTillEvent");
		Message serverMessage = getNodeMessage("event.server_countdownTillEvent");
		Set<MessageOption> ops = message.getOptions();
		if (serverChannel != Channels.NullChannel){
			ops.addAll(serverMessage.getOptions());
		}
		MessageFormatter msgf = new MessageFormatter(this, match.getParams(), 0, message, ops);
		msgf.formatCommonOptions(null,seconds);
		if (serverChannel != Channels.NullChannel){
			String msg = msgf.getFormattedMessage(serverMessage);
			serverChannel.broadcast(msg);
		}
	}

}
