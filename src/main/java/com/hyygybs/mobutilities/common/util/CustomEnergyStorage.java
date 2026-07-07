package com.hyygybs.mobutilities.common.util;

import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {
    private final boolean canExtract;
    private final boolean canReceive;
    private Runnable changeListener = () -> {
    };

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract, boolean canExtract, boolean canReceive) {
        super(capacity, maxReceive, maxExtract);
        this.canExtract = canExtract;
        this.canReceive = canReceive;
    }

    @Override
    public boolean canExtract() {
        return canExtract;
    }

    @Override
    public boolean canReceive() {
        return canReceive;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && received > 0) {
            changeListener.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && extracted > 0) {
            changeListener.run();
        }
        return extracted;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
        changeListener.run();
    }

    public void addEnergy(int amount) {
        setEnergy(this.energy + amount);
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener == null ? () -> {
        } : changeListener;
    }
}
