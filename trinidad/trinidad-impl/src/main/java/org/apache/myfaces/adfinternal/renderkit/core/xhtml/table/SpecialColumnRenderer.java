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
package org.apache.myfaces.adfinternal.renderkit.core.xhtml.table;

import javax.faces.component.UIComponent;
import org.apache.myfaces.adf.bean.FacesBean;
import org.apache.myfaces.adf.component.core.data.CoreColumn;
import org.apache.myfaces.adfinternal.renderkit.AdfRenderingContext;
import org.apache.myfaces.adfinternal.renderkit.core.xhtml.ColumnRenderer;
import org.apache.myfaces.adfinternal.renderkit.core.xhtml.XhtmlRenderer;

public class SpecialColumnRenderer extends ColumnRenderer
{
  // previously,the special column used to be public static, and we
  // shared it to render the selection, detail and focus columns.
  // However, this resulted in those columns sharing the same client ID
  // as well, which is bad. So each column, now gets its own special column
  // instance:
  private final UIComponent _specialColumn = new CoreColumn();

  public UIComponent getSpecialColumn()
  {
    return _specialColumn;
  }

  protected boolean getHeaderNoWrap(FacesBean bean)
  {
    return true;
  }


  protected boolean getSortable(FacesBean bean)
  {
    return false;
  }


  protected String getSortProperty(FacesBean bean)
  {
    return null;
  }

  protected String getHeaderInlineStyle(AdfRenderingContext arc)
  {
    if (XhtmlRenderer.isIE(arc))
      return "word-break:keep-all"; // bugs 2342291, 1999842
    
    return null;
  }
  
  protected boolean isSpecialColumn()
  {
    return true;
  }  
}
