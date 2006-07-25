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

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.FacesEvent;

import org.apache.myfaces.adf.logging.ADFLogger;

import org.apache.myfaces.adf.bean.FacesBean;
import org.apache.myfaces.adf.component.UIXCollection;
import org.apache.myfaces.adf.component.UIXTable;
import org.apache.myfaces.adf.component.UIXTree;
import org.apache.myfaces.adf.event.SelectionEvent;
import org.apache.myfaces.adf.model.RowKeySet;

import org.apache.myfaces.adfinternal.renderkit.AdfRenderingContext;
import org.apache.myfaces.adfinternal.renderkit.core.CoreRenderer;
import org.apache.myfaces.adfinternal.renderkit.core.xhtml.OutputUtils;

public class TableSelectManyRenderer extends TableSelectOneRenderer
{
  public static final String UNSELECTED_KEY = "_us";
  public static final String SELECTED_KEY = "_s";
  public static final String SELECTED_MODE_KEY = "_sm";

  public TableSelectManyRenderer()
  {
  }

  //
  // Decode
  //
  public void decode(FacesContext context, UIComponent component)
  {
    UIXCollection table = (UIXCollection) component;
    Object oldKey = table.getRowKey();

    table.setRowIndex(-1);
    String tableId = table.getClientId(context);

    Map parameters =  context.getExternalContext().getRequestParameterValuesMap();

    String[] unselectedBoxes = (String[])
      parameters.get(tableId+NamingContainer.SEPARATOR_CHAR+UNSELECTED_KEY);

    // check to see if there were any selection boxes in the request.
    // if there were no unselected boxes, then there can't be any selected
    // ones either:
    if ((unselectedBoxes == null) || (unselectedBoxes.length == 0))
      return;

    String[] selectedBoxes = (String[])
      parameters.get(tableId+NamingContainer.SEPARATOR_CHAR+SELECTED_KEY);

    // must work with both table and hgrid:
    final RowKeySet selectionModel;
    if (table instanceof UIXTable)
      selectionModel = ((UIXTable) table).getSelectedRowKeys();
    else
      selectionModel = ((UIXTree) table).getSelectedRowKeys();


//    Object selectMode = tableMap.get(UIConstants.SELECT_MODE_KEY);
//    // select-all/none across all ranges (both visible and invisible)
//    // is disabled for EA6. This is because it is pretty bad if in the email
//    // demo the user does a select-all followed by delete and all of his/her
//    // messages (including the ones that are currently not visible) are deleted.
//    if (false && (selectMode != null))
//    {
//      if ("all".equals(selectMode))
//        selectionModel.addAll();
//      else if ("none".equals(selectMode))
//        selectionModel.clear();
//
//      // even if we do a select all/none we still need to run through the
//      // regular per row selection code below. this is because the user might
//      // have clicked select-all and then deselected some rows before submitting:
//    }

    RowKeySet selectedDelta = selectionModel.clone();
    selectedDelta.clear();
    RowKeySet unselectedDelta = selectedDelta.clone();

    _setDeltas(table, selectedBoxes, unselectedBoxes,
               selectionModel, selectedDelta, unselectedDelta);
    if ((selectedDelta.getSize() != 0) || (unselectedDelta.getSize() != 0))
    {
      FacesEvent event =
        new SelectionEvent(table, unselectedDelta, selectedDelta);
      event.queue();
    }
    table.setRowKey(oldKey);
  }

