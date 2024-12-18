package net.minecraft.client;

import net.betterthanadventure.updater.Updater;

import java.io.IOException;

public class Minecraft {
    public static void main(String[] args) {
        Updater up = new Updater();
        up.run();
    }
}
