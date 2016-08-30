package org.zalando.tracer.spring;

import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.zalando.tracer.Tracer;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class TracerTaskSchedulerInteractionTest {
    
    private final Tracer tracer = mock(Tracer.class);
    private final TaskScheduler scheduler = mock(TaskScheduler.class);
    private final TaskScheduler unit = new TracerTaskScheduler(tracer, scheduler);

    private final Runnable task = mock(Runnable.class);
    private final Runnable decorated = mock(Runnable.class);
    
    @Before
    public void setUp() {
        when(tracer.manage(any(Runnable.class))).thenReturn(decorated);
    }

    @Test
    public void shouldScheduleTrigger() throws Exception {
        final CronTrigger trigger = new CronTrigger("0 0/1 * 1/1 * ?");
        unit.schedule(task, trigger);

        verify(tracer).manage(task);
        verify(scheduler).schedule(decorated, trigger);
    }

    @Test
    public void shouldScheduleDate() throws Exception {
        final Date now = new Date();
        unit.schedule(task, now);
        
        verify(tracer).manage(task);
        verify(scheduler).schedule(decorated, now);
    }

    @Test
    public void shouldScheduleAtFixedRate() throws Exception {
        unit.scheduleAtFixedRate(task, 1);
        
        verify(tracer).manage(task);
        verify(scheduler).scheduleAtFixedRate(decorated, 1);
    }

    @Test
    public void shouldScheduleAtFixedRateWithDate() throws Exception {
        final Date now = new Date();
        unit.scheduleAtFixedRate(task, now, 1);
        
        verify(tracer).manage(task);
        verify(scheduler).scheduleAtFixedRate(decorated, now, 1);
    }

    @Test
    public void shouldScheduleWithFixedDelay() throws Exception {
        unit.scheduleWithFixedDelay(task, 1);
        
        verify(tracer).manage(task);
        verify(scheduler).scheduleWithFixedDelay(decorated, 1);
    }

    @Test
    public void shouldScheduleWithFixedDelayWithDate() throws Exception {
        final Date now = new Date();
        unit.scheduleWithFixedDelay(task, now, 1);
        
        verify(tracer).manage(task);
        verify(scheduler).scheduleWithFixedDelay(decorated, now, 1);
    }

}