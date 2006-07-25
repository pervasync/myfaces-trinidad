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
 * The DateFormatContext class contains all date format parameters.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/share/nls/DateFormatContext.java#0 $) $Date: 10-nov-2005.19:00:01 $
 * @author The Oracle ADF Faces Team
 */
abstract public class DateFormatContext implements Cloneable
{
  /**
   * Returns the year offset for parsing years with only two digits.
   */
  public abstract int getTwoDigitYearStart();


  /**
   * Override of Object.hashCode().
   */
  public int hashCode()
  {
    int twoDigitYearStart = getTwoDigitYearStart();

    return twoDigitYearStart;
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

    DateFormatContext dfc = (DateFormatContext) obj;

    int thisTwoDigitYearStart = getTwoDigitYearStart();
    int thatTwoDigitYearStart = dfc.getTwoDigitYearStart();

    return (thisTwoDigitYearStart == thatTwoDigitYearStart);
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
      throw new IllegalStateException("DateFormatContext is not cloneable!");
    }
  }

  /**
   * Override of Object.toString().
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer(super.toString());

    buffer.append(", twoDigitYearStart=");
    buffer.append(getTwoDigitYearStart());

    return buffer.toString();
  }

}

