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
package org.apache.myfaces.adfinternal.renderkit.core.xhtml;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;

import org.apache.myfaces.adf.bean.FacesBean;
import org.apache.myfaces.adf.bean.PropertyKey;

import org.apache.myfaces.adfinternal.convert.ConverterUtils;

abstract public class ValueRenderer extends XhtmlRenderer
{
  protected ValueRenderer(FacesBean.Type type)
  {
    super(type);
  }

  protected void findTypeConstants(FacesBean.Type type)
  {
    super.findTypeConstants(type);
    _converterKey = type.findKey("converter");
    _valueKey = type.findKey("value");
  }

  protected String getConvertedString(
    FacesContext context,
    UIComponent  component,
    FacesBean    bean)
  {
    Object value = getValue(bean);
    if (value == null)
      return null;

    Converter converter = getConverter(bean);
    if ((converter == null) && !(value instanceof String))
      converter = getDefaultConverter(context, bean);

    if (converter != null)
    {
      return converter.getAsString(context, component, value);
    }

    return value.toString();
  }


  protected Converter getDefaultConverter(
    FacesContext context,
    FacesBean    bean)
  {
    ValueBinding binding = getValueBinding(bean);
    if (binding == null)
      return null;

    Class type = binding.getType(context);
    return ConverterUtils.createConverter(context, type);
  }

  protected Object getValue(FacesBean bean)
  {
    return bean.getProperty(_valueKey);
  }

  /**
   * Returns the ValueBinding for the "value" property.
   */
  protected ValueBinding getValueBinding(FacesBean bean)
  {
    return bean.getValueBinding(_valueKey);
  }

  protected Converter getConverter(FacesBean bean)
  {
    return (Converter) bean.getProperty(_converterKey);
  }

  private PropertyKey _valueKey;
  private PropertyKey _converterKey;
}
