package matteroverdrive.core.packet.type.serverbound.android;

import matteroverdrive.core.android.api.perk.AndroidPerkManager;
import matteroverdrive.core.android.api.perk.IAndroidPerk;
import matteroverdrive.core.capability.MatterOverdriveCapabilities;
import matteroverdrive.core.packet.type.AbstractOverdrivePacket;
import matteroverdrive.registry.SoundRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAndroidPerkAttemptBuy extends AbstractOverdrivePacket<PacketAndroidPerkAttemptBuy> {

	public String perk = "";

	public PacketAndroidPerkAttemptBuy() {

	}

	public PacketAndroidPerkAttemptBuy(String perk) {
		this.perk = perk;
	}

	@Override
	public void encode(FriendlyByteBuf outBuffer) {
		outBuffer.writeUtf(perk);
	}
	
	@Override
	public boolean handle(PacketAndroidPerkAttemptBuy pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer entity = ctx.get().getSender();
			IAndroidPerk androidPerk = IAndroidPerk.PERKS.get(pkt.perk);
			entity.getCapability(MatterOverdriveCapabilities.ANDROID_DATA).ifPresent(iAndroid -> {
				AndroidPerkManager perkManager = iAndroid.getPerkManager();
				if (!perkManager.hasPerk(androidPerk)
						|| perkManager.getOwned().get(androidPerk.getName()) < androidPerk.getMaxLevel()) {
					int requiredXP = androidPerk.getRequiredXP(iAndroid,
							perkManager.hasPerk(androidPerk) ? perkManager.getOwned().get(androidPerk.getName()) + 1
									: 1);
					if (entity.experienceLevel >= requiredXP) {
						entity.giveExperienceLevels(-requiredXP);
						perkManager.buyPerk(androidPerk);
						entity.level.playSound(entity, entity.blockPosition(), SoundRegistry.PERK_UNLOCK.get(),
								SoundSource.PLAYERS, 0.5f, 1f);
						iAndroid.requestUpdate();
					}
				}
			});
		});
		return true;
	}
	
	public static PacketAndroidPerkAttemptBuy decode(FriendlyByteBuf inBuffer) {
		return new PacketAndroidPerkAttemptBuy(inBuffer.readUtf());
	}


}