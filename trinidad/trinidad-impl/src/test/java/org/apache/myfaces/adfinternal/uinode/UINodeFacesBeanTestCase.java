/*
 * Copyright 2004,2006 The Apache Software Foundation.
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
package org.apache.myfaces.adfinternal.uinode;


import org.apache.myfaces.adf.bean.FacesBean;
import org.apache.myfaces.adf.bean.FacesBeanFactory;
import org.apache.myfaces.adf.component.UIXComponent;
import org.apache.myfaces.adfbuild.test.FacesTestCase;

/**
 * Base class for JavaServer Faces UINodeFacesBean unit tests.
 *
 * @author John Fallows
 */
public class UINodeFacesBeanTestCase extends FacesTestCase
{
  /**
   * Creates a new UINodeFacesBeanTestCase.
   *
   * @param testName  the unit test name
   */
  public UINodeFacesBeanTestCase(
    String testName)
  {
    super(testName);
  }

  protected void initUINodeFacesBean(
    UINodeFacesBean bean,
    UIXComponent    component,
    FacesBean.Type  type)
  {
    bean.init(component, type);
  }

  protected UINodeFacesBean createUINodeFacesBean(
   UIXComponent component,
   FacesBean.Type type)
  {
    UINodeFacesBean bean = (UINodeFacesBean) 
      FacesBeanFactory.createFacesBean(component.getClass(),
                                       component.getRendererType());
    initUINodeFacesBean(bean, component, type);

    return bean;
  }
  
}
