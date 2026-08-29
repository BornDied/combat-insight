package com.combatinsight.live;

import com.combatinsight.calculation.TargetProfile;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Known Player-Owned House combat-dummy forms exposed by RuneLite. */
public enum CombatDummy
{
	COMBAT(
		2668,
		29336,
		"Combat dummy",
		false),
	UNDEAD(
		7413,
		29337,
		"Undead combat dummy",
		true,
		"undead", "demon"),
	ORNATE(
		8598,
		9354,
		"Ornate combat dummy",
		false),
	ORNATE_UNDEAD(
		10507,
		9355,
		"Ornate undead combat dummy",
		true,
		"undead", "demon"),
	ORNATE_WILDERNESS(
		10508,
		9356,
		"Ornate wilderness combat dummy",
		false,
		"wilderness"),
	ORNATE_KALPHITE(
		10509,
		9357,
		"Ornate kalphite combat dummy",
		false,
		"kalphite"),
	ORNATE_KURASK(
		10510,
		9358,
		"Ornate kurask combat dummy",
		false,
		"leafy"),
	ORNATE_VAMPYRE(
		10511,
		40430,
		"Ornate vampyre combat dummy",
		false,
		"vampyre2"),
	ORNATE_DRAGON(
		10512,
		40431,
		"Ornate dragon combat dummy",
		false,
		"dragon");

	private final int npcId;
	private final int objectId;
	private final String displayName;
	private final TargetProfile targetProfile;

	CombatDummy(
		int npcId,
		int objectId,
		String displayName,
		boolean slayerMonster,
		String... attributes)
	{
		this.npcId = npcId;
		this.objectId = objectId;
		this.displayName = displayName;
		Set<String> targetAttributes = attributes.length == 0
			? Collections.emptySet()
			: new HashSet<>(Arrays.asList(attributes));
		this.targetProfile = new TargetProfile(
			npcId,
			0,
			0,
			0,
			0,
			1,
			0,
			0,
			0,
			0,
			0,
			0,
			0,
			0,
			"",
			0,
			slayerMonster,
			targetAttributes,
			false);
	}

	public static CombatDummy forNpcId(int npcId)
	{
		for (CombatDummy dummy : values())
		{
			if (dummy.npcId == npcId)
			{
				return dummy;
			}
		}
		return null;
	}

	public static CombatDummy forObjectId(int objectId)
	{
		for (CombatDummy dummy : values())
		{
			if (dummy.objectId == objectId)
			{
				return dummy;
			}
		}
		return null;
	}

	public int getNpcId()
	{
		return npcId;
	}

	public int getObjectId()
	{
		return objectId;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public TargetProfile getTargetProfile()
	{
		return targetProfile;
	}
}
