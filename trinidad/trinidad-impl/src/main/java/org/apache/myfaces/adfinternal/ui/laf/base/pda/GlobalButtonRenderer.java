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
package org.apache.myfaces.adfinternal.ui.laf.base.pda;

import org.apache.myfaces.adfinternal.ui.RenderingContext;
import org.apache.myfaces.adfinternal.ui.UINode;

import org.apache.myfaces.adfinternal.ui.laf.base.xhtml.LinkRenderer;


/**
 * Renderer for GlobalButton nodes.
 * 
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/pda/GlobalButtonRenderer.java#0 $) $Date: 10-nov-2005.18:54:55 $
 * @author The Oracle ADF Faces Team
 */
public class GlobalButtonRenderer extends LinkRenderer
{
  /**
   * Override to return the correct style depending on the
   * state
   */
  protected Object getStyleClass(
    RenderingContext context,
    UINode           node
    )
  {
    if (isDisabled(context, node))
    {
      return "af|menuButtons::text-disabled";
    }
    else
    {
      if (isSelected(context, node))
      {
        return "af|menuButtons::text-selected";
      }
      else
      {
        return "af|menuButtons::text";
      }
    }
  }
}
