package io.github.TcFoxy.ArenaTOW.BattleArena.listeners.Custom;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Event;

import io.github.TcFoxy.ArenaTOW.BattleArena.Defaults;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.events.EventPriority;


/**
 *
 * @author alkarin
 *
 */
abstract class GeneralEventListener extends BaseEventListener  {
    /** Set of arena listeners */
    final public EnumMap<EventPriority, Map<RListener,Integer>> listeners =
            new EnumMap<EventPriority, Map<RListener,Integer>>(EventPriority.class);

    protected volatile RListener[] handlers = null;

    public GeneralEventListener(Class<? extends Event> bukkitEvent, org.bukkit.event.EventPriority bukkitPriority) {
        super(bukkitEvent, bukkitPriority);
    }


    /**
     * Does this event even have any listeners
     * @return true if there are listeners, false otherwise
     */
    @Override
    public boolean hasListeners(){
        return !listeners.isEmpty();
    }

    /**
     * Get the set of arena listeners
     * @return map of listeners
     */
    public EnumMap<EventPriority, Map<RListener,Integer>> getListeners(){
        return listeners;
    }

    /**
     * Add a player listener to this bukkit event
     * @param rl RListener
     */
    public void addListener(RListener rl) {
        addMatchListener(rl);
    }

    /**
     * remove a player listener from this bukkit event
     * @param rl RListener
     */
    public synchronized void removeListener(RListener rl) {
        if (Defaults.DEBUG_EVENTS) System.out.println("    removing listener listener="+rl);
        removeMatchListener(rl);
    }

    @Override
    public synchronized void removeAllListeners(RListener rl) {
        if (Defaults.DEBUG_EVENTS) System.out.println("    removing all listeners  listener="+rl);
        removeMatchListener(rl);
    }

    /**
     * add an arena listener to this bukkit event
     *
     * @param listener RListener
     */
    protected abstract void addMatchListener(RListener listener);

    /**
     * remove an arena listener to this bukkit event
     * @param listener RListener
     * @return whether listener was found and removed
     */
    protected abstract boolean removeMatchListener(RListener listener);


    protected synchronized void bake() {
        if (handlers != null) return;
        List<RListener> entries = new ArrayList<RListener>();
        for (Map.Entry<EventPriority,Map<RListener,Integer>> entry : listeners.entrySet()){
            entries.addAll(entry.getValue().keySet());}
        handlers = entries.toArray(new RListener[entries.size()]);
    }

    /**
     * Bake code from Bukkit
     * @return array of listeners
     */
    public RListener[] getRegisteredListeners() {
        RListener[] handlers;
        while ((handlers = this.handlers) == null) bake(); // This prevents fringe cases of returning null
        return handlers;
    }
}
