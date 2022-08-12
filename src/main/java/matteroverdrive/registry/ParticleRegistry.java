package matteroverdrive.registry;

import matteroverdrive.References;
import matteroverdrive.client.particle.replicator.ParticleOptionReplicator;
import matteroverdrive.client.particle.shockwave.ParticleOptionShockwave;
import matteroverdrive.client.particle.vent.ParticleOptionVent;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistry {

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister
			.create(ForgeRegistries.PARTICLE_TYPES, References.ID);
	
	public static final RegistryObject<ParticleOptionReplicator> PARTICLE_REPLICATOR = PARTICLES.register("replicator",
			() -> new ParticleOptionReplicator());
	public static final RegistryObject<ParticleOptionShockwave> PARTICLE_SHOCKWAVE = PARTICLES.register("shockwave",
			() -> new ParticleOptionShockwave());
	public static final RegistryObject<ParticleOptionVent> PARTICLE_VENT = PARTICLES.register("vent",
			() -> new ParticleOptionVent());

	
}
