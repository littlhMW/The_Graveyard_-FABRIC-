package com.finallion.graveyard.client;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.finallion.graveyard.blockentities.renders.SarcophagusBlockEntityRenderer;
import com.finallion.graveyard.blockentities.renders.BrazierBlockEntityRenderer;
import com.finallion.graveyard.entities.models.CorruptedIllagerModel;
import com.finallion.graveyard.entities.renders.*;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGItems;
import com.finallion.graveyard.init.TGParticles;
import com.finallion.graveyard.item.VialOfBlood;
import com.finallion.graveyard.network.GraveyardEntitySpawnPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.ClientListener;


@Environment(EnvType.CLIENT)
public class TheGraveyardClient implements ClientModInitializer {
    private static final RenderLayer CUTOUT_MIPPED = RenderLayer.getCutoutMipped();
    public static final EntityModelLayer CORRUPTED_ILLAGER_MODEL_LAYER = new EntityModelLayer(new Identifier(TheGraveyard.MOD_ID, "corrupted_illager"), "main");

    @Override
    public void onInitializeClient() {
        TGParticles.init();

        BlockRenderLayerMap.INSTANCE.putBlocks(CUTOUT_MIPPED,
                TGBlocks.SKULL_WITH_RIB_CAGE,
                TGBlocks.WITHER_SKULL_WITH_RIB_CAGE,
                TGBlocks.LEANING_SKELETON,
                TGBlocks.LEANING_WITHER_SKELETON,
                TGBlocks.LYING_SKELETON,
                TGBlocks.LYING_WITHER_SKELETON,
                TGBlocks.BONE_REMAINS,
                TGBlocks.SKULL_ON_PIKE,
                TGBlocks.LATERALLY_LYING_SKELETON,
                TGBlocks.TORSO_PILE,
                TGBlocks.WITHER_BONE_REMAINS,
                TGBlocks.WITHER_SKULL_ON_PIKE,
                TGBlocks.LATERALLY_LYING_WITHER_SKELETON,
                TGBlocks.WITHER_TORSO_PILE,
                TGBlocks.DARK_IRON_BARS,
                TGBlocks.TG_GRASS_BLOCK,
                TGBlocks.SOUL_FIRE_BRAZIER,
                TGBlocks.FIRE_BRAZIER,
                TGBlocks.CANDLE_HOLDER,
                TGBlocks.DARK_IRON_DOOR,
                TGBlocks.DARK_IRON_TRAPDOOR);

        BlockEntityRendererRegistry.register(TGBlocks.GRAVESTONE_BLOCK_ENTITY, GravestoneBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(TGBlocks.SARCOPHAGUS_BLOCK_ENTITY, SarcophagusBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(TGBlocks.BRAZIER_BLOCK_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new BrazierBlockEntityRenderer());

        // coloring of tg_grass_block depending on biome
        ColorProviderRegistry.BLOCK.register(new BlockColorProvider() {
            @Override
            public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
                return world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D);
            }
        }, TGBlocks.TG_GRASS_BLOCK);

        ColorProviderRegistry.ITEM.register(new ItemColorProvider() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                return GrassColors.getColor(0.5D, 1.0D);
            }
        }, TGBlocks.TG_GRASS_BLOCK);


        // entities
        EntityRendererRegistry.register(TGEntities.SKELETON_CREEPER, SkeletonCreeperRender::new);
        EntityRendererRegistry.register(TGEntities.ACOLYTE, AcolyteRender::new);
        EntityRendererRegistry.register(TGEntities.GHOUL, GhoulRenderer::new);
        EntityRendererRegistry.register(TGEntities.REAPER, ReaperRenderer::new);
        EntityRendererRegistry.register(TGEntities.REVENANT, RevenantRenderer::new);
        EntityRendererRegistry.register(TGEntities.NIGHTMARE, NightmareRenderer::new);
        EntityRendererRegistry.register(TGEntities.CORRUPTED_PILLAGER, CorruptedPillagerRenderer::new);
        EntityRendererRegistry.register(TGEntities.CORRUPTED_VINDICATOR, CorruptedVindicatorRenderer::new);
        EntityRendererRegistry.register(TGEntities.WRAITH, WraithRenderer::new);
        EntityRendererRegistry.register(TGEntities.LICH, LichRenderer::new);
        EntityRendererRegistry.register(TGEntities.FALLING_CORPSE, FallingCorpseRenderer::new);
        EntityRendererRegistry.register(TGEntities.SKULL, SkullEntityRenderer::new);
        EntityRendererRegistry.register(TGEntities.GHOULING, GhoulingRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CORRUPTED_ILLAGER_MODEL_LAYER, CorruptedIllagerModel::getTexturedModelData);

        /* CHANGING ITEM TEXTURE */
        ModelPredicateProviderRegistry.register(TGItems.VIAL_OF_BLOOD, new Identifier("charged"), (stack, world, entity, seed) -> {
            if (entity != null && stack.isOf(TGItems.VIAL_OF_BLOOD)) {
                return VialOfBlood.getBlood(stack);
            }
            return 0.0F;
        });

        // projectile packet logic
        ClientPlayNetworking.registerGlobalReceiver(GraveyardEntitySpawnPacket.ID, (client, handler, buf, responseSender) -> {
            ClientListener.EntityPacketOnClient.onPacket(client, buf);
        });
    }
}
