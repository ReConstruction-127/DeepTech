package dev.celestiacraft.deep_tech.compat.jade.api;

import dev.celestiacraft.deep_tech.DeepTech;

public interface IJadeUtils {
	default String addTranKey(String key) {
		return String.format(key, DeepTech.MODID);
	}
}