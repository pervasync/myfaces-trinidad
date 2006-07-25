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

package org.apache.myfaces.adfinternal.ui.laf;



import org.apache.myfaces.adfinternal.ui.RenderingContext;

/**
 * NameScoreProxyr delegates to another LookAndFeelScorer instance
 * to provide all scoring except for the name.
 *
 * This class is used to score LookAndFeelExtensions.  LookAndFeelExtensions
 * should inherit scoring from their base LookAndFeel's scorer, with one
 * exception: the LookAndFeelExtension's family name may differ from the
 * base LookAndFeel.  The NameScorer calls the wrapped LookAndFeelScorer
 * to produce a base Score - and then corrects the family name score
 * so that the result will match the LookAndFeelExtension's family name.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/NameOnlyScorer.java#0 $) $Date: 10-nov-2005.18:50:33 $
 * @author The Oracle ADF Faces Team
 */
public class NameOnlyScorer extends LookAndFeelScorer
{
  /**
   */
  public NameOnlyScorer(
    String            requiredLafName,
    LookAndFeelScorer baseScorer
    )
  {
    if (baseScorer == null) 
    {
      throw new NullPointerException("Null baseScorer");
    }

    _lafName           = requiredLafName;
    _baseScorer        = baseScorer;
  }

  /**
   * Produces a Score for the current request, using the
   * base LookAndFeelScorer to score everything other than
   * the look and feel family name.
   */
  public Score scoreLookAndFeel(
    RenderingContext context,
    String lafName
    )
  {
    // Get the base scorer's Score, using a null laf name.
    Score baseScore = _baseScorer.scoreLookAndFeel(context, null);

    // If the laf name is null, then we can just use the base LAF's
    // score as is.
    if (lafName == null)
      return baseScore;

    // Otherwise, we need to override the name score with our own score.
    int nameScore = _scoreName(lafName);

    return new NameScore(nameScore, baseScore);
  }

  // Scores the family name
  private int _scoreName(String lafName)
  {
    if ((lafName == null) || (_lafName == null))
      return DONT_CARE_MATCH;

    return (lafName.equals(_lafName)) ? EXACT_MATCH : NO_MATCH;
  }

  // A ScoreProxy that overrides just the name.
  private static class NameScore extends ScoreProxy
  {
    public NameScore(int nameScore, Score baseScore)
    {
      super(baseScore);
      _nameScore = nameScore;
    }

    public int getNameScore()
    {
      return _nameScore;
    }

    private int _nameScore;
  }

  private String            _lafName;
  private LookAndFeelScorer _baseScorer;
}
