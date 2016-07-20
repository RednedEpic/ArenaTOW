package io.github.TcFoxy.ArenaTOW.Listeners;

import io.github.TcFoxy.ArenaTOW.Tower;
import io.github.TcFoxy.ArenaTOW.TugArena;
import io.github.TcFoxy.ArenaTOW.playerModifiers.shop.ArenaEcon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import mc.alk.arena.BattleArena;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.teams.ArenaTeam;
import net.minecraft.server.v1_10_R1.EntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TugListener implements Listener{
	

	TugArena tug;
	
	private final String customRedZombie = "io.github.TcFoxy.ArenaTOW.nms..EntityRedZombie",
			customBlueZombie = "io.github.TcFoxy.ArenaTOW.nms..EntityBlueZombie",
			customRedGolem = "io.github.TcFoxy.ArenaTOW.nms..CustomRedGolem",
			customBlueGolem = "io.github.TcFoxy.ArenaTOW.nms..CustomBlueGolem",
			customRedGuardian = "io.github.TcFoxy.ArenaTOW.nms..CustomRedGuardian",
			customBlueGuardian = "io.github.TcFoxy.ArenaTOW.nms..CustomBlueGuardian";
	
	public TugListener(TugArena tug, HashMap<String, String> towerSave){
		this.tug = tug;
	}
	
	
	
	/*
	 * used to make sure that entities 
	 * of the same team will not pathfind towards friendly targets
	 * or targets that are invisible.
	 */	
	@EventHandler
	private void sameTeamTarget(EntityTargetEvent event){
		if (event.getTarget() == null) {
			return;
		}
		if(event.getEntity().getClass().getName() == "org.bukkit.craftbukkit..entity.CraftZombie" ||
				event.getEntity().getClass().getName() == "org.bukkit.craftbukkit..entity.CraftIronGolem" ||
				event.getEntity().getClass().getName() == "org.bukkit.craftbukkit..entity.CraftGuardian"){
			if(event.getTarget() instanceof Player){
				Player p = (Player) event.getTarget();
				ArenaPlayer ap = BattleArena.toArenaPlayer(p);
				ArenaTeam team = ap.getTeam();
				EntityLiving el = (EntityLiving) ((CraftEntity) event.getEntity()).getHandle();
				if (team == null){
					return;
				}
				String teamname = team.getDisplayName();
				String entityclass = el.getClass().getName();
				switch(entityclass){
				case customRedZombie:
					if(teamname == "Red") event.setCancelled(true);
					break;
				case customRedGolem:
					if(teamname == "Red") event.setCancelled(true);
					break;
				case customRedGuardian:
					if(teamname == "Red") event.setCancelled(true);
					break;
				case customBlueZombie:
					if(teamname == "Blue") event.setCancelled(true);
					break;
				case customBlueGolem:
					if(teamname == "Blue") event.setCancelled(true);
					break;
				case customBlueGuardian:
					if(teamname == "Blue") event.setCancelled(true);
					break;
				default:
					return;
				}

				//if the event isnt cancelled but the player is invisible:
				if(!event.isCancelled()){
					Collection<PotionEffect> potions = p.getActivePotionEffects();
					if(potions.contains(PotionEffectType.INVISIBILITY)){
						event.setCancelled(true);
					}
				}
			}
		}else{
			return;
		}
	}
	
	/*
	 * when a player kills a minion, tower, or player,
	 * their team level increase, and they get money.
	 */
	@EventHandler 
	public void minionDeath(EntityDeathEvent event){
		if(event.getEntity() instanceof IronGolem || event.getEntity() instanceof Zombie){
			EntityLiving el = (EntityLiving) ((CraftEntity) event.getEntity().getLastDamageCause().getEntity()).getHandle();
			String entityclass = el.getClass().getName();
			ArenaTeam team;
			Integer q = 0;
			switch(entityclass){
			case customRedZombie:
				team = tug.blueTeam;
				q = 1;
				break;
			case customRedGolem:
				team = tug.blueTeam;
				q = 50;
				break;
			case customBlueZombie:
				team = tug.redTeam;
				q = 1;
				break;
			case customBlueGolem:
				team = tug.redTeam;
				q = 50;
				break;
			default:
				return;
			}
			if(team == null){
				return;
			}
			Set<Player> players = team.getBukkitPlayers();
			for(Player p: players){
				ArenaEcon.addCash(p, q);
				if(tug.sh == null){
					Bukkit.broadcastMessage("sh == null");
				}
				tug.sh.refreshScore(p);
			}
			tug.teamLevel.addTeamPoint(q, team);
			
		}
				
	}

	
	/*
	 * used to prevent any annoying loot drop by entities.
	 */
	@EventHandler
	private void noLootDrop(EntityDeathEvent event){
		event.setDroppedExp(0);
		event.getDrops().clear();
	}
	
	/*
	 * no breaking blocks
	 */
	@EventHandler
	private void noBreakBlocks(BlockBreakEvent event){
		for(ArenaPlayer ap : tug.arena.getMatch().getAlivePlayers()){
			if(event.getPlayer() == ap.getPlayer()){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.DARK_RED + "Spamy Spam Spam - That's what you get for breaking blocks in the Arena!");
			}
		}
	}
	
	/*
	 * used to stop incendiary fireballs from igniting blocks
	 */
	@EventHandler
	 private void noFireballFire(BlockIgniteEvent event){
		if(event.getCause().equals(IgniteCause.FIREBALL)) event.setCancelled(true);
	}

	
	/*
	 * used to set the victors if one of the nexi is destroyed
	 */
	@EventHandler
	private void nexusDeath(EntityDeathEvent event){
		for(Tower tow : tug.towerteams.values()){
			if(tow.getMob() != null){
				if(tow.getMob().getBukkitEntity().toString() == "CraftGuardian" && tow.getMob().getHealth() == 0){
					EntityLiving el = (EntityLiving) ((CraftEntity) event.getEntity()).getHandle();
					if(el.getClass().getName() == customBlueGuardian){
						tug.arena.getMatch().setVictor(tug.redTeam);
					}else if(el.getClass().getName() == customRedGuardian){					
						tug.arena.getMatch().setVictor(tug.blueTeam);
					}
				}
			}
		}
	}
	
	/*
	 * disable the explosions and fire from wizard fireballs
	 */
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
	    Entity ent = event.getEntity();
	   
	    if (ent instanceof Creeper || ent instanceof Fireball) {
	        event.setCancelled(true); //Removes block damage
	    }
	}
	
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent event) {
	    event.setFire(false); //Only really needed for fireballs
	   
	    Entity ent = event.getEntity();
	    if (ent instanceof Fireball)
	        event.setRadius(2); //Increased from default(1), since the fireball now doesn't cause fire
	}
	
	
	/*
	 * used to activate item upgrade chest
	 */
	@EventHandler
	public void itemUpgrades(PlayerInteractEvent event){
		if(event.getClickedBlock() == null){
			return;
		}
		if(event.getClickedBlock().getType() == Material.ANVIL){
			event.setCancelled(true);
			tug.uGUI.openGUI(event);
		}
	}
	
}