package com.hyygybs.mobutilities.common.blockentity;

import com.hyygybs.mobutilities.common.block.MachineBlock;
import com.hyygybs.mobutilities.common.item.UpgradeItem;
import com.hyygybs.mobutilities.common.menu.MachineLayout;
import com.hyygybs.mobutilities.common.menu.MachineMenu;
import com.hyygybs.mobutilities.common.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMachineBlockEntity extends BlockEntity implements MenuProvider {
    protected final ItemStackHandler itemHandler;
    protected final CustomEnergyStorage energyStorage;
    protected final LazyOptional<IItemHandler> itemCapability;
    protected final LazyOptional<IEnergyStorage> energyCapability;
    protected int progress;
    protected int maxProgress;
    private final ContainerData containerData;

    protected AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slots, CustomEnergyStorage energyStorage) {
        super(type, pos, state);
        this.itemHandler = new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return AbstractMachineBlockEntity.this.isItemValid(slot, stack);
            }
        };
        this.energyStorage = energyStorage;
        this.energyStorage.setChangeListener(this::setChanged);
        this.itemCapability = LazyOptional.of(this::createAutomationItemHandler);
        this.energyCapability = LazyOptional.of(() -> energyStorage);
        this.containerData = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> energyStorage.getEnergyStored();
                    case 3 -> energyStorage.getMaxEnergyStored();
                    default -> getAdditionalContainerData(index);
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> energyStorage.setEnergy(value);
                    default -> setAdditionalContainerData(index, value);
                }
            }

            @Override
            public int getCount() {
                return 4 + getAdditionalContainerDataCount();
            }
        };
    }

    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        tickServer();
        afterServerTick();
    }

    protected abstract void tickServer();

    protected void afterServerTick() {
    }

    protected abstract boolean isItemValid(int slot, ItemStack stack);

    protected boolean canExternalInsert(int slot, ItemStack stack) {
        return isItemValid(slot, stack);
    }

    public abstract MenuType<?> getMenuType();

    public abstract MachineLayout getLayout();

    protected abstract String getMachineNameKey();

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public int getInventorySize() {
        return itemHandler.getSlots();
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public boolean isLit() {
        return getBlockState().hasProperty(MachineBlock.LIT) && getBlockState().getValue(MachineBlock.LIT);
    }

    protected void setLit(boolean lit) {
        if (level == null) {
            return;
        }
        BlockState currentState = getBlockState();
        if (currentState.hasProperty(MachineBlock.LIT) && currentState.getValue(MachineBlock.LIT) != lit) {
            level.setBlock(worldPosition, currentState.setValue(MachineBlock.LIT, lit), 3);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getMachineNameKey());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new MachineMenu(getMenuType(), containerId, inventory, this, getLayout());
    }

    public void dropContents() {
        if (level == null) {
            return;
        }
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
            }
        }
    }

    public int getComparatorOutput() {
        int filled = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                filled++;
            }
        }
        return Math.min(15, Math.round((filled / (float) itemHandler.getSlots()) * 15.0F));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        energyStorage.setEnergy(tag.getInt("Energy"));
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCapability.invalidate();
        energyCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCapability.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    protected boolean canInsertAll(ItemStack stack, int... slots) {
        ItemStack remaining = stack.copy();
        for (int slot : slots) {
            remaining = insertIntoMachineSlot(remaining, slot, true);
            if (remaining.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    protected void insertOrDrop(ItemStack stack, int... slots) {
        if (level == null || stack.isEmpty()) {
            return;
        }
        ItemStack remaining = stack.copy();
        for (int slot : slots) {
            remaining = insertIntoMachineSlot(remaining, slot, false);
            if (remaining.isEmpty()) {
                break;
            }
        }
        if (!remaining.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), remaining);
        }
    }

    protected void pushItemsToNeighbors(int... outputSlots) {
        if (level == null || outputSlots.length == 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            boolean hasItemsToMove = false;
            for (int slot : outputSlots) {
                if (!itemHandler.getStackInSlot(slot).isEmpty()) {
                    hasItemsToMove = true;
                    break;
                }
            }
            if (!hasItemsToMove) {
                return;
            }

            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor == null) {
                continue;
            }

            neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite())
                    .ifPresent(target -> moveItemsIntoHandler(target, outputSlots));
        }
    }

    private void moveItemsIntoHandler(IItemHandler target, int... outputSlots) {
        for (int slot : outputSlots) {
            ItemStack sourceStack = itemHandler.getStackInSlot(slot);
            if (sourceStack.isEmpty()) {
                continue;
            }

            ItemStack simulatedRemaining = ItemHandlerHelper.insertItem(target, sourceStack.copy(), true);
            int transferable = sourceStack.getCount() - simulatedRemaining.getCount();
            if (transferable <= 0) {
                continue;
            }

            ItemStack extracted = itemHandler.extractItem(slot, transferable, false);
            if (extracted.isEmpty()) {
                continue;
            }

            ItemStack remaining = ItemHandlerHelper.insertItem(target, extracted, false);
            if (!remaining.isEmpty()) {
                insertIntoMachineSlot(remaining, slot, false);
            }
        }
    }

    protected ItemStack insertIntoMachineSlot(ItemStack stack, int slot, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = itemHandler.getStackInSlot(slot);
        int maxStackSize = Math.min(stack.getMaxStackSize(), itemHandler.getSlotLimit(slot));
        if (existing.isEmpty()) {
            int moved = Math.min(maxStackSize, stack.getCount());
            if (!simulate) {
                ItemStack inserted = stack.copy();
                inserted.setCount(moved);
                itemHandler.setStackInSlot(slot, inserted);
            }

            if (stack.getCount() <= moved) {
                return ItemStack.EMPTY;
            }

            ItemStack remaining = stack.copy();
            remaining.shrink(moved);
            return remaining;
        }

        if (!ItemStack.isSameItemSameTags(existing, stack)) {
            return stack;
        }

        int space = Math.max(0, maxStackSize - existing.getCount());
        if (space <= 0) {
            return stack;
        }

        int moved = Math.min(space, stack.getCount());
        if (!simulate) {
            ItemStack merged = existing.copy();
            merged.grow(moved);
            itemHandler.setStackInSlot(slot, merged);
        }

        if (stack.getCount() <= moved) {
            return ItemStack.EMPTY;
        }

        ItemStack remaining = stack.copy();
        remaining.shrink(moved);
        return remaining;
    }

    private IItemHandler createAutomationItemHandler() {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return itemHandler.getSlots();
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return itemHandler.getStackInSlot(slot);
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (!canExternalInsert(slot, stack)) {
                    return stack;
                }
                return itemHandler.insertItem(slot, stack, simulate);
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return itemHandler.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return itemHandler.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return canExternalInsert(slot, stack);
            }
        };
    }

    protected boolean hasEnoughEnergy(int amount) {
        return energyStorage.getEnergyStored() >= amount;
    }

    protected void consumeEnergy(int amount) {
        energyStorage.extractEnergy(amount, false);
        setChanged();
    }

    protected void receiveEnergy(int amount) {
        energyStorage.receiveEnergy(amount, false);
        setChanged();
    }

    protected void resetProgress() {
        progress = 0;
    }

    protected int getAdditionalContainerData(int index) {
        return 0;
    }

    protected void setAdditionalContainerData(int index, int value) {
    }

    protected int getAdditionalContainerDataCount() {
        return 0;
    }

    protected int getSpeedCount() {
        return countUpgrade(UpgradeItem.Type.SPEED);
    }

    protected boolean hasSimulationUpgrade() {
        return countUpgrade(UpgradeItem.Type.SIMULATION) > 0;
    }

    protected int getLootingCount() {
        return countUpgrade(UpgradeItem.Type.LOOTING);
    }

    protected boolean hasSpecialUpgrade() {
        return countUpgrade(UpgradeItem.Type.SPECIAL) > 0;
    }

    private int countUpgrade(UpgradeItem.Type type) {
        int count = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() instanceof UpgradeItem upgradeItem && upgradeItem.getType() == type) {
                count += stack.getCount();
            }
        }
        return type == UpgradeItem.Type.SIMULATION ? Math.min(1, count) : count;
    }
}
