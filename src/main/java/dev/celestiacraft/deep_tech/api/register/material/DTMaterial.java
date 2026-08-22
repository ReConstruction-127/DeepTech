package dev.celestiacraft.deep_tech.api.register.material;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.libs.api.register.item.BasicItem;
import dev.celestiacraft.libs.tags.TagsBuilder;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DTMaterial {
	private final String id;

	private final Set<IMaterialPart> parts = new LinkedHashSet<>();
	private final Map<IMaterialPart, ItemEntry<BasicItem>> entries = new LinkedHashMap<>();

	public DTMaterial(String id) {
		this.id = id;
	}

	public DTMaterial part(IMaterialPart part) {
		parts.add(part);
		return this;
	}

	public DTMaterial ingot() {
		return part(IMaterialPart.INGOT);
	}

	public DTMaterial nugget() {
		return part(IMaterialPart.NUGGET);
	}

	public DTMaterial plate() {
		return part(IMaterialPart.PLATE);
	}

	public DTMaterial dust() {
		return part(IMaterialPart.DUST);
	}

	public DTMaterial raw() {
		return part(IMaterialPart.RAW_MATERIAL);
	}

	public void registerMaterial() {
		parts.forEach((part) -> {
			entries.put(part, registerPart(part));
		});
	}

	private ItemEntry<BasicItem> registerPart(IMaterialPart part) {
		String registryName = String.format("%s_%s", id, part.getName());

		return DeepTech.REGISTRATE.item(registryName, BasicItem::new)
				.model(ItemModelGen.generated(part.getModelPath(id)))
				.tab(DTCreativeTabs.MATERIAL.getKey())
				.tag(TagsBuilder.item(part.getTagFolder()).forge())
				.tag(TagsBuilder.item(part.getMaterialTag(id)).forge())
				.register();
	}

	public ItemEntry<BasicItem> get(IMaterialPart part) {
		return entries.get(part);
	}

	public ItemEntry<BasicItem> getIngot() {
		return get(IMaterialPart.INGOT);
	}

	public ItemEntry<BasicItem> getNugget() {
		return get(IMaterialPart.NUGGET);
	}

	public ItemEntry<BasicItem> getPlate() {
		return get(IMaterialPart.PLATE);
	}

	public ItemEntry<BasicItem> getDust() {
		return get(IMaterialPart.DUST);
	}

	public ItemEntry<BasicItem> getRaw() {
		return get(IMaterialPart.RAW_MATERIAL);
	}
}