  private void _setDeltas(UIXCollection table,
                          String[] selectedBoxes, String[] unselectedBoxes,
                          RowKeySet current,
                          RowKeySet selectedDelta,
                          RowKeySet unselectedDelta)
  {
    Map deltas = new HashMap(unselectedBoxes.length);
    for(int i=0; i< unselectedBoxes.length; i++)
    {
      String currencyStr = unselectedBoxes[i];
      deltas.put(currencyStr, Boolean.FALSE);
    }

    if (selectedBoxes != null)
    {
      for(int i=0; i < selectedBoxes.length; i++)
      {
        String currencyStr = selectedBoxes[i];
        deltas.put(currencyStr, Boolean.TRUE);
      }
    }

    for(Iterator entries=deltas.entrySet().iterator(); entries.hasNext();)
    {
      Map.Entry entry = (Map.Entry) entries.next();
      String currencyStr = (String) entry.getKey();
      boolean select = (Boolean.TRUE == entry.getValue());
      table.setCurrencyString(currencyStr);

      // TODO: do not mutate the component's selectedRowKeys here.
      // instead mutate when the SelectionEvent is broadcast:
      if (select)
      {
        if  (current.add())
        {
          // the server-side state changed. Therefore, this must have been
          // previously unselected. so add it to the newly selected set:
          selectedDelta.add();
        }
      }
      else if (current.remove())
      {
        // the server-side state changed. Therefore, this must have been
        // previously selected. so add it to the newly unselected set:
        unselectedDelta.add();
      }
    }
  }

  protected CoreRenderer createCellRenderer(FacesBean.Type type)
  {
    return new Checkbox(type);
  }

  protected boolean isSelectOne()
  {
    return false;
  }

  public static void renderScripts(
    FacesContext        context,
    AdfRenderingContext arc,
    TableRenderingContext trc) throws IOException
  {
    if (arc.getProperties().put(_JS_RENDERED_KEY, Boolean.TRUE) == null)
    {
      ResponseWriter writer = context.getResponseWriter();
      writer.startElement("script", null);
      renderScriptDeferAttribute(context, arc);
      renderScriptTypeAttribute(context, arc);

      String jsCall =
      TreeUtils.setupJSMultiSelectCollectionComponent(
        SELECTED_KEY, SELECTED_MODE_KEY, false);
      writer.writeText(jsCall, null);
      writer.writeText(";", null);
      writer.endElement("script");
    }

    // This needs to happen once for every table (see bug 4542030)
    String selectedModeName = trc.getTableId() + ":" + SELECTED_MODE_KEY;

    // render hidden field for select mode. this field lets the server
    // know that the user has selected "all" or "none", so that it can
    // use that when repopulating the page or another record set.
    if (arc.getFormData() != null)
      arc.getFormData().addNeededValue(selectedModeName);
  }


  protected void renderCellContent(
    FacesContext          context,
    AdfRenderingContext   arc,
    TableRenderingContext tContext,
    UIComponent           component,
    FacesBean             bean) throws IOException
  {
    super.renderCellContent(context, arc, tContext, component, bean);
    _renderUnsuccessfulField(context, tContext);
  }


  /**
   * @todo Eliminate the need for this "unsuccessful" field
   */
  private void _renderUnsuccessfulField(
    FacesContext          context,
    TableRenderingContext tContext) throws IOException
  {
    String unsuccessfulId = (tContext.getTableId() +
                             NamingContainer.SEPARATOR_CHAR +
                             UNSELECTED_KEY);
    String value = ((UIXCollection) tContext.getCollectionComponent()).
                getCurrencyString();

    OutputUtils.renderHiddenField(context,
                                  unsuccessfulId,
                                  value);
  }




  static private class Checkbox extends TableSelectOneRenderer.Radio
  {
    public Checkbox(FacesBean.Type type)
    {
      super(type);
    }

    protected void renderId(
      FacesContext context,
      UIComponent  component) throws IOException
    {
      TableRenderingContext tContext =
        TableRenderingContext.getCurrentInstance();
      String param = (tContext.getTableId() +
                      NamingContainer.SEPARATOR_CHAR +
                      SELECTED_KEY);
      ResponseWriter writer = context.getResponseWriter();
      writer.writeAttribute("name", param, null);

      // =-=AEW Inefficient.  We only need the "id" when there's
      // a shortDescription (which is when we'll get a label)
      if (getShortDesc(getFacesBean(component)) != null)
        writer.writeAttribute("id", getClientId(context, component), null);

    }


    protected Object getType()
    {
      return "checkbox";
    }


    protected String getDefaultShortDescKey()
    {
      return "af_tableSelectMany.SELECT_COLUMN_HEADER";
    }
  }

  private static final Object _JS_RENDERED_KEY = new Object();
  private static final ADFLogger _LOG =
    ADFLogger.createADFLogger(TableSelectManyRenderer.class);
}
