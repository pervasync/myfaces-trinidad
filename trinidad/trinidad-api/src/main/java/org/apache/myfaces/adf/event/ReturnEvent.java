/*
 * Copyright  2004-2006 The Apache Software Foundation.
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

package org.apache.myfaces.adf.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;

import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;


/**
 * Event delivered when a dialog has returned.
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-api/src/main/java/oracle/adf/view/faces/event/ReturnEvent.java#0 $) $Date: 10-nov-2005.19:09:06 $
 * @see org.apache.myfaces.adf.component.DialogSource
 * @see org.apache.myfaces.adf.component.UIXCommand
 * @see org.apache.myfaces.adf.event.ReturnListener
 * @author The Oracle ADF Faces Team
 */
public class ReturnEvent extends FacesEvent
{
  public ReturnEvent(UIComponent source, Object returnValue)
  {
    this(source, returnValue, null);
  }

  public ReturnEvent(UIComponent source, Object returnValue, Map returnParams)
  {
    super(source);
    _returnValue = returnValue;
    _returnParams = new HashMap();
    if (returnParams == null)
      _returnParams = Collections.EMPTY_MAP;
    else
      _returnParams = new HashMap(returnParams);
  }


  public Object getReturnValue()
  {
    return _returnValue;
  }

  public Map getReturnParameters()
  {
    return Collections.unmodifiableMap(_returnParams);
  }

  public void processListener(FacesListener listener)
  {
    ((ReturnListener) listener).processReturn(this);
  }

  public boolean isAppropriateListener(FacesListener listener)
  {
    return (listener instanceof ReturnListener);
  }


  public int hashCode()
  {
    return ((getComponent() == null) ? 0 : getComponent().hashCode()) +
           (37 * (((_returnValue == null) ? 0 : _returnValue.hashCode()) +
            37 * ((_returnParams == null) ? 0 : _returnParams.hashCode())));
  }

  public boolean equals(
   Object o)
  {
    if (o == this)
      return true;

    if (o instanceof ReturnEvent)
    {
      ReturnEvent that = (ReturnEvent)o;
      if (!this.getComponent().equals(that.getComponent()))
        return false;

      if (_returnValue == null)
      {
        if (that._returnValue != null)
          return false;
      }
      else
      {
        if (!_returnValue.equals(that._returnValue))
          return false;
      }

      if (_returnParams == null)
      {
        if (that._returnParams != null)
          return false;
      }
      else
      {
        if (!_returnParams.equals(that._returnParams))
          return false;
      }

      return true;
    }

    return false;
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(getClass().getName());
    sb.append("[component=");
    sb.append(getComponent());
    sb.append(", returnValue=");
    sb.append(getReturnValue());
    if ((_returnParams != null) && 
        !_returnParams.isEmpty())
    {
      sb.append(", returnParams=");
      sb.append(_returnParams);
    }

    sb.append(']');
    return sb.toString();
  }

  private Object _returnValue;
  private Map    _returnParams;
}

