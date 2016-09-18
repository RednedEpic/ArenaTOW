package io.github.TcFoxy.ArenaTOW.BattleArena.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.TcFoxy.ArenaTOW.BattleArena.BattleArena;
import io.github.TcFoxy.ArenaTOW.BattleArena.controllers.ArenaAlterController;
import io.github.TcFoxy.ArenaTOW.BattleArena.controllers.ArenaAlterController.ArenaOptionPair;
import io.github.TcFoxy.ArenaTOW.BattleArena.controllers.ArenaDebugger;
import io.github.TcFoxy.ArenaTOW.BattleArena.controllers.ArenaEditor;
import io.github.TcFoxy.ArenaTOW.BattleArena.controllers.ArenaEditor.CurrentSelection;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.arenas.Arena;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.exceptions.InvalidOptionException;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.options.SpawnOptions;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.pairs.ParamAlterOptionPair;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.pairs.TransitionOptionTuple;
import io.github.TcFoxy.ArenaTOW.BattleArena.objects.spawns.TimedSpawn;
import io.github.TcFoxy.ArenaTOW.BattleArena.serializers.ArenaSerializer;
import io.github.TcFoxy.ArenaTOW.BattleArena.serializers.SpawnSerializer;
import io.github.TcFoxy.ArenaTOW.BattleArena.util.MessageUtil;

public class ArenaEditorExecutor extends CustomCommandExecutor {
    public static String idPrefix = "ar_";
    
    public ArenaEditorExecutor(){
        super();
    }

    @MCCommand(cmds={"select","sel"}, admin=true)
    public boolean arenaSelect(CommandSender sender, Arena arena) {
        ArenaEditor aac = BattleArena.getArenaEditor();
        aac.setCurrentArena(sender, arena);
        return MessageUtil.sendMessage(sender, "&2You have selected arena &6" + arena.getName());
    }

    @MCCommand(cmds={"ds","deletespawn"}, admin=true, usage="/aa deleteSpawn <index>")
    public boolean arenaDeleteSpawn(CommandSender sender, CurrentSelection cs, Integer number) {
        if (number <= 0 || number > 10000){
            return MessageUtil.sendMessage(sender, "&cYou need to specify an index within the range &61-10000");}

        Arena a = cs.getArena();
        TimedSpawn ts = a.deleteTimedSpawn((long) number);
        if (ts != null){
            ac.updateArena(a);
            BattleArena.saveArenas();
            return MessageUtil.sendMessage(sender, "&6"+a.getName()+ "&e has deleted index=&4D" + number+"&e that had spawn="+ts);
        } else {
            return MessageUtil.sendMessage(sender, "&cThere was no spawn at that index");
        }
    }

    /**
     * This command syntax needs to be changed.
     * 
     * The last index parameter needs to be optional.
     * 
     * The reason why it can't just be deleted, is because if it's optional,
     * then it will be backwards compatible for people creating mini-games with 
     * the BattleArena API.
     * 
     */
    @MCCommand(cmds={"as","addspawn"}, admin=true, min=2,
            usage="/aa addspawn <mob/item/spawnGroup> [buffs or effects] [number] [fs=first spawn time] [rs=respawn time] [ds=despawn time] [index|i=<index>]")
    public boolean arenaAddSpawn(Player sender, CurrentSelection cs, String[] args) {
        Long index = parseIndex(sender, cs.getArena(), args);
        Arena a = cs.getArena();

        TimedSpawn spawn = SpawnSerializer.parseSpawn(Arrays.copyOfRange(args, 0, args.length));
        if (spawn == null){
            return MessageUtil.sendMessage(sender,"Couldnt recognize spawn " + args[1]);
        }
        Location l = sender.getLocation();
        spawn.getSpawn().setLocation(l);
        
        if (index == -1) {
            index = a.addTimedSpawn(spawn);
        } else {
            a.putTimedSpawn(index, spawn);
        }
        ac.updateArena(a);
        ArenaSerializer.saveArenas(a.getArenaType().getPlugin());
        return MessageUtil.sendMessage(sender, "&6"+a.getName()+ "&e now has spawn &6" + spawn +"&2  index=&5" + index);
    }
    
    @MCCommand(cmds={"ss","setspawn"}, admin=true, min=2,
            usage="/aa setspawn <mob/item/spawnGroup> [buffs or effects] [number] [fs=first spawn time] [rs=respawn time] [ds=despawn time] [index|i=<index>]")
    public boolean arenaSetSpawn(Player sender, CurrentSelection cs, String[] args) {
        return arenaAddSpawn(sender, cs, args);
    }

    @MCCommand(cmds={"ab","addBlock"}, admin=true,
            usage="/aa addBlock [number] [fs=first spawn time] [rt=respawn time] [trigger=<trigger type>] [resetTo=<block>] [index]")
    public boolean arenaAddBlock(Player sender, CurrentSelection cs, String[] args) {
        Long index = parseIndex(sender, cs.getArena(), args);
        if (index == -1)
            return true;
        SpawnOptions po = SpawnOptions.parseSpawnOptions(args);
        cs.setStartListening(index, po);

        return MessageUtil.sendMessage(sender, "&2Success: &eClick a block to add the block spawn");
    }

