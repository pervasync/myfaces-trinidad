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
package org.apache.myfaces.adfinternal.share.nls;

/**
 * The DecimalFormatContext class contains all number format parameters.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/share/nls/DecimalFormatContext.java#0 $) $Date: 10-nov-2005.19:00:01 $
 * @author The Oracle ADF Faces Team
 */
abstract public class DecimalFormatContext implements Cloneable
{
  /**
   * Returns the character used to separate number groups.
   * If zero (NUL), the default separator for the Locale
   * will be used.
   */
  public abstract char getGroupingSeparator();

  /**
   * Returns the character used as a decimal separator.
   * If zero (NUL), the default separator for the Locale
   * will be used.
   */
  public abstract char getDecimalSeparator();


  /**
   * Override of Object.hashCode().
   */
  public int hashCode()
  {
    char grouping = getGroupingSeparator();
    char decimal = getDecimalSeparator();
    
    return grouping ^ (decimal << 8);
  }

  /**
   * Override of Object.equals().
   */
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
        
    if (obj == null)
      return false;

    DecimalFormatContext dfc = (DecimalFormatContext) obj;

    char thisGrouping = getGroupingSeparator();
    char thatGrouping = dfc.getGroupingSeparator();
    if (thisGrouping != thatGrouping)
      return false;

    char thisDecimal = getDecimalSeparator();
    char thatDecimal = dfc.getDecimalSeparator();
    return (thisDecimal == thatDecimal);
  }

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      // should never happen
      throw new IllegalStateException("DecimalFormatContext is not cloneable!");
    }
  }

  /**
   * Override of Object.toString().
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer(super.toString());
    buffer.append('[');

    boolean addComma = false;
    char grouping = getGroupingSeparator();
    if (grouping != 0)
    {
      buffer.append("groupingSeparator=");
      buffer.append(grouping);
      addComma = true;
    }
    char decimal = getDecimalSeparator();
    if (decimal != 0)
    {
      if (addComma)
        buffer.append(',');

      buffer.append("decimalSeparator=");
      buffer.append(decimal);
    }

    buffer.append(']');

    return buffer.toString();
  }

}

