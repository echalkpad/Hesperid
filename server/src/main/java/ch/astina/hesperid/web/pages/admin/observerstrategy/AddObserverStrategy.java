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
package ch.astina.hesperid.web.pages.admin.observerstrategy;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;

import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.model.base.ObserverStrategy;
import ch.astina.hesperid.util.StacktraceUtil;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class AddObserverStrategy
{
    @Property
    private ObserverStrategy observerStrategy;
    @InjectComponent
    private Form form;
    @Inject
    private ObserverDAO observerDAO;

    @Secured({"ROLE_ADMIN"})
    void onActivate()
    {
        if (observerStrategy == null) {
            observerStrategy = new ObserverStrategy();
        }
    }

    @CommitAfter
    public Object onSuccess()
    {
        observerDAO.save(observerStrategy);
        return ObserverStrategyIndex.class;
    }

    public void onValidateForm()
    {
        try {
            observerStrategy.getGroovyScriptInstance();
        } catch (Exception e) {
            form.recordError(StacktraceUtil.getStackTrace(e));
        }
    }
}
