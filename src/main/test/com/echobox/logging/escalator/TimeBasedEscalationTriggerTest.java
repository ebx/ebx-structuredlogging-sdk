package com.echobox.logging.escalator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimeBasedEscalationTriggerTest {
  
  private TimeBasedEscalationTrigger trigger;
  private long now;
  
  @BeforeEach
  public void setup() {
    trigger = new TimeBasedEscalationTrigger(() -> now, 30, 60);
  }
  
  @Test
  public void doesNotEscalateOnFirstEvent() {
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
  }
  
  @Test
  public void doesNotEscalateIfNotEnoughTimePassed() {
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
    now += 30;
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
  }
  
  @Test
  public void doesEscalateIfEnoughTimePassed() {
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
    now += 30;
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
    now += 30;
    assertTrue(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
  }
  
  @Test
  public void resetOnEventEscalation() {
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
    now += 30;
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
    now += 30;
    assertTrue(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
    now += 10;
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
    now += 50;
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
    now += 10;
    assertTrue(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
  }
  
  @Test
  public void canKeepTrackOfMultipleEventsByLevel() {
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
    assertFalse(trigger.markAndTrigger(LoggingLevel.ERROR, "event"));
    now += 30;
    assertFalse(trigger.markAndTrigger(LoggingLevel.ERROR, "event"));
    now += 30;
    assertTrue(trigger.markAndTrigger(LoggingLevel.ERROR, "event"));
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event"));
  }
  
  @Test
  public void canKeepTrackOfMultipleEventsByKey() {
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event1"));
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event2"));
    now += 30;
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event1"));
    now += 30;
    assertTrue(trigger.markAndTrigger(LoggingLevel.DEBUG, "event1"));
    assertFalse(trigger.markAndTrigger(LoggingLevel.DEBUG, "event2"));
  }
}