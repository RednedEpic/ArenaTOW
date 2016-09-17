package io.github.TcFoxy.ArenaTOW.BattleArena.objects.joining;


import java.util.Collection;

import io.github.TcFoxy.ArenaTOW.BattleArena.controllers.joining.AbstractJoinHandler;
import io.github.TcFoxy.ArenaTOW.BattleArena.controllers.joining.TeamJoinFactory;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.ArenaPlayer;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.MatchParams;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.MatchState;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.arenas.Arena;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.arenas.ArenaListener;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.exceptions.NeverWouldJoinException;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.options.JoinOptions;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.options.TransitionOption;



public class WaitingObject {
    protected boolean joinable = true;
    protected final AbstractJoinHandler jh;
    protected final MatchParams params;
    protected final QueueObject originalQueuedObject;
    protected final Arena arena;

    public WaitingObject(QueueObject qo) throws NeverWouldJoinException {
        this.params = qo.getMatchParams();
        this.originalQueuedObject = qo;
        this.arena = qo.getJoinOptions().getArena();
        if (qo instanceof MatchTeamQObject){
            this.jh = TeamJoinFactory.createTeamJoinHandler(qo.getMatchParams(), qo.getTeams());
            this.joinable = false;
        } else {
            this.jh = TeamJoinFactory.createTeamJoinHandler(qo.getMatchParams());
            this.joinable = true;
        }
    }

//    public boolean matches(QueueObject qo) {
//        return joinable &&
//                (arena != null ?
//                        arena.matches(qo.getMatchParams(), qo.getJoinOptions()) :
//                        params.matchesIgnoreNTeams(qo.getMatchParams()));
//    }
public boolean matches(QueueObject qo) {
        return joinable && (arena != null ?
                        arena.matches(qo.getJoinOptions()) :
                        params.matches(qo.getJoinOptions()));
    }
    public AbstractJoinHandler.TeamJoinResult join(TeamJoinObject qo) {
        return jh.joiningTeam(qo);
    }

    public boolean hasEnough() {
        return jh.hasEnough(params.getAllowedTeamSizeDifference());
    }

    public boolean isFull() {
        return jh.isFull();
    }

    public MatchParams getParams() {
        return params;
    }

    public Arena getArena() {
        return arena;
    }

    public Collection<ArenaPlayer> getPlayers() {
        return jh.getPlayers();
    }

    public JoinOptions getJoinOptions() {
        return this.originalQueuedObject.getJoinOptions();
    }

    public Collection<ArenaListener> getArenaListeners(){
        return this.originalQueuedObject.getListeners();
    }

    public QueueObject getOriginalQueuedObject() {
        return originalQueuedObject;
    }

    public String toString() {
        return "[WO " + (arena != null ? arena.getName() : "") + " " + params.getDisplayName() + "]";
    }

    public boolean createsOnJoin() {
        Arena a = originalQueuedObject.getArena();
        if (a != null) {
            return a.getParams().hasOptionAt(MatchState.ONJOIN, TransitionOption.TELEPORTIN) ||
                    params.hasOptionAt(MatchState.ONJOIN, TransitionOption.TELEPORTIN);
        }
        return params.hasOptionAt(MatchState.ONJOIN, TransitionOption.TELEPORTIN);
    }
}
