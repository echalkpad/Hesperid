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
package ch.astina.hesperid.agentbundle.scheduler;

import java.io.File;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 133 $, $Date: 2011-09-23 13:43:55 +0200 (Fr, 23 Sep 2011) $
 */
public class MicroclientStopListener implements Job
{
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        String stopFile = (String) jobExecutionContext.getJobDetail().getJobDataMap().get("stopFile");
        File file = new File(stopFile);

        if (file.exists()) {
            file.delete();
            System.exit(1);
        }
    }
}
