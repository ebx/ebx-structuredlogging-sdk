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

package com.echobox.logging;

import ch.qos.logback.classic.net.SyslogAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;

/**
 * A variant {@link SyslogAppender} admitting a specified {@code Layout} that will be attached to
 * the end of each message.
 *
 * @author MarcF
 */
public class SyslogAppenderWithAppendix extends SyslogAppender {
  /**
   * The layout to append to each message.
   */
  protected Layout<ILoggingEvent> appendixLayout;

  @Override
  public Layout<ILoggingEvent> buildLayout() {
    // First layout is just the base layout:
    Layout<ILoggingEvent> syslogLayout = super.buildLayout();

    // Second layout comes from this class, probably set in a config file.
    TwoPartLayout<ILoggingEvent> layout = new TwoPartLayout<>(syslogLayout, appendixLayout);

    layout.setContext(getContext());
    layout.start();

    return layout;
  }

  /**
   * Public setter to allow the layout to be set from a config file.
   *
   * @param appendixLayout the appendix layout
   */
  public void setAppendixLayout(Layout<ILoggingEvent> appendixLayout) {
    this.appendixLayout = appendixLayout;
  }
}
