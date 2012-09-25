/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.trinidad.webapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentELTag;

import javax.servlet.jsp.JspException;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.change.ChangeManager;
import org.apache.myfaces.trinidad.component.UIXComponent;
import org.apache.myfaces.trinidad.component.UIXDocument;
import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.logging.TrinidadLogger;


/**
 * Subclass of UIComponentTag to add convenience methods,
 * and optimize where appropriate.
 */
abstract public class UIXComponentELTag extends UIComponentELTag
{
  public UIXComponentELTag()
  {
  }

  public void setAttributeChangeListener(MethodExpression attributeChangeListener)
  {
    _attributeChangeListener = attributeChangeListener;
  }

  @Override
  public int doStartTag() throws JspException
  {
    ComponentIdSuffixStack suffixStack =
      ComponentIdSuffixStack.getInstance(pageContext);

    _suffixId(suffixStack);

    int retVal = super.doStartTag();

    //pu: There could have been some validation error during property setting
    //  on the bean, this is the closest opportunity to burst out.
    if (_validationError != null)
      throw new JspException(_validationError);

    if (getComponentInstance() instanceof NamingContainer)
    {
      // If a naming container, do not carry component suffixes over from
      // outside of the naming container.
      suffixStack.suspend();
    }

    return retVal;
  }

  @Override
  public int doEndTag() throws JspException
  {
    UIComponent component = getComponentInstance();

    // Apply changes once we have a stable UIComponent subtree is completely
    //  created. End of document tag is a best bet.
    if (component instanceof UIXDocument)
    {
      ChangeManager cm = RequestContext.getCurrentInstance().getChangeManager();
      cm.applyComponentChangesForCurrentView(FacesContext.getCurrentInstance());
    }

    if (getComponentInstance() instanceof NamingContainer)
    {
      ComponentIdSuffixStack suffixStack =
        ComponentIdSuffixStack.getInstance(pageContext);
      suffixStack.resume();
    }

    // In the case where this component has had a suffix appended to it,
    // clear the suffix and revert back to the original ID
    setId(_origId);
    _origId = null;

    // Make iteration tags aware that the processing of this component is now complete so that
    // the tags are able to determine heirarchies.
    TagComponentBridge bridge = TagComponentBridge.getInstance(pageContext);
    bridge.notifyAfterComponentProcessed(getComponentInstance());

    return super.doEndTag();
  }

  @Override
  protected UIComponent findComponent(FacesContext context)
    throws JspException
  {
    // Note that although this method is called "findComponent", it is actually a find or create
    // component. When the super class method is called, it will first look for the component, and
    // if it is not found, it will create one. Therefore, after this super call returns, the
    // component will be non-null
    UIComponent component = super.findComponent(context);

    // Notify any listening tags that this component was found or created. This allows iteration
    // tags to know what components belong to each iteration.
    TagComponentBridge bridge = TagComponentBridge.getInstance(pageContext);
    bridge.notifyComponentProcessed(component);

    return component;
  }

  protected final void setProperties(UIComponent component)
  {
    if (component instanceof UIViewRoot)
    {
      throw new IllegalStateException(
         "<f:view> was not present on this page; tag " + this +
         "encountered without an <f:view> being processed.");
    }

    super.setProperties(component);

    UIXComponent uixComponent = (UIXComponent) component;

    if (_attributeChangeListener != null)
    {
      uixComponent.setAttributeChangeListener(_attributeChangeListener);
    }

    setProperties(uixComponent.getFacesBean());
  }

  protected void setProperty(
    FacesBean   bean,
    PropertyKey key,
    ValueExpression expression)
  {
    if (expression == null)
      return;

    if (expression.isLiteralText())
    {
      bean.setProperty(key, expression.getValue(null));
    }
    else
    {
      bean.setValueExpression(key, expression);
    }
  }

  /**
   * Set a property of type java.lang.String[].  If the value
   * is an EL expression, it will be stored as a ValueExpression.
   * Otherwise, it will parsed as a whitespace-separated series
   * of strings.
   * Null values are ignored.
   */
  protected void setStringArrayProperty(
    FacesBean       bean,
    PropertyKey     key,
    ValueExpression expression)
  {
    if (expression == null)
      return;

    if (expression.isLiteralText())
    {
      bean.setProperty(key, _parseNameTokens(expression.getValue(null)));
    }
    else
    {
      bean.setValueExpression(key, expression);
    }
  }

  /**
   * Set a property of type java.util.List&lt;java.lang.String>.  If the value
   * is an EL expression, it will be stored as a ValueExpression.
   * Otherwise, it will parsed as a whitespace-separated series
   * of strings.
   * Null values are ignored.
   */
  protected void setStringListProperty(
    FacesBean       bean,
    PropertyKey     key,
    ValueExpression expression)
  {
    if (expression == null)
      return;

    if (expression.isLiteralText())
    {
      bean.setProperty(key,
                       _parseNameTokensAsList(expression.getValue(null)));
    }
    else
    {
      bean.setValueExpression(key, expression);
    }
  }

