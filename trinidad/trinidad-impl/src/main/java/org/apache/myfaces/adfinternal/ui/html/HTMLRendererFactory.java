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
package org.apache.myfaces.adfinternal.ui.html;

import org.apache.myfaces.adfinternal.ui.Renderer;
import org.apache.myfaces.adfinternal.ui.RendererFactory;
import org.apache.myfaces.adfinternal.ui.RendererFactoryImpl;
import org.apache.myfaces.adfinternal.ui.RendererManager;


/**
 * Renderer factory for raw HTML elements.
 * <p>
 * @see org.apache.myfaces.adfinternal.ui.html.HTMLWebBean
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/html/HTMLRendererFactory.java#0 $) $Date: 10-nov-2005.18:56:25 $
 * @author The Oracle ADF Faces Team
 */
public class HTMLRendererFactory extends RendererFactoryImpl
{
  /**
   * The HTML 4.0 namespace.
   */
  static public final String HTML_NAMESPACE =
     "http://www.w3.org/TR/REC-html40";


  /**
   * Registers the HTML renderer factory on the default
   * renderer manager.
   * <p>
   * @deprecated only the version taking the RendererManager
   *             should be called.
   */ 
  static public void registerSelf()
  {
    registerSelf(RendererManager.getDefaultRendererManager());
  }
  


  /**
   * Registers the HTML renderer factory on a
   * renderer manager.
   */ 
  static public void registerSelf(RendererManager manager)
  {
    manager.registerFactory(HTML_NAMESPACE,
                            getRendererFactory());
  }
    
  
  /**
   * Returns a shared instance of the renderer factory.
   */
  static public RendererFactory getRendererFactory()
  {
    return _sFactory;
  }


  /**
   * Returns the renderer for HTML elements.
   */
  public Renderer getRenderer(String elementName)
  {
    return HTMLElementRenderer.getRenderer();
  }


  // Private constructor
  private HTMLRendererFactory()
  {
  }

  // Shared instance
  static private HTMLRendererFactory _sFactory = new HTMLRendererFactory();
}
