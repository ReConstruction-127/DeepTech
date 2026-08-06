package dev.celestiacraft.deep_tech.compat.jade;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.compat.jade.machine.MachineBasicInfo;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class DeepTechJadePlugin implements IWailaPlugin {
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(new MachineBasicInfo(), MachineBlock.class);
		DeepTech.registerLog("Jade Plugins");
	}
}