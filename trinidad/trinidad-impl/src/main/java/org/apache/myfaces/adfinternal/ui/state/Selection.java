/*
 * Copyright  2000-2006 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.myfaces.adfinternal.ui.state;

import org.apache.myfaces.adfinternal.ui.RenderingContext;

/**
 * The Selection interface encapsulates access to one or more selected indicies
 * and selected values.  This is used for setting the selection on a UIX
 * component such as an option container.
 *
 * Note: Clients must always subclass BaseSelection rather than implementing
 * this interface directly.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/state/Selection.java#0 $) $Date: 10-nov-2005.18:50:27 $
 * @author The Oracle ADF Faces Team
 */
public interface Selection
{
  /**
   * @param context the rendering context
   * @param value  the value to test, pass null to ignore this parameter
   * @param index  the index to test, pass -1 to ignore this parameter
   */
  public boolean isSelected(
    RenderingContext  context,
    Object            value,
    int               index);
}

