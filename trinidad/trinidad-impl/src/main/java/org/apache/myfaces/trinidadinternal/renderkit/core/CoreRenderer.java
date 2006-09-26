/*
 * Copyright  2005,2006 The Apache Software Foundation.
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
package org.apache.myfaces.trinidadinternal.renderkit.core;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.component.UIXComponent;
import org.apache.myfaces.trinidad.context.Agent;
import org.apache.myfaces.trinidad.context.RequestContext;

import org.apache.myfaces.trinidad.context.RenderingContext;
import org.apache.myfaces.trinidadinternal.renderkit.RenderUtils;
import org.apache.myfaces.trinidadinternal.webapp.TrinidadFilterImpl;

/**
 * Basic implementation of the core rendering functionality
 * across render kits.
 */
public class CoreRenderer extends Renderer
{
  /**
   * @todo Move elsewhere?
   */
  static public final char CHAR_UNDEFINED = (char) -1;  
  static public final int NO_CHILD_INDEX = -1;    

  protected CoreRenderer()
  {
  }


  //
  // COERCION HELPERS
  //

  /**
   * Coerces an object into a String.
   */
  static public String toString(Object o)
  {
    if (o == null)
      return null;
    return o.toString();
  }


  /**
   * Coerces an object into a URI, accounting for JSF rules
   * with initial slashes.
   */
  static public String toUri(Object o)
  {
    if (o == null)
      return null;
    
    String uri = o.toString();
    if (uri.startsWith("/"))
    {
      // Treat two slashes as server-relative
      if (uri.startsWith("//"))
      {
        uri = uri.substring(1);
      }
      else
      {
        FacesContext fContext = FacesContext.getCurrentInstance();
        uri = fContext.getExternalContext().getRequestContextPath() + uri;
      }
    }

    return uri;
  }


  /**
   * Returns the integer value of an object;  this does 
   * not support null (which must be substituted with a default
   * before calling).
   */
  static public int toInt(Object o)
  {
    return ((Number) o).intValue();
  }



  /**
   * Returns the integer value of an object;  this does 
   * not support null (which must be substituted with a default
   * before calling).
   */
  static public long toLong(Object o)
  {
    return ((Number) o).longValue();
  }

  /**
   * Returns the character value of an object, XhtmlConstants.CHAR_UNDEFINED
   * if there is none.
   */
  static public char toChar(Object o)
  {
    if (o == null)
      return CHAR_UNDEFINED;

    if (o instanceof Character)
      return ((Character) o).charValue();

    // If it's not a Character object, then let's turn it into
    // a CharSequence and grab the first character
    CharSequence cs;
    if (o instanceof CharSequence)
    {
      cs = (CharSequence) o;
    }
    else
    {
      cs = o.toString();
    }
    
    if (cs.length() == 0)
      return CHAR_UNDEFINED;

    return cs.charAt(0);
  }


  @Override
  public final void encodeBegin(FacesContext context,
                          UIComponent component) throws IOException
  {
    if (!getRendersChildren())
    {
      RenderingContext arc = RenderingContext.getCurrentInstance();
      if (arc == null)
        throw new IllegalStateException("No RenderingContext");
      
      FacesBean bean = getFacesBean(component);
      encodeBegin(context, arc, component, bean);
    }
  }

  @Override
  public final void encodeChildren(FacesContext context, UIComponent component)
  {
    // encodeChildren() is fairly useless - it's simpler to just
    // put the output in encodeEnd(), or use the encodeAll() hook
  }

  @Override
  public final void encodeEnd(FacesContext context,
                        UIComponent component) throws IOException
  {
    RenderingContext arc = RenderingContext.getCurrentInstance();
    if (arc == null)
      throw new IllegalStateException("No RenderingContext");

    FacesBean bean = getFacesBean(component);
    if (getRendersChildren())
    {
      encodeAll(context, arc, component, bean);
    }
    else
    {
      encodeEnd(context, arc, component, bean);
    }
  }

  /**
   * Hook for rendering the start of a component;  only 
   * called if getRendersChildren() is <em>false</em>.
   */
  protected void encodeBegin(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    if (getRendersChildren())
      throw new IllegalStateException();
  }

  /**
   * Hook for rendering the end of a component;  only 
   * called if getRendersChildren() is <em>false</em>.
   */
  protected void encodeEnd(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    if (getRendersChildren())
      throw new IllegalStateException();
  }


  /**
   * Hook for rendering all of a component;  only 
   * called if getRendersChildren() is <em>true</em>.
   */
  protected void encodeAll(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    if (!getRendersChildren())
      throw new IllegalStateException();
  }

