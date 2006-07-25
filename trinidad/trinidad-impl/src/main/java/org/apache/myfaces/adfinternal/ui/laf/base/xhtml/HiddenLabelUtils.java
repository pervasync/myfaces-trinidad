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
package org.apache.myfaces.adfinternal.ui.laf.base.xhtml;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.adfinternal.agent.AdfFacesAgent;
import org.apache.myfaces.adfinternal.share.config.Configuration;
import org.apache.myfaces.adfinternal.share.config.AccessibilityMode;

import org.apache.myfaces.adfinternal.ui.RenderingContext;
import org.apache.myfaces.adfinternal.ui.UIConstants;

import org.apache.myfaces.adfinternal.ui.laf.base.BaseLafRenderer;

/**
 * Utilities for outputting the hidden labels required for
 * accessibility support.
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/xhtml/HiddenLabelUtils.java#0 $) $Date: 10-nov-2005.18:53:55 $
 * @author The Oracle ADF Faces Team
 */
public class HiddenLabelUtils
{
  /**
   * Identifies whether the current agent supports the hidden label
   * trick.
   */
  static public boolean supportsHiddenLabels(RenderingContext context)
  {
    if (!BaseLafRenderer.supportsID(context))
      return false;

    AdfFacesAgent agent = context.getAgent();
    switch (agent.getAgentApplication())
    {
      case AdfFacesAgent.APPLICATION_IEXPLORER:
        if (agent.getAgentOS() == AdfFacesAgent.OS_WINDOWS)
        {
          // IE 4 doesn't support the label hack.
          if (agent.getAgentMajorVersion() == 4)
            return false;
          
          // JDev VE masquerades as IE Windows, but doesn't support this
          if (agent.getCapability(AdfFacesAgent.CAP_IS_JDEV_VE) != null)
            return false;

          // IE 5 and 6 do.
          return true;
        }

        // IE on the Mac doesn't support the label hack
        return false;

      // Mozilla does support the label hack
      case AdfFacesAgent.APPLICATION_GECKO:
        // Make sure we don't change the VE to Gecko
        assert(agent.getCapability(AdfFacesAgent.CAP_IS_JDEV_VE) == null);
        return true;

      // Assume everyone else doesn't.
      case AdfFacesAgent.APPLICATION_NETSCAPE:
      default:
        return false;
    }
  }

  /**
   * Outputs a hidden label.
   * @param component 
   */
  static public void outputHiddenLabel(
    RenderingContext context,
    String           id,
    Object           text, 
    UIComponent component
    ) throws IOException
  {
    Configuration config = context.getConfiguration();
    if (!AccessibilityMode.isInaccessibleMode(config) &&
        (text != null) &&
        (id   != null))
    {
      if (!id.equals(context.getProperty(UIConstants.MARLIN_NAMESPACE,
                                         _LABEL_KEY)))
      {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("label", component);
        writer.writeAttribute("for", id, null);
        XhtmlLafRenderer.renderStyleClassAttribute(context,
                                                   _HIDDEN_LABEL_CLASS);
        writer.writeText(text, null);
        writer.endElement("label");
      }
    }
  }


  /**
   * Remembers that a "normal" hidden label has already been outputted.
   * Note that this really is distinct from FormRenderer.addLabelMapping();
   * the latter is called whether or not we've actually outputted
   * a <label> tag, since the values are needed for validation.
   */
  static public void rememberLabel(
    RenderingContext context,
    Object           id)
  {
    if (id != null)
    {
      context.setProperty(UIConstants.MARLIN_NAMESPACE,
                          _LABEL_KEY,
                          id.toString());
    }
  }


  private static final String _HIDDEN_LABEL_CLASS = 
    "p_OraHiddenLabel";

  // Key used for storing the "last" label ID
  private static final Object _LABEL_KEY = new Object();

  private HiddenLabelUtils()
  {
  }
}
