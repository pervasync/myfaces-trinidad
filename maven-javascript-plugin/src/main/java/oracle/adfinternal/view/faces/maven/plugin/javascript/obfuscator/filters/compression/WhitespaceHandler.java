/*
 * Copyright 2006 The Apache Software Foundation.
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
package oracle.adfinternal.view.faces.maven.plugin.javascript.obfuscator.filters.compression;

import oracle.adfinternal.view.faces.maven.plugin.javascript.obfuscator.javascript15parser.AnnotatedToken;


public class WhitespaceHandler implements CompressionHandler
{
  public WhitespaceHandler()
  {
  }

  public void handle(AnnotatedToken token, AnnotatedToken prevToken)
  {
    AnnotatedToken specialToken = token.getSpecialToken();
    AnnotatedToken prevSpecialToken = token;
    boolean skipOne = (prevToken == null) ? false
                                          : (prevToken.isWSSensitive() ||
      token.isInfixWSSensitive());

    while (specialToken != null)
    {
      // remove whitespace. Keep one if the token is whitespace sensitive
      if (specialToken.isWhiteSpace())
      {
        if (skipOne)
        {
          skipOne = false;
          specialToken.image = " ";
          prevSpecialToken = specialToken;
          specialToken = specialToken.getSpecialToken();
        }
        else
        {
          prevSpecialToken.specialToken = specialToken.specialToken;
          specialToken = specialToken.getSpecialToken();
        }
      }
      else
      {
        prevSpecialToken = specialToken;
        specialToken = specialToken.getSpecialToken();
      }
    }
  }
}