  /**
   * Set a property of type java.util.Set&lt;java.lang.String>.  If the value
   * is an EL expression, it will be stored as a ValueExpression.
   * Otherwise, it will parsed as a whitespace-separated series
   * of strings.
   * Null values are ignored.
   */
  protected void setStringSetProperty(
    FacesBean       bean,
    PropertyKey     key,
    ValueExpression expression)
  {
    if (expression == null)
      return;

    if (expression.isLiteralText())
    {
      bean.setProperty(key,
                       _parseNameTokensAsSet(expression.getValue(null)));
    }
    else
    {
      bean.setValueExpression(key, expression);
    }
  }

  /**
   * Set a property of type java.lang.Number.  If the value
   * is an EL expression, it will be stored as a ValueBinding.
   * Otherwise, it will parsed with Integer.valueOf() or Double.valueOf() .
   * Null values are ignored.
   */
  protected void setNumberProperty(
    FacesBean   bean,
    PropertyKey key,
    ValueExpression expression)
  {
    if (expression == null)
      return;

    if (expression.isLiteralText())
    {
      Object value = expression.getValue(null);
      if (value != null)
      {
        if (value instanceof Number)
        {
          bean.setProperty(key, value);
        }
        else
        {
          String valueStr = value.toString();
          if(valueStr.indexOf('.') == -1)
            bean.setProperty(key, Integer.valueOf(valueStr));
          else
            bean.setProperty(key, Double.valueOf(valueStr));
        }
      }
    }
    else
    {
      bean.setValueExpression(key, expression);
    }
  }

  /**
   * Set a property of type int[].  If the value
   * is an EL expression, it will be stored as a ValueExpression.
   * Otherwise, it will parsed as a whitespace-separated series
   * of ints.
   * Null values are ignored.
   */
  protected void setIntArrayProperty(
    FacesBean   bean,
    PropertyKey key,
    ValueExpression expression)
  {
    if (expression == null)
      return;

    if (expression.isLiteralText())
    {
      Object value = expression.getValue(null);
      if (value != null)
      {
        String[] strings = _parseNameTokens(value);
        final int[] ints;
        if (strings != null)
        {
          try
          {
            ints = new int[strings.length];
            for(int i=0; i<strings.length; i++)
            {
              int j = Integer.parseInt(strings[i]);
              ints[i] = j;
            }
          }
          catch (NumberFormatException e)
          {
            _LOG.severe("CANNOT_CONVERT_INTO_INT_ARRAY",value);
            _LOG.severe(e);
            return;
          }
        }
      }
    }
    else
    {
      bean.setValueExpression(key, expression);
    }
  }

  /**
   * Set a property of type java.util.Date.  If the value
   * is an EL expression, it will be stored as a ValueExpression.
   * Otherwise, it will parsed as an ISO 8601 date (yyyy-MM-dd).
   * Null values are ignored.
   */
  protected void setDateProperty(
    FacesBean   bean,
    PropertyKey key,
    ValueExpression expression)
  {
    if (expression == null)
      return;

    if (expression.isLiteralText())
    {
      bean.setProperty(key, _parseISODate(expression.getValue(null)));
    }
    else
    {
      bean.setValueExpression(key, expression);
    }
  }

    /**
   * Set a property of type java.util.Date.  If the value
   * is an EL expression, it will be stored as a ValueBinding.
   * Otherwise, it will parsed as an ISO 8601 date (yyyy-MM-dd)
   * and the time components (hour, min, second, millisecond) maximized.
   * Null values are ignored.
   */
    protected void setMaxDateProperty(
    FacesBean   bean,
    PropertyKey key,
    ValueExpression expression)
  {
    if (expression == null)
      return;

    if (expression.isLiteralText())
    {
      Date d = _parseISODate(expression.getValue(null));
      Calendar c = Calendar.getInstance();
      TimeZone tz = RequestContext.getCurrentInstance().getTimeZone();
      if (tz != null)
        c.setTimeZone(tz);
      c.setTime(d);
      // Original value had 00:00:00 for hours,mins, seconds now maximize those
      // to get the latest time value for the date supplied.
      c.set (Calendar.HOUR_OF_DAY, 23);
      c.set (Calendar.MINUTE, 59);
      c.set (Calendar.SECOND, 59);
      c.set (Calendar.MILLISECOND, 999);
      bean.setProperty(key, c.getTime());
    }
    else
    {
      bean.setValueExpression(key, expression);
    }
  }

  protected void setProperties(FacesBean bean)
  {
    // Could be abstract, but it's easier to *always* call super.setProperties(),
    // and perhaps we'll have something generic in here, esp. if we take
    // over "rendered" from UIComponentTag
  }

  /**
   * Sets any fatal validation error that could have happened during property
   *  setting. If this is set, tag execution aborts with a JspException at the
   *  end of doStartTag().
   * @param validationError
   */
  protected void setValidationError(String validationError)
  {
    _validationError = validationError;
  }

