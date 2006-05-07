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
package org.apache.myfaces.adfbuild.plugin.faces.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SourceTemplate
{
  public SourceTemplate(File file)
  {
    _file = file;
  }

  public void substitute(String in, String out)
  {
    _substitutions.put(in, out);
  }

  public Set getImports()
  {
    return Collections.unmodifiableSet(_imports);
  }

  public Set getImplements()
  {
    return Collections.unmodifiableSet(_implements);
  }

  public void readPreface() throws IOException
  {
    _reader = new BufferedReader(new FileReader(_file));
    while (true)
    {
      String line = _reader.readLine();
      if (line == null)
        throw new EOFException("File " + _file + " ended prematurely");

      if (line.equals("{"))
        break;

      if (line.startsWith("import "))
      {
        line = line.trim();
        String imported = line.substring("import ".length(),
                                         line.length() - 1);
        _imports.add(imported);
        _fqcnMap.put(Util.getClassFromFullClass(imported), imported);
      }

      int index = line.indexOf(" implements ");
      if (index != -1)
      {
        String clause = line.substring(index + " implements ".length());
        String[] interfaces = clause.split(", ");
        for (int i=0; i < interfaces.length; i++)
        {
          String className = interfaces[i];
          if (!Util.isFullClass(className))
          {
            String importedName = (String)_fqcnMap.get(className);
            if (importedName != null)
              className = importedName;
          }
          _implements.add(className);
        }
      }
    }
  }


  public void writeContent(Writer out) throws IOException
  {
    while (true)
    {
      String line = _reader.readLine();
      if (line == null)
        throw new EOFException("File " + _file + " ended prematurely");

      if (line.startsWith(_IGNORE_PREFIX))
        continue;

      if (line.equals("}"))
        break;

      line = _substitute(line);
      out.write(line);
      out.write("\n");
    }
  }

  public void close() throws IOException
  {
    _reader.close();
  }

  private String _substitute(String in)
  {
    Iterator keys = _substitutions.keySet().iterator();
    while (keys.hasNext())
    {
      String key = (String) keys.next();
      String value = (String) _substitutions.get(key);
      in = _substitute(in, key, value);
    }

    return in;
  }

  static private String _substitute(String in, String from, String to)
  {
    int index = in.indexOf(from);
    if (index < 0)
      return in;

    StringBuffer buffer = new StringBuffer(in.length() +
                                           to.length() - from.length());
    buffer.append(in.substring(0, index));
    buffer.append(to);
    String suffix = in.substring(index + from.length());
    buffer.append(_substitute(suffix, from, to));

    return buffer.toString();
  }

  private File           _file;
  private BufferedReader _reader;
  private Set            _imports = new HashSet();
  private Map            _fqcnMap = new HashMap();
  private Set            _implements = new HashSet();
  private Map            _substitutions = new HashMap();

  // Magic syntax indicating "please ignore this line"
  static private final String _IGNORE_PREFIX = "/**/";
}
