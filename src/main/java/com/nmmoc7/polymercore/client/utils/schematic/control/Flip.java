package com.nmmoc7.polymercore.client.utils.schematic.control;

import com.nmmoc7.polymercore.api.capability.IMultiblockLocateHandler;
import com.nmmoc7.polymercore.client.resources.Icons;
import com.nmmoc7.polymercore.common.registry.KeysRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class Flip extends ControlAction {

    public Flip() {
        super(Icons.FLIP);
    }

    @Override
    public ITextComponent getName() {
        return new TranslationTextComponent("gui.polymer.locator.control.flip.title");
    }

    @Override
    public List<ITextComponent> getDescription() {
        return super.getDescription();
    }

    @Override
    public void handleMouseInput(IMultiblockLocateHandler locateHandler, int mouseButton, boolean pressed) {
        if (!pressed || mouseButton != 1) {
            return;
        }
        if (Screen.hasAltDown()) {
            locateHandler.flip();
            callback.run();
        }
    }
}