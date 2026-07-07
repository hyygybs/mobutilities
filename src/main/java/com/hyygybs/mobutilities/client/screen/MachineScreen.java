package com.hyygybs.mobutilities.client.screen;

import com.hyygybs.mobutilities.MobUtilities;
import com.hyygybs.mobutilities.common.menu.MachineLayout;
import com.hyygybs.mobutilities.common.menu.MachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends AbstractContainerScreen<MachineMenu> {
    public MachineScreen(MachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = menu.getLayout().imageWidth();
        this.imageHeight = menu.getLayout().imageHeight();
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MobUtilities.MOD_ID, menu.getLayout().texturePath());
        int left = leftPos;
        int top = topPos;
        guiGraphics.blit(texture, left, top, 0, 0, imageWidth, imageHeight);

        if (menu.getLayout() == MachineLayout.RESOLVER) {
            renderResolverWidgets(guiGraphics, texture, left, top);
        } else if (menu.getLayout() == MachineLayout.BLOOD_GENERATOR || menu.getLayout() == MachineLayout.SOUL_GENERATOR) {
            renderGeneratorWidgets(guiGraphics, texture, left, top);
        } else if (menu.getLayout() == MachineLayout.REGENERATOR) {
            renderRegeneratorWidgets(guiGraphics, texture, left, top);
        } else if (menu.getLayout() == MachineLayout.MOB_FARM) {
            renderMobFarmWidgets(guiGraphics, texture, left, top);
        } else {
            int progressWidth = menu.getProgressScaled(24);
            if (progressWidth > 0) {
                guiGraphics.blit(texture, left + menu.getLayout().progressX(), top + menu.getLayout().progressY(), 176, 0, progressWidth, 17);
            }

            int energyHeight = menu.getEnergyScaled(menu.getLayout().energyHeight());
            if (energyHeight > 0) {
                int drawY = top + menu.getLayout().energyY() + (menu.getLayout().energyHeight() - energyHeight);
                guiGraphics.blit(texture, left + menu.getLayout().energyX(), drawY, 176, 17 + (menu.getLayout().energyHeight() - energyHeight), 12, energyHeight);
            }
        }
    }

    private void renderGeneratorWidgets(GuiGraphics guiGraphics, ResourceLocation texture, int left, int top) {
        int flameHeight = menu.getRemainingProgressScaled(14);
        if (flameHeight > 0) {
            int flameY = top + menu.getLayout().progressY() + (14 - flameHeight);
            guiGraphics.blit(texture, left + menu.getLayout().progressX(), flameY, 188, 14 - flameHeight, 14, flameHeight);
        }

        int energyHeight = menu.getEnergyScaled(menu.getLayout().energyHeight());
        if (energyHeight > 0) {
            int drawY = top + menu.getLayout().energyY() + (menu.getLayout().energyHeight() - energyHeight);
            guiGraphics.blit(texture, left + menu.getLayout().energyX(), drawY, 176, menu.getLayout().energyHeight() - energyHeight, 12, energyHeight);
        }
    }

    private void renderRegeneratorWidgets(GuiGraphics guiGraphics, ResourceLocation texture, int left, int top) {
        int horizontalProgress = menu.getProgressScaled(24);
        if (horizontalProgress > 0) {
            guiGraphics.blit(texture, left + menu.getLayout().progressX(), top + menu.getLayout().progressY(), 188, 14, horizontalProgress, 17);
        }

        int verticalProgress = menu.getProgressScaled(17);
        if (verticalProgress > 0) {
            guiGraphics.blit(texture, left + 115, top + 34, 188, 31, 17, verticalProgress);
        }

        int flameHeight = menu.getRemainingProgressScaled(12);
        if (flameHeight > 0) {
            int flameY = top + 55 + (14 - flameHeight);
            guiGraphics.blit(texture, left + 57, flameY, 188, 14 - flameHeight, 14, flameHeight);
        }

        int energyHeight = menu.getEnergyScaled(menu.getLayout().energyHeight());
        if (energyHeight > 0) {
            int drawY = top + menu.getLayout().energyY() + (menu.getLayout().energyHeight() - energyHeight);
            guiGraphics.blit(texture, left + menu.getLayout().energyX(), drawY, 176, menu.getLayout().energyHeight() - energyHeight, 12, energyHeight);
        }
    }

    private void renderMobFarmWidgets(GuiGraphics guiGraphics, ResourceLocation texture, int left, int top) {
        int progressWidth = menu.getProgressScaled(24);
        if (progressWidth > 0) {
            guiGraphics.blit(texture, left + menu.getLayout().progressX(), top + menu.getLayout().progressY(), 188, 0, progressWidth, 17);
        }

        int flameHeight = menu.getRemainingProgressScaled(14);
        if (flameHeight > 0) {
            int flameY = top + 38 + (13 - flameHeight);
            guiGraphics.blit(texture, left + 26, flameY, 188, 17 + (14 - flameHeight), 31, flameHeight);
        }

        int energyHeight = menu.getEnergyScaled(menu.getLayout().energyHeight());
        if (energyHeight > 0) {
            int drawY = top + menu.getLayout().energyY() + (menu.getLayout().energyHeight() - energyHeight);
            guiGraphics.blit(texture, left + menu.getLayout().energyX(), drawY, 176, menu.getLayout().energyHeight() - energyHeight, 12, energyHeight);
        }
    }

    private void renderResolverWidgets(GuiGraphics guiGraphics, ResourceLocation texture, int left, int top) {
        int progressWidth = menu.getProgressScaled(24);
        if (progressWidth > 0) {
            guiGraphics.blit(texture, left + menu.getLayout().progressX(), top + menu.getLayout().progressY(), 176, 14, progressWidth, 17);
        }

        int flameHeight = menu.getBurnScaled(menu.getLayout().energyHeight());
        if (flameHeight > 0) {
            int flameY = top + menu.getLayout().energyY() + (menu.getLayout().energyHeight() - flameHeight);
            guiGraphics.blit(texture, left + menu.getLayout().energyX(), flameY, 176, menu.getLayout().energyHeight() - flameHeight, 14, flameHeight);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
        if (menu.getLayout() != MachineLayout.RESOLVER
                && menu.getLayout() != MachineLayout.BLOOD_GENERATOR
                && menu.getLayout() != MachineLayout.SOUL_GENERATOR
                && menu.getLayout() != MachineLayout.REGENERATOR
                && menu.getLayout() != MachineLayout.MOB_FARM) {
            guiGraphics.drawString(font, Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"), 26, 58, 0x404040, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderGeneratorEnergyTooltip(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderGeneratorEnergyTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (menu.getLayout() != MachineLayout.BLOOD_GENERATOR
                && menu.getLayout() != MachineLayout.SOUL_GENERATOR
                && menu.getLayout() != MachineLayout.REGENERATOR
                && menu.getLayout() != MachineLayout.MOB_FARM) {
            return;
        }

        int x = leftPos + menu.getLayout().energyX();
        int y = topPos + menu.getLayout().energyY();
        int width = 12;
        int height = menu.getLayout().energyHeight();
        if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            guiGraphics.renderTooltip(font,
                    java.util.List.of(Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE")),
                    java.util.Optional.empty(), mouseX, mouseY);
        }
    }
}
