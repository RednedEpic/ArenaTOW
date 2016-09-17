package io.github.TcFoxy.ArenaTOW.BattleArena.util.plugins.heroes;


import org.bukkit.entity.Player;


public class Heroes_pre1_5_2 extends HeroesUtil{

	@Override
	public void setHeroPlayerHealth(Player player, double health) {
		Hero hero = getHero(player);
		if (hero == null){
			player.setHealth((int)health);
		} else{
			hero.setHealth((int)health);
			hero.syncHealth();
		}
	}

	@Override
	public double getHeroHealth(Player player) {
		Hero hero = getHero(player);
		return hero == null ? player.getHealth() : hero.getHealth();
	}

	@Override
	public void setHeroHealthP(Player player, double health) {
		Hero hero = getHero(player);
		if (hero == null){
			double val = player.getMaxHealth() * health/100.0;
			player.setHealth((int)val);
		} else{
			double val = hero.getMaxHealth() * health/100.0;
			hero.setHealth((int)val);
			hero.syncHealth();
		}
	}

}
