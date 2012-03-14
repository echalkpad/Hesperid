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
package ch.astina.hesperid.web.pages.admin;

import ch.astina.hesperid.dao.MailServerDAO;
import ch.astina.hesperid.dao.SystemSettingsDAO;
import ch.astina.hesperid.model.internal.MailServer;
import ch.astina.hesperid.model.internal.MailServerSecureConnectionType;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.EnumSelectModel;
import org.apache.tapestry5.util.EnumValueEncoder;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 105 $, $Date: 2011-09-21 16:01:05 +0200 (Mi, 21 Sep 2011) $
 */
public class SystemSettings
{
    @Inject
    private MailServerDAO mailServerDAO;

	@Inject
	private SystemSettingsDAO systemSettingsDAO;

    @Inject
    private ComponentResources componentResources;

    @Property
    private MailServer mailServer;

	@Property
	private ch.astina.hesperid.model.internal.SystemSettings systemSettings;

    public void onActivate()
    {
        mailServer = mailServerDAO.getMailServerForId(1l);

        if (mailServer == null) {
            mailServer = new MailServer();
        }

	    systemSettings = systemSettingsDAO.getSystemSettingsForId(1l);

	    if (systemSettings == null) {
		    systemSettings = new ch.astina.hesperid.model.internal.SystemSettings();
	    }
    }

    public SelectModel getMailServerSecureConnectionTypeModel()
    {
        return new EnumSelectModel(MailServerSecureConnectionType.class, componentResources.getMessages());
    }

    public ValueEncoder<MailServerSecureConnectionType> getMailServerSecureConnectionTypeEncoder()
    {
        return new EnumValueEncoder<MailServerSecureConnectionType>(MailServerSecureConnectionType.class);
    }

    @CommitAfter
    public void onSuccessFromSettingsForm()
    {
        mailServerDAO.saveOrUpdateMailServer(mailServer);
	    systemSettingsDAO.saveOrUpdateSystemSettings(systemSettings);
    }
}
