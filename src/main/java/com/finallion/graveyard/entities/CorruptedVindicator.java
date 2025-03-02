package com.finallion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class CorruptedVindicator extends CorruptedIllager {

    public CorruptedVindicator(EntityType<? extends CorruptedIllager> entityType, World world) {
        super(entityType, world, "corrupted_vindicator");
    }

    @Override
    public boolean isModelDamaged() {
        return true;
    }

    @Override
    public CorruptedIllager.State getState() {
        if (this.isAttacking()) {
            return State.UNDEAD_ATTACKING;
        } else {
            return State.UNDEAD;
        }
    }

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, 0.8F, 0.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT, 0.8F, 0.0F);
    }


    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH, 0.8F, 0.0F);
    }
}
