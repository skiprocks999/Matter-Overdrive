package matteroverdrive.core.sound.item;

import java.util.UUID;

import matteroverdrive.SoundRegister;
import matteroverdrive.common.item.tools.electric.ItemMatterScanner;
import matteroverdrive.core.utils.UtilsItem;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.core.utils.UtilsWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TickableSoundMatterScanner extends AbstractTickableSoundInstance {

	private final InteractionHand hand;
	private final UUID id;
	
	private BlockPos previousPos;
	private Item previousBlock;
	
	private Player originPlayer;
	
	private static final float MAX_DISTANCE = 10.0F;
	
	public TickableSoundMatterScanner(InteractionHand hand, UUID id) {
		super(SoundRegister.SOUND_MATTER_SCANNER_RUNNING.get(), SoundSource.PLAYERS);
		this.hand = hand;
		this.id = id;
		this.volume = 0.5F;
		this.pitch = 1.0F;
		this.looping = true;
	}

	@Override
	public void tick() {
		Minecraft minecraft = Minecraft.getInstance();
		originPlayer = minecraft.level.getPlayerByUUID(id);
		if(checkStop()) {
			stop();
		}
		this.volume = getSoundVolume();
		this.pitch = 1.0F;
	}
	
	private float getSoundVolume() {
		double distance = UtilsWorld.distanceBetweenPositions(originPlayer.getOnPos(), Minecraft.getInstance().player.getOnPos());
		if(distance > 1.0F && distance <= MAX_DISTANCE) {
			return 0.5F / MAX_DISTANCE;
		} else if (distance <= 1.0F) {
			return 0.5F;
		} else {
			return 0.0F;
		}
	}
	
	private boolean checkStop() {
		if(originPlayer == null || originPlayer.isRemoved()) {
			return true;
		}
		ItemStack scanner = originPlayer.getItemInHand(hand);
		if(scanner.isEmpty()) {
			return true;
		}
		if(scanner.getItem() instanceof ItemMatterScanner matter && scanner.hasTag() && matter.isOn(scanner) && matter.isPowered(scanner) && matter.isHeld(scanner)) {
			
			CompoundTag tag = scanner.getTag();
			
			if(previousBlock == null || previousPos == null) {
				Item item = UtilsNbt.getItemFromString(tag.getString(UtilsNbt.ITEM));
				if(item == null) {
					return true;
				}
				previousBlock = item;
				if(!tag.contains(ItemMatterScanner.RAY_TRACE_POS)) {
					return true;
				}
				previousPos = NbtUtils.readBlockPos(tag.getCompound(ItemMatterScanner.RAY_TRACE_POS));
				
				return false;
			}
			
			Item item = UtilsNbt.getItemFromString(tag.getString(UtilsNbt.ITEM));
			if(item == null) {
				return true;
			}
			
			if(!tag.contains(ItemMatterScanner.RAY_TRACE_POS)) {
				return true;
			}
			BlockPos pos = NbtUtils.readBlockPos(tag.getCompound(ItemMatterScanner.RAY_TRACE_POS)); 
			
			return !UtilsItem.compareItems(previousBlock, item) || !pos.equals(pos);
		} 
		return true;
	}

}
