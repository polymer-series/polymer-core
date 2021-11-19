package com.nmmoc7.polymercore.common.network;

import com.nmmoc7.polymercore.api.PolymerCoreApi;
import com.nmmoc7.polymercore.api.capability.IMultiblockLocateHandler;
import com.nmmoc7.polymercore.api.multiblock.IAssembledMultiblock;
import com.nmmoc7.polymercore.common.capability.blueprint.CapabilityMultiblock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAssembleMultiblock {
    private final String multiblockId;
    private final boolean anchored;
    private final BlockPos offset;
    private final Rotation rotation;
    private final boolean flipped;

    public PacketAssembleMultiblock(String multiblockId, boolean anchored, BlockPos offset, Rotation rotation, boolean flipped) {
        this.multiblockId = multiblockId;
        this.anchored = anchored;
        this.offset = offset;
        this.rotation = rotation;
        this.flipped = flipped;
    }

    public PacketAssembleMultiblock(String multiblockId, IMultiblockLocateHandler locateHandler) {
        this.multiblockId = multiblockId;
        this.anchored = locateHandler.isAnchored();
        this.offset = locateHandler.getOffset();
        this.rotation = locateHandler.getRotation();
        this.flipped = locateHandler.isFlipped();
    }

    public PacketAssembleMultiblock(PacketBuffer buffer) {
        this.multiblockId = buffer.readString();
        this.anchored = buffer.readBoolean();
        this.offset = buffer.readBlockPos();
        this.rotation = buffer.readEnumValue(Rotation.class);
        this.flipped = buffer.readBoolean();
    }


    public void toBytes(PacketBuffer buffer) {
        buffer.writeString(multiblockId);
        buffer.writeBoolean(anchored);
        buffer.writeBlockPos(offset);
        buffer.writeEnumValue(rotation);
        buffer.writeBoolean(flipped);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;
                PolymerCoreApi.getInstance()
                    .getMultiblockManager()
                    .findById(new ResourceLocation(multiblockId))
                    .ifPresent(it -> {
                        IAssembledMultiblock assemble = it.assemble(player.world, offset, rotation, flipped);

                        if (assemble != null) {
                            player.sendMessage(new TranslationTextComponent("chat.polymer.info.multiblock.assemble.success"), Util.DUMMY_UUID);
                        } else {
                            player.sendMessage(new TranslationTextComponent("chat.polymer.info.multiblock.assemble.failed"), Util.DUMMY_UUID);
                        }
                    });

            });
        }
        ctx.get().setPacketHandled(true);
    }
}
