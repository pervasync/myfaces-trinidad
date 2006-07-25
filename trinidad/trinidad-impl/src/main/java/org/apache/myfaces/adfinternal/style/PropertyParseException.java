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

package org.apache.myfaces.adfinternal.style;

/**
 * Exception thrown by PropertyParser.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/style/PropertyParseException.java#0 $) $Date: 10-nov-2005.18:57:56 $
 * @author The Oracle ADF Faces Team
 */
public class PropertyParseException extends IllegalArgumentException
{
  /**
   * Creates a PropertyParseException with no message.
   */
  public PropertyParseException()
  {
    super();
  }

  /**
   * Creates a PropertyParseException with a message.
   */
  public PropertyParseException(String message)
  {
    super(message);
  }
}