    private Long parseIndex(CommandSender sender, Arena arena, String[] args) {
        Long number = -1L;
        String last = args[args.length - 1];
        String split = "index=|i=";
        try {
            String[] s = last.split(split);
            if (s.length==2)
                number = Long.parseLong(s[1]);
        } catch (Exception e) {
            MessageUtil.sendMessage(sender, "&cindex "+last+" was bad");
            return -1L;
        }
        long nextIndex = 1;
        if (number == -1L) {
            Map<Long, TimedSpawn> spawns = arena.getTimedSpawns();
            if (spawns==null) {
                return 1L;
            }
            List<Long> keys = new ArrayList<Long>(spawns.keySet());
            Collections.sort(keys);
            for (Long k : keys){
                if (k!= nextIndex)
                    break;
                nextIndex++;
            }
            number = nextIndex;
        }

        if (number <= 0 || number > 10000) {
            MessageUtil.sendMessage(sender, "&cYou need to specify an index within the range &61-10000");
            return -1L;
        }
        return number;
    }

    @MCCommand(cmds = {}, admin = true, perm = "arena.alter")
    public boolean arenaGeneric(CommandSender sender, CurrentSelection cs, ArenaOptionPair aop) {
        return ArenaEditorExecutor.setArenaOption(sender, cs.getArena(), aop);
    }

    @MCCommand(cmds = {}, admin = true, perm = "arena.alter")
    public boolean arenaGeneric(CommandSender sender,CurrentSelection cs,  ParamAlterOptionPair gop) {
        return ArenaEditorExecutor.setArenaOption(sender, cs.getArena(), gop);
    }

    @MCCommand(cmds = {}, admin = true, perm = "arena.alter")
    public boolean arenaGeneric(CommandSender sender,CurrentSelection cs,  TransitionOptionTuple top) {
        return ArenaEditorExecutor.setArenaOption(sender, cs.getArena(), top);
    }

    public static boolean setArenaOption(CommandSender sender, Arena arena, TransitionOptionTuple top) {
        try {
            ArenaAlterController.setArenaOption(sender, arena, top.state, top.op, top.value);
            if (top.value != null) {
                sendMessage(sender, "&2Arena "+arena.getDisplayName()+" options &6" + top.op + "&2 changed to &6" + top.value);
            } else {
                sendMessage(sender, "&2Arena "+arena.getDisplayName()+" options &6" + top.op + "&2 changed");
            }
            return true;
        } catch (IllegalStateException e) {
            return sendMessage(sender, "&c" + e.getMessage());
        } catch (InvalidOptionException e) {
            return sendMessage(sender, "&c" + e.getMessage());
        }
    }

    public static boolean setArenaOption(CommandSender sender,Arena arena, ArenaOptionPair aop){
        try {
            /// all of the messages are handled inside of setArenaOption
            ArenaAlterController.setArenaOption(sender, arena, aop.ao, aop.value);
            return true;
        } catch (IllegalStateException e) {
            return sendMessage(sender, "&c" + e.getMessage());
        }
    }

    public static boolean setArenaOption(CommandSender sender,Arena arena,  ParamAlterOptionPair gop){
        try {
            ArenaAlterController.setArenaOption(sender, arena, gop.alterParamOption, gop.value);
            if (gop.value != null) {
                sendMessage(sender, "&2Arena "+arena.getDisplayName()+" options &6" + gop.alterParamOption.name() + "&2 changed to &6" + gop.value);
            } else {
                sendMessage(sender, "&2Arena "+arena.getDisplayName()+" options &6" + gop.alterParamOption.name() + "&2 changed");
            }
            return true;
        } catch (IllegalStateException e) {
            return sendMessage(sender, "&c" + e.getMessage());
        }
    }


    @MCCommand(cmds={"hidespawns"}, admin=true, usage="hidespawns")
    public boolean arenaHideSpawns(Player sender, CurrentSelection cs) {
        Arena arena = cs.getArena();
        ArenaDebugger ad = ArenaDebugger.getDebugger(arena);
        ad.hideSpawns(sender);
        ArenaDebugger.removeDebugger(ad);
        return sendMessage(sender,ChatColor.YELLOW+ "You are hiding spawns for &6" + arena.getName());
    }

    @MCCommand(cmds={"showspawns"}, admin=true,  usage="showspawns")
    public boolean arenaShowSpawns(Player sender,CurrentSelection cs) {
        Arena arena = cs.getArena();

        ArenaDebugger ad = ArenaDebugger.getDebugger(arena);
        ad.hideSpawns(sender);
        ad.showSpawns(sender);
        return sendMessage(sender, ChatColor.GREEN + "You are showing spawns for &6" + arena.getName());
    }

    @MCCommand(cmds={"listspawns"}, admin=true)
    public boolean arenaListSpawns(Player sender,CurrentSelection cs) {
        Arena arena = cs.getArena();
        sendMessage(sender, ChatColor.GREEN + "You are listing spawns for &6" + arena.getName());
        Map<Long, TimedSpawn> spawns = arena.getTimedSpawns();
        if (spawns==null) {
            return sendMessage(sender, ChatColor.RED+ "Arena has no spawns");}
        List<Long> keys = new ArrayList<Long>(spawns.keySet());
        Collections.sort(keys);
        for (Long k : keys) {
            sendMessage(sender, "&5"+k+"&e: "+spawns.get(k).getDisplayName());
        }
        return true;
    }

}
