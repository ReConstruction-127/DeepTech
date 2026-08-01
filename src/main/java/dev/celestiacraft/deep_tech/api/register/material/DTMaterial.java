package dev.celestiacraft.deep_tech.api.register.material;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.libs.api.register.item.BasicItem;
import dev.celestiacraft.libs.tags.TagsBuilder;

import java.util.EnumMap;
import java.util.EnumSet;

public class DTMaterial {
	private final String id;

	private final EnumSet<MaterialPart> parts = EnumSet.noneOf(MaterialPart.class);
	private final EnumMap<MaterialPart, ItemEntry<BasicItem>> entries = new EnumMap<>(MaterialPart.class);

	public DTMaterial(String id) {
		this.id = id;
	}

	public DTMaterial ingot() {
		parts.add(MaterialPart.INGOT);
		return this;
	}

	public DTMaterial nugget() {
		parts.add(MaterialPart.NUGGET);
		return this;
	}

	public DTMaterial plate() {
		parts.add(MaterialPart.PLATE);
		return this;
	}

	public DTMaterial dust() {
		parts.add(MaterialPart.DUST);
		return this;
	}

	public void registerMaterial() {
		parts.forEach((part) -> {
			entries.put(part, registerPart(part));
		});
	}

	private ItemEntry<BasicItem> registerPart(MaterialPart part) {
		String registryName = String.format(
				"%s_%s",
				id,
				part.getPath()
		);

		String modelPath = String.format(
				"item/material/%s/%s",
				part.getPath(),
				id
		);

		return DeepTech.REGISTRATE.item(registryName, BasicItem::new)
				.model(ItemModelGen.generated(modelPath))
				.tab(DTCreativeTabs.getTabKey("material"))
				.tag(TagsBuilder.item(part.getTagFolder()).forge())
				.tag(TagsBuilder.item(part.getMaterialTag(id)).forge())
				.register();
	}

	public ItemEntry<BasicItem> get(MaterialPart part) {
		return entries.get(part);
	}

	public ItemEntry<BasicItem> getIngot() {
		return get(MaterialPart.INGOT);
	}

	public ItemEntry<BasicItem> getNugget() {
		return get(MaterialPart.NUGGET);
	}

	public ItemEntry<BasicItem> getPlate() {
		return get(MaterialPart.PLATE);
	}

	public ItemEntry<BasicItem> getDust() {
		return get(MaterialPart.DUST);
	}
}