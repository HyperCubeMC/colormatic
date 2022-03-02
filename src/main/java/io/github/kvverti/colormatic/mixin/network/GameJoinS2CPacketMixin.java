/*
 * Colormatic
 * Copyright (C) 2021  Thalia Nero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * As an additional permission, when conveying the Corresponding Source of an
 * object code form of this work, you may exclude the Corresponding Source for
 * "Minecraft" by Mojang Studios, AB.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.kvverti.colormatic.mixin.network;

import net.minecraft.util.registry.RegistryEntry;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

@Mixin(GameJoinS2CPacket.class)
public abstract class GameJoinS2CPacketMixin {

    @Final
    @Mutable
    @Shadow
    private RegistryEntry<DimensionType> dimensionType;

    @Final
    @Shadow
    private DynamicRegistryManager.Immutable registryManager;

    /**
     * We loop through and make the instances identical since we want to be able to get the ID for a given dimension
     * type.
     */
    @Inject(
        method = "<init>(IZLnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;Ljava/util/Set;Lnet/minecraft/util/registry/DynamicRegistryManager$Immutable;Lnet/minecraft/util/registry/RegistryEntry;Lnet/minecraft/util/registry/RegistryKey;JIIIZZZZ)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;dimensionType:Lnet/minecraft/util/registry/RegistryEntry;",
            ordinal = 0,
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
        )
    )
    private void fixDimensionType(CallbackInfo info) {
        RegistryEntry<DimensionType> target = this.dimensionType;
        Registry<DimensionType> registry = this.registryManager.get(Registry.DIMENSION_TYPE_KEY);
        for (RegistryEntry<DimensionType> dimType : registry.getIndexedEntries()) {
            if (dimType.equals(target)) {
                this.dimensionType = dimType;
                break;
            }
        }
    }
}
