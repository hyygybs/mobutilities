package com.hyygybs.mobutilities.common.menu;

public enum MachineLayout {
    BLOOD_GENERATOR("gui/energy_generator.png", 176, 166, 26, 37, 8, 17, 52),
    SOUL_GENERATOR("gui/energy_generator.png", 176, 166, 26, 37, 8, 17, 52),
    RESOLVER("gui/resolver.png", 176, 166, 79, 34, 56, 36, 14),
    REGENERATOR("gui/regenerator.png", 176, 166, 80, 35, 8, 17, 52),
    MOB_FARM("gui/mob_farm.png", 176, 166, 43, 17, 8, 17, 52);

    private final String texturePath;
    private final int imageWidth;
    private final int imageHeight;
    private final int progressX;
    private final int progressY;
    private final int energyX;
    private final int energyY;
    private final int energyHeight;

    MachineLayout(String texturePath, int imageWidth, int imageHeight, int progressX, int progressY, int energyX, int energyY, int energyHeight) {
        this.texturePath = texturePath;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.progressX = progressX;
        this.progressY = progressY;
        this.energyX = energyX;
        this.energyY = energyY;
        this.energyHeight = energyHeight;
    }

    public String texturePath() {
        return texturePath;
    }

    public int imageWidth() {
        return imageWidth;
    }

    public int imageHeight() {
        return imageHeight;
    }

    public int progressX() {
        return progressX;
    }

    public int progressY() {
        return progressY;
    }

    public int energyX() {
        return energyX;
    }

    public int energyY() {
        return energyY;
    }

    public int energyHeight() {
        return energyHeight;
    }
}
