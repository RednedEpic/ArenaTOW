package io.github.TcFoxy.ArenaTOW.BattleArena.events;


import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.TcFoxy.ArenaTOW.BattleArena.Defaults;

public class BAEvent extends Event{
	private static final HandlerList handlers = new HandlerList();

	public void callEvent(){
		if (Defaults.TESTSERVER) return;
		Bukkit.getServer().getPluginManager().callEvent(this);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