  /**
   * Hook for encoding a child;  this assumes that isRendered()
   * has already been called. (RenderUtils.encodeRecursive()
   * can be used if you don't need that check.)
   * =-=AEW Ugh.
   */
  @SuppressWarnings("unchecked")
  protected void encodeChild(
    FacesContext context,
    UIComponent  child) throws IOException
  {
    assert(child.isRendered());
    child.encodeBegin(context);
    if (child.getRendersChildren())
    {
      child.encodeChildren(context);
    }
    else
    {
      if (child.getChildCount() > 0)
      {
        for(UIComponent subChild : (List<UIComponent>)child.getChildren())
        {
          RenderUtils.encodeRecursive(context, subChild);
        }
      }
    }
    
    child.encodeEnd(context);
  }


  @SuppressWarnings("unchecked")
  protected final void encodeAllChildren(
    FacesContext context,
    UIComponent  component) throws IOException
  {
    int  childCount = component.getChildCount();
    if (childCount == 0)
      return;
    
    for(UIComponent child : (List<UIComponent>)component.getChildren())
    {
      if (child.isRendered())
      {
        encodeChild(context, child);
      }
    }
  }

  /**
   * @todo Fix reference to CoreRenderer
   */
  protected void delegateRenderer(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         component,
    FacesBean           bean,
    CoreRenderer        renderer) throws IOException
  {
    if (renderer.getRendersChildren())
    {
      renderer.encodeAll(context, arc, component, bean);
    }
    else
    {
      throw new IllegalStateException();
    }
  }
    
  /**
   * @todo Fix reference to CoreRenderer
   */
  protected void delegateRendererBegin(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         component,
    FacesBean           bean,
    CoreRenderer        renderer) throws IOException
  {
    if (renderer.getRendersChildren())
    {
      throw new IllegalStateException();
    }
    else
    {
      renderer.encodeBegin(context, arc, component, bean);
    }
  }

  /**
   * @todo Fix reference to CoreRenderer
   */
  protected void delegateRendererEnd(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         component,
    FacesBean           bean,
    CoreRenderer        renderer) throws IOException
  {
    if (renderer.getRendersChildren())
    {
      throw new IllegalStateException();
    }
    else
    {
      renderer.encodeEnd(context, arc, component, bean);
    }
  }

  /**
   * Renders the client ID as an "id".
   */
  protected void renderId(
    FacesContext context,
    UIComponent  component) throws IOException
  {
    if (shouldRenderId(context, component))
    {
      String clientId = getClientId(context, component);
      context.getResponseWriter().writeAttribute("id", clientId, "id");
    }
  }

  /**
   * Returns the client ID that should be used for rendering (if
   * {@link #shouldRenderId} returns true).
   */
  protected String getClientId(
    FacesContext context,
    UIComponent  component)
  {
    return component.getClientId(context);
  }

  /**
   * Returns true if the component should render an ID.  Components
   * that deliver events should always return "true".
   * @todo Is this a bottleneck?  If so, optimize!
   */
  protected boolean shouldRenderId(
    FacesContext context,
    UIComponent  component)
  {
    String id = component.getId();

    // Otherwise, if ID isn't set, don't bother
    if (id == null)
      return false;
    
    // ... or if the ID was generated, don't bother
    if (id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX))
      return false;

