package com.combatinsight;
import org.junit.Test;
import static org.junit.Assert.*;
public class InspectorSessionTest {
    @Test public void freezeKeepsDisplayedDocumentWhileLiveUpdatesContinue() {
        InspectorSession session=new InspectorSession();
        InspectorDocument first=new InspectorDocument("First", "report-one", new String[]{"one"});
        InspectorDocument second=new InspectorDocument("Second", "report-two", new String[]{"two"});
        session.update(first); session.toggleFreeze(); session.update(second);
        assertSame(first,session.displayed()); assertTrue(session.isFrozen());
        session.toggleFreeze(); assertSame(second,session.displayed()); assertFalse(session.isFrozen());
    }
    @Test public void clearDropsFrozenDataAndCopyUsesDisplayedReport() {
        InspectorSession session=new InspectorSession();
        session.update(new InspectorDocument("First","exact report",new String[]{"one"}));
        session.toggleFreeze(); assertEquals("exact report",session.displayed().report);
        session.clear(); assertFalse(session.isFrozen()); assertNull(session.displayed());
    }
    @Test public void documentCopiesSections() {
        String[] sections={"original"};
        InspectorDocument doc=new InspectorDocument("title","report",sections);
        sections[0]="changed";
        assertEquals("original",doc.section(0));
    }
}
