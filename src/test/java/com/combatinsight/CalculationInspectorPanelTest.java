package com.combatinsight;

import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class CalculationInspectorPanelTest
{
    @Test public void frozenPanelCopiesVisibleDataAndResumesLatestData() throws Exception
    {
        SwingUtilities.invokeAndWait(() -> {
            AtomicReference<String> copied=new AtomicReference<>();
            CalculationInspectorPanel panel=new CalculationInspectorPanel(copied::set);
            String[] sections={"Magic:112","Target details","Special unavailable","No notices","Full report","Selected effects","Input sources"};
            panel.update(new InspectorDocument("First snapshot","first-report",sections));
            button(panel,"Freeze snapshot").doClick();
            panel.update(new InspectorDocument("New snapshot","new-report",sections));
            button(panel,"Copy diagnostic").doClick(); assertEquals("first-report",copied.get());
            button(panel,"Resume live").doClick();
            button(panel,"Copy diagnostic").doClick(); assertEquals("new-report",copied.get());
            panel.clear(); assertFalse(button(panel,"Copy diagnostic").isEnabled());
            assertFalse(panel.isActive());
        });
    }

    @Test public void sidebarRendersAtRuneLiteWidthWithoutHorizontalOverflow() throws Exception
    {
        SwingUtilities.invokeAndWait(() -> {
            CalculationInspectorPanel panel=new CalculationInspectorPanel(x -> {});
            panel.update(InspectorDocument.capture(com.combatinsight.live.LiveCombatSnapshot.empty()));
            panel.setSize(225,850); layout(panel);
            for(Component c:panel.getComponents()) assertTrue("Child wider than sidebar",c.getX()+c.getWidth()<=225);
            BufferedImage image=new BufferedImage(225,850,BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g=image.createGraphics(); panel.printAll(g);g.dispose();
            String output=System.getProperty("combatinsight.inspectorPreview");
            if(output!=null) try {ImageIO.write(image,"png",new File(output));} catch(Exception e){throw new RuntimeException(e);}
        });
    }

    private static void layout(Container c) {c.doLayout();for(Component x:c.getComponents())if(x instanceof Container)layout((Container)x);}
    private static JButton button(Container c,String text) {
        for(Component child:c.getComponents()) {
            if(child instanceof JButton && text.equals(((JButton)child).getText()))return (JButton)child;
            if(child instanceof Container) { JButton b=find((Container)child,text);if(b!=null)return b; }
        }
        throw new AssertionError("Missing button "+text);
    }
    private static JButton find(Container c,String text) {
        for(Component child:c.getComponents()) {
            if(child instanceof JButton && text.equals(((JButton)child).getText()))return (JButton)child;
            if(child instanceof Container){JButton b=find((Container)child,text);if(b!=null)return b;}
        }
        return null;
    }
}
