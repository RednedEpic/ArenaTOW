package io.github.TcFoxy.ArenaTOW.v1_13_R1;

import io.github.TcFoxy.ArenaTOW.API.NMSHandler;
import io.github.TcFoxy.ArenaTOW.API.TOWEntityHandler;

import java.io.File;

public class NSMHandler implements NMSHandler{

    private v1_13_R1_MobHandler mobHandler;
    private v1_13_R1_EntityRegistrar entityRegistrar;
    private v1_13_R1_Listener listener;

    public NSMHandler(File saveDirectory) {
        this.mobHandler = new v1_13_R1_MobHandler();
        this.entityRegistrar = new v1_13_R1_EntityRegistrar(saveDirectory);
        this.listener = new v1_13_R1_Listener(entityRegistrar, saveDirectory);
    }

    @Override
    public v1_13_R1_EntityRegistrar getEntityRegistry() {
        return entityRegistrar;
    }

    @Override
    public TOWEntityHandler getEntityHandler() {
        return mobHandler;
    }

    @Override
    public v1_13_R1_Listener getListener() {
        return listener;
    }
}