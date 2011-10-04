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
package ch.astina.hesperid.web.pages.escalation;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;

import ch.astina.hesperid.dao.EscalationDAO;
import ch.astina.hesperid.model.base.EscalationScheme;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Secured("ROLE_ADMIN")
public class EscalationIndex
{
    @Inject
    private EscalationDAO escalationDAO;
    
    @SuppressWarnings("unused")
    @Property
    private EscalationScheme escalationScheme;

    public List<EscalationScheme> getEscalationSchemes()
    {
        return escalationDAO.getEscalationSchemes();
    }
    
    @CommitAfter
    public void onActionFromDelete(EscalationScheme es)
    {
        escalationDAO.delete(es);
    }
}
