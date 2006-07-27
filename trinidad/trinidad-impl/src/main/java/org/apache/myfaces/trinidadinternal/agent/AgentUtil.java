/*
* Copyright 2006 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.myfaces.trinidadinternal.agent;

import java.util.Map;

import org.apache.myfaces.trinidad.context.Agent;
import org.apache.myfaces.trinidad.context.RequestContext;

import javax.faces.context.FacesContext;

/**
 * agent util class
 */
public class AgentUtil
{
  /**
   * Construct an AdfFacesAgent from agentType, agent application constant, agent version
   * and plaform application constant
   *
   * //TODO: Check: if this is really needed
   *
   * @param type
   * @param browser
   * @param agentVersion
   * @param platform
   * @return
   */
  public static AdfFacesAgent getAgent(int type, int browser, String agentVersion, int platform)
  {
    final int fType = type;
    final int fBrowser = browser;
    final String fAgentVersion = agentVersion;
    final int fPlatform = platform;

    Agent agent =
            new DefaultAgent ()
            {
              public Object getType() {
                return AgentNameUtil.getAgentName(fType);
              }

              public String getAgentName() {
                return AgentNameUtil.getAgentName(fBrowser);
              }

              public String getAgentVersion() {
                return fAgentVersion;
              }

              public String getPlatFormName() {
                return AgentNameUtil.getPlatformName(fPlatform);
              }
            };
    return new AdfFacesAgentImpl(agent);
  }

  /**
   * Get a default agent, that defines no characteristics.
   *
   * @return
   */
  public static AdfFacesAgent getUnknownAgent()
  {
    DefaultAgent adfAgent = new DefaultAgent();
    return new AdfFacesAgentImpl(adfAgent);
  }

  /**
   * Get agent for the current faces context
   *
   * @param context
   * @return
   */
  public static AdfFacesAgent getAgent(FacesContext context)
  {
    Agent agent = RequestContext.getCurrentInstance().getAgent();
    if (agent instanceof AdfFacesAgent)
      return (AdfFacesAgent) agent;

    return new AdfFacesAgentImpl(context, agent);
  }


  /**
   *
   * Merge capabilities provided with the Agent capabilities
   *
   * @param agent  Agent to merge the capabilities with
   * @param capabilities  List (array of {name, value}) capabilities that should be merged
   * @return An Agent with the capabilities merged with the provided agent
   */
   //@TODO: Check this: Why is this called from an LookAndFeel.
   //@Ideally the an Agent's display mode (called facet in uix22), should be
   //@built into the capabilities repository, hence is should not be
   //@look and feel sepecific
  //=-=AEW: I don't believe this is a repository thing:  the concept
  //of output mode is entirely renderkit specific, and the tweaks
  //that are made to the capabilities are also renderkit specific.
  public static AdfFacesAgent mergeCapabilities(AdfFacesAgent agent, Map capabilities)
  {
    if (!(agent instanceof AdfFacesAgentImpl))
      throw new IllegalArgumentException("mergeCapabilities() may only be " +
                                         "used with Agents created by this " +
                                         "class.");
    // Make a copy of the agent first
    agent = (AdfFacesAgent) agent.clone();

    // Then merge in the capabilities
    // =-=AEW This codepath ends up creating copies of the
    // capability map twice, once in clone(), once in __mergeCapabilities()
    ((AdfFacesAgentImpl)agent).__mergeCapabilities(capabilities);

    return agent;
  }

}
