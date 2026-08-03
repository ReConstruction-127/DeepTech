package dev.celestiacraft.deep_tech.api.register.material;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleMaterialPart implements IMaterialPart {
	private final String path;
	private final String tagFolder;
}