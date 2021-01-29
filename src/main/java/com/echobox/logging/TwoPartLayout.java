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

import ch.qos.logback.core.Context;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.LayoutBase;

/**
 * A wrapper for a pair of Layouts.
 *
 * When {@link #doLayout(Object)} is called on an event, it applies each layout to the event and
 * returns the concatenation of the results.
 *
 * @param <E> The type parameter for the Layout
 * 
 * @author MarcF
 */
public class TwoPartLayout<E> extends LayoutBase<E> {

  /**
   * The first layout, which will make up the beginning of each message.
   */
  protected Layout<E> first;

  /**
   * The second layout, which will make up the end of each message.
   */
  protected Layout<E> second;

  /**
   * Construct a new {@code TwoPartLayout} from the provided {@code Layout}s.
   *
   * @param first the first
   * @param second the second
   */
  public TwoPartLayout(Layout<E> first, Layout<E> second) {
    this.first = first;
    this.second = second;
  }

  /*
   * (non-Javadoc)
   * 
   * @see ch.qos.logback.core.Layout#doLayout(java.lang.Object)
   */
  @Override
  public String doLayout(E event) {
    return first.doLayout(event) + second.doLayout(event);
  }

  @Override
  public void setContext(Context context) {
    first.setContext(context);
    second.setContext(context);
    super.setContext(context);
  }

  @Override
  public void start() {
    first.start();
    second.start();
    super.start();
  }
}
