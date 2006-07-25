/*
 * Copyright  2003-2006 The Apache Software Foundation.
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
package org.apache.myfaces.adf.component;
import org.apache.myfaces.adf.model.RowKeyIndex;

/**
 * Identifies a Paging component.
 * Components that page, have a starting index (see {@link #getFirst})
 * and the number of rows to show on a single page (see {@link #getRows}).
 * @author The Oracle ADF Faces Team
 */
public interface CollectionComponent extends RowKeyIndex
{
  public String getVar();
  public int getRows();
  public int getFirst();
}
