package com.kelvin.aem.scheduler;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        immediate = true,
        metatype = true,
        label = "Sample Scheduler",
        description = "Sample Scheduler for Testing Friendly Cron Expression")
@Service(value = Runnable.class)
@Properties({ @Property(
        label = "Cron expression",
        description = "[every minute = 0 0/1 * * * ?]",
        name = Scheduler.PROPERTY_SCHEDULER_EXPRESSION,
        value = "0 0/1 * * * ?") })
public class SampleScheduler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleScheduler.class);

    @Property(label = "Another Property", description = "Another property for testing", value = "test value")
    private static final String ANOTHER_PROPERTY = "another.property";

    private String expression;
    private String anotherProperty;

    @Override
    public void run() {
        LOGGER.debug("Inside SampleScheduler run method, scheduler.expression is [{}], and anotherProperty is [{}]",
                expression, anotherProperty);
    }

    @Activate
    @Modified
    protected final void activate(final ComponentContext componentContext) {
        LOGGER.debug("Inside SampleScheduler activate method");
        final Dictionary<String, ?> config = componentContext.getProperties();
        expression = PropertiesUtil.toString(config.get(Scheduler.PROPERTY_SCHEDULER_EXPRESSION), null);
        anotherProperty = PropertiesUtil.toString(config.get(ANOTHER_PROPERTY), null);
    }

    @Deactivate
    protected final void deactivate(final ComponentContext ctx) {
        LOGGER.debug("inside SampleScheduler deactivate method");
    }
}
