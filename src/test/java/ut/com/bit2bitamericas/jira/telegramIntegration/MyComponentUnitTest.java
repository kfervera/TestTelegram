package ut.com.bit2bitamericas.jira.telegramIntegration;

import org.junit.Test;
import com.bit2bitamericas.jira.telegramIntegration.api.MyPluginComponent;
import com.bit2bitamericas.jira.telegramIntegration.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}