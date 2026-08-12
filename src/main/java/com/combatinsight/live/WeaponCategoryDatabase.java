package com.combatinsight.live;

import com.combatinsight.calculation.WeaponCategory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Resolves equipped weapon item IDs to their combat-interface category. */
final class WeaponCategoryDatabase
{
	private static final String RESOURCE = "/com/combatinsight/data/weapon-categories.tsv";
	private static final Map<Integer, WeaponCategory> CATEGORIES = load();

	private WeaponCategoryDatabase()
	{
	}

	static WeaponCategory find(int itemId)
	{
		return CATEGORIES.getOrDefault(itemId, WeaponCategory.UNKNOWN);
	}

	private static Map<Integer, WeaponCategory> load()
	{
		InputStream stream = WeaponCategoryDatabase.class.getResourceAsStream(RESOURCE);
		if (stream == null)
		{
			throw new IllegalStateException("Missing weapon category resource " + RESOURCE);
		}

		Map<Integer, WeaponCategory> categories = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(
			new InputStreamReader(stream, StandardCharsets.UTF_8)))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.isEmpty() || line.charAt(0) == '#')
				{
					continue;
				}
				String[] fields = line.split("\\t", -1);
				if (fields.length != 2)
				{
					throw new IllegalStateException("Invalid weapon category row: " + line);
				}
				categories.put(
					Integer.parseInt(fields[0]),
					WeaponCategory.fromDataName(fields[1]));
			}
		}
		catch (IOException | NumberFormatException ex)
		{
			throw new IllegalStateException("Could not load bundled weapon categories", ex);
		}
		return Collections.unmodifiableMap(categories);
	}
}
