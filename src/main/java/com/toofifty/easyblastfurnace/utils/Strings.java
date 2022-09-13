package com.toofifty.easyblastfurnace.utils;

import lombok.Getter;

public enum Strings {
    FILLCOALBAG("Fill coal bag"),
    REFILLCOALBAG("Refill coal bag"),
    EMPTYCOALBAG("Empty coal bag"),
    WITHDRAWCOALBAG("Withdraw coal bag"),
    WITHDRAWCOAL("Withdraw coal"),
    WITHDRAWGOLDORE("Withdraw gold ore"),
    WITHDRAWIRONORE("Withdraw iron ore"),
    WITHDRAWMITHRILORE("Withdraw mithril ore"),
    WITHDRAWADAMANTITEORE("Withdraw adamantite ore"),
    WITHDRAWRUNITEORE("Withdraw runite ore"),
    WITHDRAWICEORSMITHSGLOVES("Withdraw ice gloves or smiths gloves (i)"),
    EQUIPICEORSMITHSGLOVES("Equip ice gloves or smiths gloves (i)"),
    WITHDRAWGOLDSMITHGAUNTLETS("Withdraw goldsmith gauntlets"),
    EQUIPGOLDSMITHGAUNTLETS("Equip goldsmith gauntlets"),
    WITHDRAWSMITHINGCAPE("Withdraw Smithing cape"),
    EQUIPSMITHINGCAPE("Equip Smithing cape"),
    DEPOSITINVENTORY("Deposit inventory"),
    PUTONTOCONVEYORBELT("Put ore onto conveyor belt"),
    OPENBANK("Open bank chest"),
    COLLECTBARS("Collect bars"),
    WAITFORBARS("Wait for bars to smelt"),
    DRINKSTAMINA(null),

    ADAMANTITEHYBRID("Gold + adamantite bars"),
    ADAMANTITE("Adamantite bars"),
    GOLD("Gold bars"),
    MITHRIL("Mithril bars"),
    MITHRILHYBRID("Gold + mithril bars"),
    RUNITE("Runite bars"),
    RUNITEHYBRID("Gold + runite bars"),
    STEEL("Steel bars");

    @Getter
    private final String txt;

    Strings(String txt)
    {
        this.txt = txt;
    }
}
