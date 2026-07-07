package com.hyygybs.mobutilities.common.blockentity;

import com.hyygybs.mobutilities.MobUtilitiesConfig;
import com.hyygybs.mobutilities.common.item.UpgradeItem;
import com.hyygybs.mobutilities.common.menu.MachineLayout;
import com.hyygybs.mobutilities.common.registration.ModBlockEntities;
import com.hyygybs.mobutilities.common.registration.ModItems;
import com.hyygybs.mobutilities.common.registration.ModMenus;
import com.hyygybs.mobutilities.common.util.CustomEnergyStorage;
import com.hyygybs.mobutilities.common.util.MobContainerData;
import com.hyygybs.mobutilities.common.util.MobLootHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;
import java.util.Optional;

public final class MachineBlockEntities {
    private MachineBlockEntities() {
    }

    public static final class BloodGeneratorBlockEntity extends AbstractGeneratorBlockEntity {
        public BloodGeneratorBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.BLOOD_GENERATOR.get(), pos, state, ModItems.MATTER.get(),
                    ModMenus.BLOOD_GENERATOR.get(), MachineLayout.BLOOD_GENERATOR,
                    "block.mobutilities.blood_energy_generator", MobUtilitiesConfig.BLOOD_ENERGY_PER_ITEM.get());
        }
    }

    public static final class SoulGeneratorBlockEntity extends AbstractGeneratorBlockEntity {
        public SoulGeneratorBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.SOUL_GENERATOR.get(), pos, state, ModItems.SPIRIT.get(),
                    ModMenus.SOUL_GENERATOR.get(), MachineLayout.SOUL_GENERATOR,
                    "block.mobutilities.soul_energy_generator", MobUtilitiesConfig.SOUL_ENERGY_PER_ITEM.get());
        }
    }

    public static final class ResolverBlockEntity extends AbstractMachineBlockEntity {
        private static final int FUEL_SLOT = 1;
        private static final int MATTER_OUTPUT_SLOT = 2;
        private static final int SPIRIT_OUTPUT_SLOT = 3;
        private static final int EXPERIENCE_OUTPUT_SLOT = 4;
        private int burnTimeRemaining;
        private int burnTimeTotal;

        public ResolverBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.RESOLVER.get(), pos, state, 5, new CustomEnergyStorage(0, 0, 0, false, false));
        }

        @Override
        protected void tickServer() {
            maxProgress = adjustedTicks(MobUtilitiesConfig.RESOLVER_BASE_TICKS.get(), getSpeedCount());
            ItemStack input = itemHandler.getStackInSlot(0);
            Optional<MobContainerData> optionalData = MobContainerData.fromStack(input);
            if (input.isEmpty() || optionalData.isEmpty()) {
                resetProgress();
                setLit(false);
                return;
            }

            MobContainerData data = optionalData.get();
            if (!canProduce(data)) {
                resetProgress();
                setLit(false);
                return;
            }

            if (!hasFuelForWork()) {
                resetProgress();
                setLit(false);
                return;
            }

            burnTimeRemaining = Math.max(0, burnTimeRemaining - 1);
            progress++;
            setLit(true);
            if (progress >= maxProgress) {
                itemHandler.setStackInSlot(0, new ItemStack(ModItems.MOB_CONTAINER_EMPTY.get()));
                insertOrDrop(new ItemStack(ModItems.MATTER.get(), data.matterValue()), MATTER_OUTPUT_SLOT);
                insertOrDrop(new ItemStack(ModItems.SPIRIT.get(), data.spiritValue()), SPIRIT_OUTPUT_SLOT);
                insertOrDrop(new ItemStack(ModItems.EXPERIENCE.get(), data.experienceValue()), EXPERIENCE_OUTPUT_SLOT);
                resetProgress();
            }
            setChanged();
        }

        @Override
        protected void afterServerTick() {
            ItemStack inputSlot = itemHandler.getStackInSlot(0);
            if (inputSlot.is(ModItems.MOB_CONTAINER_EMPTY.get())) {
                pushItemsToNeighbors(0);
            }
            pushItemsToNeighbors(MATTER_OUTPUT_SLOT, SPIRIT_OUTPUT_SLOT, EXPERIENCE_OUTPUT_SLOT);
        }

        private boolean hasFuelForWork() {
            if (burnTimeRemaining > 0) {
                return true;
            }

            ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT);
            int burnTime = ForgeHooks.getBurnTime(fuelStack, RecipeType.SMELTING);
            if (burnTime <= 0) {
                return false;
            }

            itemHandler.extractItem(FUEL_SLOT, 1, false);
            burnTimeTotal = burnTime;
            burnTimeRemaining = burnTime;
            return true;
        }

        private boolean canProduce(MobContainerData data) {
            return canInsertAll(new ItemStack(ModItems.MATTER.get(), data.matterValue()), MATTER_OUTPUT_SLOT)
                    && canInsertAll(new ItemStack(ModItems.SPIRIT.get(), data.spiritValue()), SPIRIT_OUTPUT_SLOT)
                    && canInsertAll(new ItemStack(ModItems.EXPERIENCE.get(), data.experienceValue()), EXPERIENCE_OUTPUT_SLOT);
        }

        @Override
        protected boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(ModItems.MOB_CONTAINER_FILLED.get());
                case FUEL_SLOT -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
                case MATTER_OUTPUT_SLOT, SPIRIT_OUTPUT_SLOT, EXPERIENCE_OUTPUT_SLOT -> false;
                default -> false;
            };
        }

        @Override
        protected boolean canExternalInsert(int slot, ItemStack stack) {
            return switch (slot) {
                case 0, FUEL_SLOT -> isItemValid(slot, stack);
                default -> false;
            };
        }

        @Override
        public MenuType<?> getMenuType() {
            return ModMenus.RESOLVER.get();
        }

        @Override
        public MachineLayout getLayout() {
            return MachineLayout.RESOLVER;
        }

        @Override
        protected String getMachineNameKey() {
            return "block.mobutilities.resolver";
        }

        @Override
        protected void saveAdditional(net.minecraft.nbt.CompoundTag tag) {
            super.saveAdditional(tag);
            tag.putInt("BurnTimeRemaining", burnTimeRemaining);
            tag.putInt("BurnTimeTotal", burnTimeTotal);
        }

        @Override
        public void load(net.minecraft.nbt.CompoundTag tag) {
            super.load(tag);
            burnTimeRemaining = tag.getInt("BurnTimeRemaining");
            burnTimeTotal = tag.getInt("BurnTimeTotal");
        }

        @Override
        protected int getAdditionalContainerData(int index) {
            return switch (index) {
                case 4 -> burnTimeRemaining;
                case 5 -> burnTimeTotal;
                default -> 0;
            };
        }

        @Override
        protected void setAdditionalContainerData(int index, int value) {
            switch (index) {
                case 4 -> burnTimeRemaining = value;
                case 5 -> burnTimeTotal = value;
                default -> {
                }
            }
        }

        @Override
        protected int getAdditionalContainerDataCount() {
            return 2;
        }
    }

    public static final class RegeneratorBlockEntity extends AbstractMachineBlockEntity {
        public RegeneratorBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.REGENERATOR.get(), pos, state, 8, new CustomEnergyStorage(40000, 4000, 0, false, true));
        }

        @Override
        protected void tickServer() {
            maxProgress = adjustedTicks(MobUtilitiesConfig.REGENERATOR_BASE_TICKS.get(), getSpeedCount());
            ItemStack container = itemHandler.getStackInSlot(0);
            ItemStack matter = itemHandler.getStackInSlot(1);
            ItemStack spirit = itemHandler.getStackInSlot(2);
            if (!container.is(ModItems.MOB_CONTAINER_EMPTY.get())
                    || matter.getCount() < MobUtilitiesConfig.REGENERATOR_MATTER_COST.get()
                    || spirit.getCount() < MobUtilitiesConfig.REGENERATOR_SPIRIT_COST.get()) {
                resetProgress();
                setLit(false);
                return;
            }

            if (!itemHandler.getStackInSlot(3).isEmpty()) {
                resetProgress();
                setLit(false);
                return;
            }

            progress++;
            setLit(true);
            if (progress >= maxProgress) {
                Optional<MobContainerData> randomMob = MobLootHelper.createRandomMob((ServerLevel) level);
                if (randomMob.isPresent()) {
                    ItemStack output = new ItemStack(ModItems.MOB_CONTAINER_FILLED.get());
                    randomMob.get().writeTo(output);
                    itemHandler.extractItem(0, 1, false);
                    itemHandler.extractItem(1, MobUtilitiesConfig.REGENERATOR_MATTER_COST.get(), false);
                    itemHandler.extractItem(2, MobUtilitiesConfig.REGENERATOR_SPIRIT_COST.get(), false);
                    itemHandler.setStackInSlot(3, output);
                }
                resetProgress();
            }
            setChanged();
        }

        @Override
        protected void afterServerTick() {
            pushItemsToNeighbors(3);
        }

        @Override
        protected boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(ModItems.MOB_CONTAINER_EMPTY.get());
                case 1 -> stack.is(ModItems.MATTER.get());
                case 2 -> stack.is(ModItems.SPIRIT.get());
                case 3 -> false;
                default -> isAllowedUpgrade(stack, true, false, false, false);
            };
        }

        @Override
        protected boolean canExternalInsert(int slot, ItemStack stack) {
            return switch (slot) {
                case 0, 1, 2 -> isItemValid(slot, stack);
                default -> false;
            };
        }

        @Override
        public MenuType<?> getMenuType() {
            return ModMenus.REGENERATOR.get();
        }

        @Override
        public MachineLayout getLayout() {
            return MachineLayout.REGENERATOR;
        }

        @Override
        protected String getMachineNameKey() {
            return "block.mobutilities.regenerator";
        }
    }

    public static final class MobFarmBlockEntity extends AbstractMachineBlockEntity {
        public MobFarmBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.MOB_FARM.get(), pos, state, 14, new CustomEnergyStorage(100000, 4000, 0, false, true));
        }

        @Override
        protected void tickServer() {
            maxProgress = adjustedTicks(MobUtilitiesConfig.FARM_BASE_TICKS.get(), getSpeedCount());
            ItemStack container = itemHandler.getStackInSlot(0);
            ItemStack experience = itemHandler.getStackInSlot(7);
            Optional<MobContainerData> optionalData = MobContainerData.fromStack(container);

            if (!container.is(ModItems.MOB_CONTAINER_FILLED.get())
                    || optionalData.isEmpty()
                    || experience.getCount() < MobUtilitiesConfig.FARM_EXPERIENCE_PER_OPERATION.get()
                    || !hasEnoughEnergy(MobUtilitiesConfig.FARM_ENERGY_PER_OPERATION.get())) {
                resetProgress();
                setLit(false);
                return;
            }

            List<ItemStack> drops = MobLootHelper.generateLoot((ServerLevel) level, optionalData.get(), hasSimulationUpgrade(), getLootingCount());
            if (!canAcceptAllDrops(drops, 1, 2, 3, 4, 5, 6)) {
                resetProgress();
                setLit(false);
                return;
            }

            progress++;
            setLit(true);
            if (progress >= maxProgress) {
                itemHandler.extractItem(7, MobUtilitiesConfig.FARM_EXPERIENCE_PER_OPERATION.get(), false);
                consumeEnergy(MobUtilitiesConfig.FARM_ENERGY_PER_OPERATION.get());
                for (ItemStack drop : drops) {
                    insertOrDrop(drop, 1, 2, 3, 4, 5, 6);
                }
                if (hasSpecialUpgrade()) {
                    MobContainerData data = optionalData.get();
                    insertOrDrop(new ItemStack(ModItems.MATTER.get(), Math.max(1, data.matterValue() / 2)), 1, 2, 3, 4, 5, 6);
                    insertOrDrop(new ItemStack(ModItems.SPIRIT.get(), Math.max(1, data.spiritValue() / 2)), 1, 2, 3, 4, 5, 6);
                    insertOrDrop(new ItemStack(ModItems.EXPERIENCE.get(), Math.max(1, data.experienceValue() / 2)), 1, 2, 3, 4, 5, 6);
                }
                resetProgress();
            }
            setChanged();
        }

        @Override
        protected void afterServerTick() {
            pushItemsToNeighbors(1, 2, 3, 4, 5, 6);
        }

        private boolean canAcceptAllDrops(List<ItemStack> drops, int... slots) {
            for (ItemStack drop : drops) {
                if (!canInsertAll(drop, slots)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(ModItems.MOB_CONTAINER_FILLED.get());
                case 1, 2, 3, 4, 5, 6 -> false;
                case 7 -> stack.is(ModItems.EXPERIENCE.get());
                default -> isMobFarmUpgradeValid(stack);
            };
        }

        @Override
        protected boolean canExternalInsert(int slot, ItemStack stack) {
            return switch (slot) {
                case 0, 7 -> isItemValid(slot, stack);
                default -> false;
            };
        }

        private boolean isMobFarmUpgradeValid(ItemStack stack) {
            if (!isAllowedUpgrade(stack, true, true, true, true)) {
                return false;
            }
            if (!(stack.getItem() instanceof UpgradeItem upgradeItem)) {
                return false;
            }
            if (upgradeItem.getType() == UpgradeItem.Type.SIMULATION || upgradeItem.getType() == UpgradeItem.Type.SPECIAL) {
                return countUpgradeInSlots(upgradeItem.getType(), 8, 13) == 0;
            }
            return true;
        }

        private int countUpgradeInSlots(UpgradeItem.Type type, int startSlot, int endSlot) {
            int count = 0;
            for (int slot = startSlot; slot <= endSlot; slot++) {
                ItemStack slotStack = itemHandler.getStackInSlot(slot);
                if (slotStack.getItem() instanceof UpgradeItem upgradeItem && upgradeItem.getType() == type) {
                    count += slotStack.getCount();
                }
            }
            return count;
        }

        @Override
        public MenuType<?> getMenuType() {
            return ModMenus.MOB_FARM.get();
        }

        @Override
        public MachineLayout getLayout() {
            return MachineLayout.MOB_FARM;
        }

        @Override
        protected String getMachineNameKey() {
            return "block.mobutilities.mob_farm";
        }
    }

    private abstract static class AbstractGeneratorBlockEntity extends AbstractMachineBlockEntity {
        private final Item fuelItem;
        private final MenuType<?> menuType;
        private final MachineLayout layout;
        private final String nameKey;
        private final int energyPerItem;

        protected AbstractGeneratorBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState state,
                                               Item fuelItem, MenuType<?> menuType, MachineLayout layout, String nameKey, int energyPerItem) {
            super(type, pos, state, 7, new CustomEnergyStorage(100000, 0, 4000, true, false));
            this.fuelItem = fuelItem;
            this.menuType = menuType;
            this.layout = layout;
            this.nameKey = nameKey;
            this.energyPerItem = energyPerItem;
        }

        @Override
        protected void tickServer() {
            maxProgress = adjustedTicks(MobUtilitiesConfig.GENERATOR_BASE_TICKS.get(), getSpeedCount());
            ItemStack fuel = itemHandler.getStackInSlot(0);
            if (!fuel.is(fuelItem) || energyStorage.getEnergyStored() + energyPerItem > energyStorage.getMaxEnergyStored()) {
                transferEnergyToNeighbors();
                if (!fuel.is(fuelItem) || energyStorage.getEnergyStored() + energyPerItem > energyStorage.getMaxEnergyStored()) {
                    resetProgress();
                    setLit(false);
                    return;
                }
            }

            progress++;
            setLit(true);
            if (progress >= maxProgress) {
                itemHandler.extractItem(0, 1, false);
                energyStorage.addEnergy(energyPerItem);
                resetProgress();
            }
            transferEnergyToNeighbors();
            setChanged();
        }

        @Override
        protected boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(fuelItem);
                case 1, 2, 3, 4, 5, 6 -> stack.getItem() instanceof UpgradeItem upgradeItem
                        && upgradeItem.getType() == UpgradeItem.Type.SPEED;
                default -> false;
            };
        }

        @Override
        protected boolean canExternalInsert(int slot, ItemStack stack) {
            return slot == 0 && isItemValid(slot, stack);
        }

        @Override
        public MenuType<?> getMenuType() {
            return menuType;
        }

        @Override
        public MachineLayout getLayout() {
            return layout;
        }

        @Override
        protected String getMachineNameKey() {
            return nameKey;
        }

        private void transferEnergyToNeighbors() {
            if (level == null || energyStorage.getEnergyStored() <= 0) {
                return;
            }

            for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.values()) {
                if (energyStorage.getEnergyStored() <= 0) {
                    break;
                }

                net.minecraft.world.level.block.entity.BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
                if (neighbor == null) {
                    continue;
                }

                neighbor.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY, direction.getOpposite())
                        .ifPresent(target -> {
                            if (!target.canReceive()) {
                                return;
                            }
                            int available = energyStorage.extractEnergy(4000, true);
                            if (available <= 0) {
                                return;
                            }
                            int accepted = target.receiveEnergy(available, false);
                            if (accepted > 0) {
                                energyStorage.extractEnergy(accepted, false);
                            }
                        });
            }
        }
    }

    private static int adjustedTicks(int baseTicks, int speedUpgrades) {
        double speedMultiplier = 1.0D + (speedUpgrades * 0.5D);
        return Math.max(20, (int) Math.round(baseTicks / speedMultiplier));
    }

    private static boolean isAllowedUpgrade(ItemStack stack, boolean speed, boolean simulation, boolean looting, boolean special) {
        if (!(stack.getItem() instanceof UpgradeItem upgradeItem)) {
            return false;
        }
        return switch (upgradeItem.getType()) {
            case SPEED -> speed;
            case SIMULATION -> simulation;
            case LOOTING -> looting;
            case SPECIAL -> special;
        };
    }

}
