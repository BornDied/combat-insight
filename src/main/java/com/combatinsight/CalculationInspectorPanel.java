package com.combatinsight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.PluginPanel;

/** A passive inspector: all game data arrives as immutable text, on the EDT. */
final class CalculationInspectorPanel extends PluginPanel
{
    private static final Color GOLD=new Color(255,200,83);
    private final InspectorSession session=new InspectorSession();
    private final JTextArea summary=textArea();
    private final JTextArea[] details=new JTextArea[7];
    private final JLabel state=new JLabel("Live");
    private final JButton freeze=new JButton("Freeze snapshot");
    private final JButton copy=new JButton("Copy diagnostic");
    private boolean active;

    CalculationInspectorPanel(Consumer<String> copier) { this(copier, () -> "Recording unavailable."); }

    CalculationInspectorPanel(Consumer<String> copier, java.util.function.Supplier<String> encounterReport)
    {
        super();
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(8,6,8,6));
        JLabel heading=new JLabel("Calculation inspector");
        heading.setForeground(GOLD);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD,15f));
        heading.setAlignmentX(LEFT_ALIGNMENT);
        add(heading); add(Box.createVerticalStrut(8));
        summary.setAlignmentX(LEFT_ALIGNMENT); add(summary);
        state.setForeground(GOLD); state.setAlignmentX(LEFT_ALIGNMENT); add(state);
        add(Box.createVerticalStrut(8));
        String[] titles={"Inputs","Target details","Special attack","Notices","Calculation report","Applied effects","Input sources"};
        for(int i=0;i<titles.length;i++)
        {
            JTextArea body=textArea(); details[i]=body;
            JPanel section=new JPanel(new BorderLayout()) {
                @Override public Dimension getMaximumSize() {
                    return new Dimension(Integer.MAX_VALUE,getPreferredSize().height);
                }
            };
            section.setOpaque(false);
            section.setAlignmentX(LEFT_ALIGNMENT);
            section.setBorder(new EmptyBorder(3,0,3,0));
            JToggleButton toggle=new JToggleButton("▸ " + titles[i]);
            toggle.setBackground(new Color(45,45,45));
            toggle.setHorizontalAlignment(SwingConstants.LEFT); toggle.setForeground(GOLD);
            final String title=titles[i];
            toggle.addActionListener(e -> {
                body.setVisible(toggle.isSelected());
                toggle.setText((toggle.isSelected()?"▾ ":"▸ ")+title);
                revalidate(); repaint();
            });
            body.setVisible(false);
            section.add(toggle,BorderLayout.NORTH); section.add(body,BorderLayout.CENTER);
            add(section);
        }
        JPanel buttons=new JPanel() {
            @Override public Dimension getMaximumSize() {return new Dimension(Integer.MAX_VALUE,getPreferredSize().height);}
        }; buttons.setOpaque(false); buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));
        buttons.setAlignmentX(LEFT_ALIGNMENT);
        freeze.setBackground(new Color(45,45,45)); freeze.setForeground(Color.WHITE);
        copy.setBackground(new Color(45,45,45)); copy.setForeground(Color.WHITE);
        freeze.setAlignmentX(LEFT_ALIGNMENT); copy.setAlignmentX(LEFT_ALIGNMENT);
        freeze.addActionListener(e -> {session.toggleFreeze(); renderDocument();});
        copy.addActionListener(e -> {
            InspectorDocument doc=session.displayed();
            if(doc!=null) copier.accept(doc.report);
        });
        buttons.add(freeze); buttons.add(Box.createVerticalStrut(4)); buttons.add(copy); add(buttons);
        JButton copyEncounter=new JButton("Copy encounter report");
        copyEncounter.setAlignmentX(LEFT_ALIGNMENT);
        copyEncounter.addActionListener(e -> copier.accept(encounterReport.get()));
        buttons.add(Box.createVerticalStrut(4));buttons.add(copyEncounter);
        renderDocument();
    }

    @Override public void onActivate() { active=true; }
    @Override public void onDeactivate() { active=false; }
    boolean isActive() { return active; }

    void update(InspectorDocument doc)
    {
        session.update(doc);
        if(!session.isFrozen()) renderDocument();
    }

    void clear()
    {
        session.clear(); active=false; renderDocument();
    }

    private void renderDocument()
    {
        InspectorDocument doc=session.displayed();
        summary.setText(doc==null?"Open while logged in to inspect a calculation.":doc.summary);
        for(int i=0;i<details.length;i++) details[i].setText(doc==null?"":doc.section(i));
        state.setText(session.isFrozen()?"Snapshot frozen — HUD remains live":"Live snapshot");
        freeze.setText(session.isFrozen()?"Resume live":"Freeze snapshot");
        freeze.setEnabled(doc!=null); copy.setEnabled(doc!=null);
        revalidate(); repaint();
    }

    private static JTextArea textArea()
    {
        JTextArea area=new JTextArea() {
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE,getPreferredSize().height);
            }
        };
        area.setEditable(false); area.setLineWrap(true); area.setWrapStyleWord(true);
        area.setOpaque(false); area.setForeground(new Color(220,220,220));
        area.setBorder(new EmptyBorder(5,2,7,2));
        area.setColumns(19); area.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));
        return area;
    }
}
