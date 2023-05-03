/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.echobox.logging.escalator;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementation of the escalation trigger that triggers and escalation when a particular event
 * has been happening at a given interval for a given period. i.e. has been happening
 * consistently for a period of time.
 *
 * Used a {@link ConcurrentHashMap} to store these times in memory.
 *
 * @author eddspencer
 */
public class TimeBasedEscalationTrigger implements EscalationTrigger {
  
  private final Supplier<Long> unixTimeNow;
  private final int minIntervalSecs;
  private final int escalationPeriodSecs;
  private final Map<String, EventInfo> cache;
  
  public TimeBasedEscalationTrigger(Supplier<Long> unixTimeNow, int minIntervalSecs,
      int escalationPeriodSecs) {
    this.unixTimeNow = unixTimeNow;
    this.minIntervalSecs = minIntervalSecs;
    this.escalationPeriodSecs = escalationPeriodSecs;
    this.cache = new ConcurrentHashMap<>();
  }
  
  public TimeBasedEscalationTrigger(int minIntervalSecs, int escalationPeriodSecs) {
    this.unixTimeNow = () -> System.currentTimeMillis() / 1000;
    this.minIntervalSecs = minIntervalSecs;
    this.escalationPeriodSecs = escalationPeriodSecs;
    this.cache = new ConcurrentHashMap<>();
  }
  
  @Override
  public boolean markAndTrigger(LoggingLevel level, Object... keys) {
    final String key = createKey(level, keys);
    final long now = unixTimeNow.get();
    
    final AtomicBoolean escalate = new AtomicBoolean(false);
    cache.compute(key, (k, eventInfo) -> {
      // If we have no other events just cache this one
      if (eventInfo == null) {
        return new EventInfo(now, now);
      }
      
      final boolean escalationPeriodPassed =
          now - eventInfo.firstSeenUnixTime >= escalationPeriodSecs;
      final boolean withinInterval = now - eventInfo.lastSeenUnixTime <= minIntervalSecs;
      
      // Trigger escalation and reset event if enough time has passed
      if (escalationPeriodPassed && withinInterval) {
        escalate.set(true);
        return null;
      }
      
      // Otherwise update event info
      return new EventInfo(eventInfo.firstSeenUnixTime, now);
    });
    
    return escalate.get();
  }
  
  private String createKey(LoggingLevel level, Object[] keys) {
    return String.format("%s::%s", level,
        Arrays.stream(keys).map(Object::toString).collect(Collectors.joining("_")));
  }
  
  /**
   * Event information for the time based event cache
   *
   * @author eddspencer
   */
  private static class EventInfo {
    private final long firstSeenUnixTime;
    private final long lastSeenUnixTime;
    
    private EventInfo(long firstSeenUnixTime, long lastSeenUnixTime) {
      this.firstSeenUnixTime = firstSeenUnixTime;
      this.lastSeenUnixTime = lastSeenUnixTime;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      EventInfo eventInfo = (EventInfo) obj;
      return firstSeenUnixTime == eventInfo.firstSeenUnixTime
          && lastSeenUnixTime == eventInfo.lastSeenUnixTime;
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(firstSeenUnixTime, lastSeenUnixTime);
    }
  }
}