package com.combatinsight;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.runelite.client.util.Filepath;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class RaidRecordingExportTest
{
    @Rule public TemporaryFolder temp = new TemporaryFolder();

    @Test public void writesCompleteUtf8ReportToSelectedFilepath() throws Exception
    {
        Path output = temp.getRoot().toPath().resolve("raid.txt");
        CombatInsightPlugin.writeRecording(Filepath.Unchecked.getRooted(output), "Raid — test\n");
        assertEquals("Raid — test\n", new String(Files.readAllBytes(output), StandardCharsets.UTF_8));
    }
}
