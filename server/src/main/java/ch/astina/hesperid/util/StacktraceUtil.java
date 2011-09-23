////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Astina AG, Zurich
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////////////////////////
package ch.astina.hesperid.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 103 $, $Date: 2011-09-21 15:51:59 +0200 (Mi, 21 Sep 2011) $
 */
public class StacktraceUtil
{
    public static String getStackTrace(Throwable aThrowable)
    {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
}
