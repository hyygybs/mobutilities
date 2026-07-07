package com.hyygybs.mobutilities.common.menu;

import com.hyygybs.mobutilities.common.blockentity.AbstractMachineBlockEntity;
import com.hyygybs.mobutilities.common.item.UpgradeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class MachineMenu extends AbstractContainerMenu {
    private final AbstractMachineBlockEntity blockEntity;
    private final ContainerData data;
    private final MachineLayout layout;

    public MachineMenu(MenuType<?> type, int containerId, Inventory inventory, AbstractMachineBlockEntity blockEntity, MachineLayout layout) {
        super(type, containerId);
        this.blockEntity = blockEntity;
        this.data = blockEntity.getContainerData();
        this.layout = layout;
        addDataSlots(data);
        addMachineSlots();
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    protected static AbstractMachineBlockEntity blockEntityFromBuffer(Inventory inventory, FriendlyByteBuf buffer) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (!(blockEntity instanceof AbstractMachineBlockEntity machineBlockEntity)) {
            throw new IllegalStateException("Expected machine block entity");
        }
        return machineBlockEntity;
    }

    private void addMachineSlots() {
        switch (layout) {
            case BLOOD_GENERATOR, SOUL_GENERATOR -> {
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 26, 53));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 62, 53));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 80, 53));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 98, 53));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 4, 116, 53));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 5, 134, 53));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 6, 152, 53));
            }
            case RESOLVER -> {
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 56, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 56, 53));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 116, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 116, 35));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 4, 116, 53));
            }
            case REGENERATOR -> {
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 116, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 56, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 56, 35));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 116, 53));
                addUpgradeSlots(4, 8);
            }
            case MOB_FARM -> {
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 26, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 62, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 80, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 98, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 4, 116, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 5, 134, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 6, 152, 17));
                addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 7, 26, 53));
                addMobFarmUpgradeSlot(8, 62, 53);
                addMobFarmUpgradeSlot(9, 80, 53);
                addMobFarmUpgradeSlot(10, 98, 53);
                addMobFarmUpgradeSlot(11, 116, 53);
                addMobFarmUpgradeSlot(12, 134, 53);
                addMobFarmUpgradeSlot(13, 152, 53);
            }
        }
    }

    private void addMobFarmUpgradeSlot(int slot, int x, int y) {
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), slot, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (isSingleInstanceMobFarmUpgrade(stack) && stack.getCount() > 1) {
                    return false;
                }
                return super.mayPlace(stack);
            }

            @Override
            public int getMaxStackSize() {
                ItemStack slotStack = getItem();
                if (isSingleInstanceMobFarmUpgrade(slotStack)) {
                    return 1;
                }
                return super.getMaxStackSize();
            }

            @Override
            public int getMaxStackSize(ItemStack stack) {
                if (isSingleInstanceMobFarmUpgrade(stack)) {
                    return 1;
                }
                return super.getMaxStackSize(stack);
            }
        });
    }

    private void addUpgradeSlots(int firstSlot, int startX) {
        for (int i = 0; i < 4; i++) {
            addSlot(new SlotItemHandler(blockEntity.getItemHandler(), firstSlot + i, startX + (i * 18), 72));
        }
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    public int getProgressScaled(int width) {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        if (progress <= 0 || maxProgress <= 0) {
            return 0;
        }
        return progress * width / maxProgress;
    }

    public int getEnergyScaled(int height) {
        int energy = data.get(2);
        int maxEnergy = data.get(3);
        if (energy <= 0 || maxEnergy <= 0) {
            return 0;
        }
        return Math.max(1, energy * height / maxEnergy);
    }

    public int getBurnScaled(int height) {
        int burnRemaining = data.get(4);
        int burnTotal = data.get(5);
        if (burnRemaining <= 0 || burnTotal <= 0) {
            return 0;
        }
        return Math.max(1, burnRemaining * height / burnTotal);
    }

    public int getRemainingProgressScaled(int height) {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        if (maxProgress <= 0 || progress >= maxProgress) {
            return 0;
        }
        return Math.max(1, (maxProgress - progress) * height / maxProgress);
    }

    public int getEnergy() {
        return data.get(2);
    }

    public int getMaxEnergy() {
        return data.get(3);
    }

    public MachineLayout getLayout() {
        return layout;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack quickMoved = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack slotStack = slot.getItem();
        quickMoved = slotStack.copy();
        int machineSlots = blockEntity.getInventorySize();

        if (index < machineSlots) {
            if (!moveItemStackTo(slotStack, machineSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (layout == MachineLayout.MOB_FARM && isSingleInstanceMobFarmUpgrade(slotStack)) {
                if (!moveSingleItemToSlots(slotStack, 8, 14)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, machineSlots, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return quickMoved;
    }

    private boolean isSingleInstanceMobFarmUpgrade(ItemStack stack) {
        if (layout != MachineLayout.MOB_FARM || !(stack.getItem() instanceof UpgradeItem upgradeItem)) {
            return false;
        }
        return upgradeItem.getType() == UpgradeItem.Type.SIMULATION
                || upgradeItem.getType() == UpgradeItem.Type.SPECIAL;
    }

    private boolean moveSingleItemToSlots(ItemStack sourceStack, int startIndex, int endIndex) {
        if (sourceStack.isEmpty()) {
            return false;
        }

        ItemStack singleItem = sourceStack.copy();
        singleItem.setCount(1);
        for (int slotIndex = startIndex; slotIndex < endIndex; slotIndex++) {
            Slot targetSlot = slots.get(slotIndex);
            if (!targetSlot.mayPlace(singleItem) || targetSlot.hasItem()) {
                continue;
            }

            targetSlot.setByPlayer(singleItem.copy());
            targetSlot.setChanged();
            sourceStack.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (shouldBlockMobFarmUpgradeInteraction(slotId, button, clickType, player)) {
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    private boolean shouldBlockMobFarmUpgradeInteraction(int slotId, int button, ClickType clickType, Player player) {
        if (layout != MachineLayout.MOB_FARM || !isMobFarmUpgradeSlotIndex(slotId)) {
            return false;
        }

        ItemStack carried = getCarried();
        if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_CRAFT)
                && isSingleInstanceMobFarmUpgrade(carried)
                && carried.getCount() > 1) {
            return true;
        }

        if (clickType == ClickType.SWAP && button >= 0 && button < Inventory.getSelectionSize()) {
            ItemStack hotbarStack = player.getInventory().getItem(button);
            return isSingleInstanceMobFarmUpgrade(hotbarStack) && hotbarStack.getCount() > 1;
        }

        return false;
    }

    private boolean isMobFarmUpgradeSlotIndex(int slotId) {
        return slotId >= 8 && slotId <= 13;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }

    public static class BloodGeneratorMenu extends MachineMenu {
        public BloodGeneratorMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
            this(containerId, inventory, blockEntityFromBuffer(inventory, buffer));
        }

        public BloodGeneratorMenu(int containerId, Inventory inventory, AbstractMachineBlockEntity blockEntity) {
            super(com.hyygybs.mobutilities.common.registration.ModMenus.BLOOD_GENERATOR.get(), containerId, inventory, blockEntity, MachineLayout.BLOOD_GENERATOR);
        }
    }

    public static class SoulGeneratorMenu extends MachineMenu {
        public SoulGeneratorMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
            this(containerId, inventory, blockEntityFromBuffer(inventory, buffer));
        }

        public SoulGeneratorMenu(int containerId, Inventory inventory, AbstractMachineBlockEntity blockEntity) {
            super(com.hyygybs.mobutilities.common.registration.ModMenus.SOUL_GENERATOR.get(), containerId, inventory, blockEntity, MachineLayout.SOUL_GENERATOR);
        }
    }

    public static class ResolverMenu extends MachineMenu {
        public ResolverMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
            this(containerId, inventory, blockEntityFromBuffer(inventory, buffer));
        }

        public ResolverMenu(int containerId, Inventory inventory, AbstractMachineBlockEntity blockEntity) {
            super(com.hyygybs.mobutilities.common.registration.ModMenus.RESOLVER.get(), containerId, inventory, blockEntity, MachineLayout.RESOLVER);
        }
    }

    public static class RegeneratorMenu extends MachineMenu {
        public RegeneratorMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
            this(containerId, inventory, blockEntityFromBuffer(inventory, buffer));
        }

        public RegeneratorMenu(int containerId, Inventory inventory, AbstractMachineBlockEntity blockEntity) {
            super(com.hyygybs.mobutilities.common.registration.ModMenus.REGENERATOR.get(), containerId, inventory, blockEntity, MachineLayout.REGENERATOR);
        }
    }

    public static class MobFarmMenu extends MachineMenu {
        public MobFarmMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
            this(containerId, inventory, blockEntityFromBuffer(inventory, buffer));
        }

        public MobFarmMenu(int containerId, Inventory inventory, AbstractMachineBlockEntity blockEntity) {
            super(com.hyygybs.mobutilities.common.registration.ModMenus.MOB_FARM.get(), containerId, inventory, blockEntity, MachineLayout.MOB_FARM);
        }
    }
}
