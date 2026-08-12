package com.combatinsight.live;

import com.combatinsight.calculation.TargetProfile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** Loads the compact, bundled OSRS Wiki target-stat snapshot. */
public final class TargetDatabase
{
	private static final String RESOURCE = "/com/combatinsight/data/monsters.tsv";
	private static final Map<Integer, TargetProfile> TARGETS = load();

	private TargetDatabase()
	{
	}

	public static TargetProfile find(int npcId)
	{
		return TARGETS.get(npcId);
	}

	public static int size()
	{
		return TARGETS.size();
	}

	private static Map<Integer, TargetProfile> load()
	{
		InputStream stream = TargetDatabase.class.getResourceAsStream(RESOURCE);
		if (stream == null)
		{
			throw new IllegalStateException("Missing target data resource " + RESOURCE);
		}

		Map<Integer, TargetProfile> targets = new HashMap<>();
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
				if (fields.length != 19)
				{
					throw new IllegalStateException("Invalid target data row: " + line);
				}

				int id = number(fields[0]);
				HashSet<String> attributes = new HashSet<>();
				if (!fields[17].isEmpty())
				{
					attributes.addAll(Arrays.asList(fields[17].split(",")));
				}
				TargetProfile target = new TargetProfile(
					id,
					number(fields[1]),
					number(fields[2]),
					number(fields[3]),
					number(fields[4]),
					number(fields[5]),
					number(fields[6]),
					number(fields[7]),
					number(fields[8]),
					number(fields[9]),
					number(fields[10]),
					number(fields[11]),
					number(fields[12]),
					number(fields[13]),
					"-".equals(fields[14]) ? "" : fields[14],
					number(fields[15]),
					"1".equals(fields[16]),
					attributes,
					"1".equals(fields[18]));
				targets.put(id, target);
			}
		}
		catch (IOException | NumberFormatException ex)
		{
			throw new IllegalStateException("Could not load bundled target data", ex);
		}
		return Collections.unmodifiableMap(targets);
	}

	private static int number(String value)
	{
		return Integer.parseInt(value);
	}
}
