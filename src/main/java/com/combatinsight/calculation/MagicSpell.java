package com.combatinsight.calculation;

/**
 * Manual spell fallback for combat spells RuneLite cannot reliably expose as
 * a live equipment value. Powered staves are detected directly and ignore this
 * setting.
 */
public enum MagicSpell
{
	NOT_SET("Not set", 0, Spellbook.NONE, SpellType.NONE),

	WIND_STRIKE("Wind Strike", 8, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.AIR),
	WATER_STRIKE("Water Strike", 8, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.WATER),
	EARTH_STRIKE("Earth Strike", 8, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.EARTH),
	FIRE_STRIKE("Fire Strike", 8, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.FIRE),
	WIND_BOLT("Wind Bolt", 12, Spellbook.STANDARD, SpellType.BOLT, Element.AIR),
	WATER_BOLT("Water Bolt", 12, Spellbook.STANDARD, SpellType.BOLT, Element.WATER),
	EARTH_BOLT("Earth Bolt", 12, Spellbook.STANDARD, SpellType.BOLT, Element.EARTH),
	FIRE_BOLT("Fire Bolt", 12, Spellbook.STANDARD, SpellType.BOLT, Element.FIRE),
	CRUMBLE_UNDEAD("Crumble Undead", 15, Spellbook.STANDARD, SpellType.TARGET_DEPENDENT),
	WIND_BLAST("Wind Blast", 16, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.AIR),
	WATER_BLAST("Water Blast", 16, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.WATER),
	EARTH_BLAST("Earth Blast", 16, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.EARTH),
	FIRE_BLAST("Fire Blast", 16, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.FIRE),
	IBAN_BLAST("Iban Blast", 25, Spellbook.STANDARD, SpellType.NONE),
	MAGIC_DART("Magic Dart", 0, Spellbook.STANDARD, SpellType.LEVEL_SCALED),
	SARADOMIN_STRIKE("Saradomin Strike", 20, Spellbook.STANDARD, SpellType.GOD),
	CLAWS_OF_GUTHIX("Claws of Guthix", 20, Spellbook.STANDARD, SpellType.GOD),
	FLAMES_OF_ZAMORAK("Flames of Zamorak", 20, Spellbook.STANDARD, SpellType.GOD),
	WIND_WAVE("Wind Wave", 20, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.AIR),
	WATER_WAVE("Water Wave", 20, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.WATER),
	EARTH_WAVE("Earth Wave", 20, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.EARTH),
	FIRE_WAVE("Fire Wave", 20, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.FIRE),
	WIND_SURGE("Wind Surge", 24, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.AIR),
	WATER_SURGE("Water Surge", 24, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.WATER),
	EARTH_SURGE("Earth Surge", 24, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.EARTH),
	FIRE_SURGE("Fire Surge", 24, Spellbook.STANDARD, SpellType.ELEMENTAL, Element.FIRE),

	SMOKE_RUSH("Smoke Rush", 13, Spellbook.ANCIENT, SpellType.NONE),
	SHADOW_RUSH("Shadow Rush", 14, Spellbook.ANCIENT, SpellType.NONE),
	BLOOD_RUSH("Blood Rush", 15, Spellbook.ANCIENT, SpellType.NONE),
	ICE_RUSH("Ice Rush", 16, Spellbook.ANCIENT, SpellType.NONE),
	SMOKE_BURST("Smoke Burst", 17, Spellbook.ANCIENT, SpellType.NONE),
	SHADOW_BURST("Shadow Burst", 18, Spellbook.ANCIENT, SpellType.NONE),
	BLOOD_BURST("Blood Burst", 21, Spellbook.ANCIENT, SpellType.NONE),
	ICE_BURST("Ice Burst", 22, Spellbook.ANCIENT, SpellType.NONE),
	SMOKE_BLITZ("Smoke Blitz", 23, Spellbook.ANCIENT, SpellType.NONE),
	SHADOW_BLITZ("Shadow Blitz", 24, Spellbook.ANCIENT, SpellType.NONE),
	BLOOD_BLITZ("Blood Blitz", 25, Spellbook.ANCIENT, SpellType.NONE),
	ICE_BLITZ("Ice Blitz", 26, Spellbook.ANCIENT, SpellType.NONE),
	SMOKE_BARRAGE("Smoke Barrage", 27, Spellbook.ANCIENT, SpellType.NONE),
	SHADOW_BARRAGE("Shadow Barrage", 28, Spellbook.ANCIENT, SpellType.NONE),
	BLOOD_BARRAGE("Blood Barrage", 29, Spellbook.ANCIENT, SpellType.NONE),
	ICE_BARRAGE("Ice Barrage", 30, Spellbook.ANCIENT, SpellType.NONE),

	GHOSTLY_GRASP("Ghostly Grasp", 12, Spellbook.ARCEUUS, SpellType.NONE),
	SKELETAL_GRASP("Skeletal Grasp", 17, Spellbook.ARCEUUS, SpellType.NONE),
	UNDEAD_GRASP("Undead Grasp", 24, Spellbook.ARCEUUS, SpellType.NONE),
	INFERIOR_DEMONBANE("Inferior Demonbane", 16, Spellbook.ARCEUUS, SpellType.TARGET_DEPENDENT),
	SUPERIOR_DEMONBANE("Superior Demonbane", 23, Spellbook.ARCEUUS, SpellType.TARGET_DEPENDENT),
	DARK_DEMONBANE("Dark Demonbane", 30, Spellbook.ARCEUUS, SpellType.TARGET_DEPENDENT);

	public enum Spellbook
	{
		NONE,
		STANDARD,
		ANCIENT,
		ARCEUUS
	}

	public enum SpellType
	{
		NONE,
		ELEMENTAL,
		BOLT,
		LEVEL_SCALED,
		GOD,
		TARGET_DEPENDENT
	}

	public enum Element
	{
		NONE,
		AIR,
		WATER,
		EARTH,
		FIRE
	}

	private final String displayName;
	private final int baseMaxHit;
	private final Spellbook spellbook;
	private final SpellType spellType;
	private final Element element;

	MagicSpell(String displayName, int baseMaxHit, Spellbook spellbook, SpellType spellType)
	{
		this(displayName, baseMaxHit, spellbook, spellType, Element.NONE);
	}

	MagicSpell(
		String displayName,
		int baseMaxHit,
		Spellbook spellbook,
		SpellType spellType,
		Element element)
	{
		this.displayName = displayName;
		this.baseMaxHit = baseMaxHit;
		this.spellbook = spellbook;
		this.spellType = spellType;
		this.element = element;
	}

	public boolean isSet()
	{
		return this != NOT_SET;
	}

	public int getBaseMaxHit()
	{
		return baseMaxHit;
	}

	public Spellbook getSpellbook()
	{
		return spellbook;
	}

	public SpellType getSpellType()
	{
		return spellType;
	}

	public String getElementName()
	{
		return element == Element.NONE ? "" : element.name().toLowerCase();
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