  /**
   * Parse a string into a java.util.Date object.  The
   * string must be in ISO 9601 format (yyyy-MM-dd).
   */
  static private final Date _parseISODate(Object o)
  {
    if (o == null)
      return null;

    String stringValue = o.toString();
    try
    {
      return _getDateFormat().parse(stringValue);
    }
    catch (ParseException pe)
    {
      _LOG.info("CANNOT_PARSE_VALUE_INTO_DATE", stringValue);
      return null;
    }
  }

  /**
   * Parses a whitespace separated series of name tokens.
   * @param stringValue the full string
   * @return an array of each constituent value, or null
   *  if there are no tokens (that is, the string is empty or
   *  all whitespace)
   * @todo Move to utility function somewhere (ADF Share?)
   */
  static private final String[] _parseNameTokens(Object o)
  {
    List<String> list = _parseNameTokensAsList (o);

    if (list == null)
      return null;

    return list.toArray(new String[list.size()]);
  }

  static private final List<String> _parseNameTokensAsList (Object o)
  {
    if (o == null)
      return null;

    String stringValue = o.toString();
    ArrayList<String> list = new ArrayList<String>(5);

    int     length = stringValue.length();
    boolean inSpace = true;
    int     start = 0;
    for (int i = 0; i < length; i++)
    {
      char ch = stringValue.charAt(i);

      // We're in whitespace;  if we've just departed
      // a run of non-whitespace, append a string.
      // Now, why do we use the supposedly deprecated "Character.isSpace()"
      // function instead of "isWhitespace"?  We're following XML rules
      // here for the meaning of whitespace, which specifically
      // EXCLUDES general Unicode spaces.
      if (Character.isWhitespace(ch))
      {
        if (!inSpace)
        {
          list.add(stringValue.substring(start, i));
          inSpace = true;
        }
      }
      // We're out of whitespace;  if we've just departed
      // a run of whitespace, start keeping track of this string
      else
      {
        if (inSpace)
        {
          start = i;
          inSpace = false;
        }
      }
    }

    if (!inSpace)
      list.add(stringValue.substring(start));

    if (list.isEmpty())
      return null;

    return list;
  }

  static private final Set<String> _parseNameTokensAsSet (Object o)
  {
    List<String> list = _parseNameTokensAsList(o);

    if (list == null)
      return null;
    else
      return new HashSet<String>(list);
  }

  private void _suffixId(
    ComponentIdSuffixStack suffixStack)
  {
    // Check to see if this component needs to have its ID suffixed.
    // This will happen when the component is inside of a suffix
    // supporting tag like the for each tag. This will allow iterating
    // components to define how unique IDs will be generated
    // for components without relying on the UIComponentClassicTagBase
    // code, which in the Mojarra implementation of JSF appends "j_id_#"
    // to each component beyond the first, but is not able to be used from
    // code in a supported fashion.
    String currentSuffix = suffixStack.getSuffix();
    if (currentSuffix != null)
    {
      _origId = getId();
      if (_origId == null)
      {
        // If the original ID is null, then we cannot suffix the ID correctly. We also cannot
        // rely on the component ID generation of the UIComponentClassicTagBase is it prevents
        // anyone from assigning an ID with the autogenerated prefix. As a result, we have to
        // generate our own unique ID ourselves.
        // This will ensure that component state will stick with Trinidad components with
        // generated IDs if components are reordered.
        Map<String, Object> viewAttrs = FacesContext.getCurrentInstance().getViewRoot()
          .getAttributes();
        Integer lastUniqueId = (Integer)viewAttrs.get(_UNIQUE_ID_KEY);
        if (lastUniqueId == null)
        {
          lastUniqueId = 0;
        }
        else
        {
          ++lastUniqueId;
        }

        _origId = "tr_" + lastUniqueId;
        viewAttrs.put(_UNIQUE_ID_KEY, lastUniqueId);

        _origIdGenerated = true;
      }

      setId(_origId + currentSuffix);
    }
  }

  private static final TrinidadLogger _LOG =
    TrinidadLogger.createTrinidadLogger(UIXComponentELTag.class);

  // We rely strictly on ISO 8601 formats
  private static DateFormat _getDateFormat()
  {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    TimeZone tz = RequestContext.getCurrentInstance().getTimeZone();
    if (tz != null)
      sdf.setTimeZone(tz);
    return sdf;
  }

  /** @deprecated Not used any more in the session state manager */
  @Deprecated
  public static final String DOCUMENT_CREATED_KEY = "org.apache.myfaces.trinidad.DOCUMENTCREATED";

  private final static String _UNIQUE_ID_KEY = UIXComponentELTag.class.getName() + ".ID";

  private MethodExpression  _attributeChangeListener;
  private String            _validationError;
  private String            _origId;
  private boolean           _origIdGenerated = false;
}
