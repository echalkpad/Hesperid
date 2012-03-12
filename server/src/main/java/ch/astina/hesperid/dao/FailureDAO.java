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
package ch.astina.hesperid.dao;

import ch.astina.hesperid.dao.hibernate.FilterGridDataSource;
import ch.astina.hesperid.model.base.*;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public interface FailureDAO
{
    public Failure getFailureById(Long id);

    @CommitAfter
    public void save(Failure failure);

    public void delete(Failure failure);

    public List<Failure> getFailures(FailureStatus status);

    public List<Failure> getFailures(Asset asset);

    public List<Failure> getFailures(Observer observer);

    public List<Failure> getUnresolvedFailures();

    public List<Failure> getUnresolvedFailures(Asset asset);

    public List<Failure> getUnresolvedFailures(Observer observer);

    public FilterGridDataSource getFailureFilterGridDataSource();

	public Failure getLatestUnresolvedFailure(Failure failure);

	public Failure getFailureByExample(Failure failure);

    @CommitAfter
    public void save(FailureEscalation failureEscalation);

    public void delete(FailureEscalation failureEscalation);
}