    return true;
  }




  protected boolean skipDecode(FacesContext context)
  {
    // =-=AEW HACK!  When executing a "dialog return" from the filter,
    // we've generally saved off the original parameters such that
    // decoding again isn't a problem.  But we can run into some problems:
    //  (1) A component that doesn't know about ReturnEvents:  it'll
    //    decode again, thereby firing the event again that launched
    //    the dialog (and you go right back to the dialog)
    //  (2) The component does know about ReturnEvents, but
    //      someone launches a dialog in response to the ReturnEvent,
    //      after setting the value of an input field.  But since
    //      we've still saved off the original parameters,
    //      now we're back in
    // The best fix would really be somehow skipping the Apply Request
    // Values phase altogether, while still queueing the ReturnEvent
    // properly.  But how the heck is that gonna happen?
    return TrinidadFilterImpl.isExecutingDialogReturn(context);
  }

  protected FacesBean getFacesBean(UIComponent component)
  {
    return ((UIXComponent) component).getFacesBean();
  }

  static protected final Object getRenderingProperty(
    RenderingContext arc,
    Object              key)
  {
    return arc.getProperties().get(key);
  }

  static protected final Object setRenderingProperty(
    RenderingContext arc,
    Object              key,
    Object              value)
  {
    return arc.getProperties().put(key, value);
  }

  /**
   * Gets a facet, verifying that the facet should be rendered.
   */
  static public UIComponent getFacet(
    UIComponent component,
    String      name)
  {
    UIComponent facet = component.getFacet(name);
    if ((facet == null) || !facet.isRendered())
      return null;

    return facet;
  }
  
  /**
   * Returns true if the component has children and at least
   * one has rendered=="true".
   */
  @SuppressWarnings("unchecked")
  static public boolean hasRenderedChildren(UIComponent component)
  {
    int count = component.getChildCount();
    if (count == 0)
      return false;
      
    for(UIComponent child : (List<UIComponent>)component.getChildren())
    {
      if (child.isRendered())
      {
        return true;
      }
    }
    
    return false;
  }

  /**
   * Returns the total number of children with rendered=="true".
   */
  @SuppressWarnings("unchecked")
  static public int getRenderedChildCount(UIComponent component)
  {
    int count = component.getChildCount();
    if (count == 0)
      return 0;
      
    int total = 0;
    for(UIComponent child : (List<UIComponent>)component.getChildren())
    {
      if (child.isRendered())
      {
        total++;
      }
    }
    
    return total;
  }


 /**
   * @param afterChildIndex The children coming after this index, will
   * be considered. 
   * @return the index of the next child that must be rendered, or
   * {@link #NO_CHILD_INDEX} if there is none.
   */
  public static int getNextRenderedChildIndex(
    List<UIComponent> components,
    int  afterChildIndex
    )
  {
    int childIndex = afterChildIndex + 1;
    Iterator<UIComponent> iter = components.listIterator(childIndex);
    for(; iter.hasNext(); childIndex++)
    {
      if(iter.next().isRendered())
      {
        return childIndex;
      }
    }

    return NO_CHILD_INDEX;
  }


  //
  // AGENT CAPABILITY CONVENIENCE METHODS
  //

  static public boolean isDesktop(RenderingContext arc)
  {
    return (arc.getAgent().getType().equals(Agent.TYPE_DESKTOP));
  }

  static public boolean isPDA(RenderingContext arc)
  {
    return (arc.getAgent().getType().equals(Agent.TYPE_PDA));
  }

  static public boolean isIE(RenderingContext arc)
  {
    return (arc.getAgent().getAgentName().equals(Agent.AGENT_IE));
  }

  static public boolean isGecko(RenderingContext arc)
  {
    return (arc.getAgent().getAgentName().equals(Agent.AGENT_GECKO));
  }

  static public boolean isInaccessibleMode(RenderingContext arc)
  {
    return (arc.getAccessibilityMode() ==
            RequestContext.Accessibility.INACCESSIBLE);
  }

  static public boolean isScreenReaderMode(RenderingContext arc)
  {
    return (arc.getAccessibilityMode() ==
            RequestContext.Accessibility.SCREEN_READER);
  }

  //
  // Rendering convenience methods.
  //

  protected void renderEncodedActionURI(
   FacesContext context,
   String       name,
   Object       value) throws IOException
  {
    if (value != null)
    {
      value = context.getExternalContext().encodeActionURL(value.toString());
      context.getResponseWriter().writeURIAttribute(name, value, null);
    }
  }

  protected void renderEncodedResourceURI(
   FacesContext context,
   String       name,
   Object       value) throws IOException
  {
    if (value != null)
    {
      value = context.getExternalContext().encodeResourceURL(value.toString());
      context.getResponseWriter().writeURIAttribute(name, value, null);
    }
  }



  /**
   * Render a generic CSS styleClass (not one derived from an attribute).
   * The styleclass will be passed through the RenderingContext
   * getStyleClass() API.
   * @param context  the FacesContext
   * @param styleClass the style class
   */
  static public void renderStyleClass(
    FacesContext        context,
    RenderingContext arc,
    String              styleClass) throws IOException
  {
    if (styleClass != null)
    {
      styleClass = arc.getStyleClass(styleClass);
      context.getResponseWriter().writeAttribute("class", styleClass, null);
    }
  }

  /**
   * Render an array of CSS styleClasses as space-separated values.
   * NOTE: the array is mutated during this method, and cannot
   * be reused!  Each styleclass will be passed through the RenderingContext
   * getStyleClass() API.
   * @param context  the FacesContext
   * @param styleClass the style class
   */
  static public void renderStyleClasses(
    FacesContext        context,
    RenderingContext arc,
    String[]            styleClasses) throws IOException
  {
    int length = 0;
    for (int i = 0; i < styleClasses.length; i++)
    {
      if (styleClasses[i] != null)
      {
        String styleClass = arc.getStyleClass(styleClasses[i]);
        length += styleClass.length() + 1;
        styleClasses[i] = styleClass;
      }
    }

    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < styleClasses.length; i++)
    {
      if (styleClasses[i] != null)
      {
        if (builder.length() != 0)
          builder.append(' ');
        builder.append(styleClasses[i]);
      }
    }

    context.getResponseWriter().writeAttribute("class", builder.toString(), null);
  }
}
