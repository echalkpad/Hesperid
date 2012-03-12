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
package ch.astina.hesperid.web.services.failures;

import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.Observer;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public interface FailureService
{
    /**
     * Creates a new failure if this failure has not been reported, yet.
     * If this is a new failure, it is escalated if necessary
     */
    public void report(Failure failure);

    /**
     * Escalates the failure to the next escalation level (if any)
     */
    public void escalate(Failure failure);

    public void acknowledge(Failure failure);

    public void resolve(Failure failure);

    /**
     * Resolve all unresolved failures
     */
    public void autoresolve(Observer observer);
}
