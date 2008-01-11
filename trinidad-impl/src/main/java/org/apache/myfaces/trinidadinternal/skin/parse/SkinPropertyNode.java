/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.myfaces.trinidadinternal.skin.parse;

import org.apache.myfaces.trinidad.logging.TrinidadLogger;
/**
 * Object which represents a single <property> element.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/xml/parse/SkinPropertyNode.java#0 $) $Date: 10-nov-2005.18:50:45 $
 */
public class SkinPropertyNode
{
  /**
   * Creates a PropertyNode
   *
   * @param selector The selector name (with namespace substituted if necessary)
   * @param name The name of the property
   * @param value The value of the property
   */
  public SkinPropertyNode(
    String selector,
    String name,
    String value
    )
  {    
  
    if (name==null)
    {
      throw new NullPointerException(_LOG.getMessage(
        "NULL_NAME"));
    }
    
    if (value==null)
    {
      throw new NullPointerException(_LOG.getMessage(
        "NULL_VALUE"));
    }
    
    _selector = selector;
    _name = name;
    _value = value;
  }

  /**
   * Returns the selector name for this property
   */
  public String getPropertySelector()
  {
    return _selector;
  }

  /**
   * Returns the name of the property that is defined
   * by this PropertyNode.
   */
  public String getPropertyName()
  {
    return _name;
  }

  /**
   * Returns the value of the property that is defined
   * by this PropertyNode.
   */
  public String getPropertyValue()
  {
    return _value;
  }

  private String      _selector;
  private String      _name;
  private String      _value;  
  private static final TrinidadLogger _LOG = TrinidadLogger.createTrinidadLogger(
    SkinPropertyNode.class);
}