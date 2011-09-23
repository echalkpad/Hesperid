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
package ch.astina.hesperid.installer.web.pages;

import ch.astina.hesperid.installer.web.services.InstallationManager;
import com.spreadthesource.tapestry.installer.services.Restart;
import java.io.File;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Import(stylesheet = {"context:styles/main.css", "context:styles/content.css",
    "context:styles/forms.css"})
public class Index
{
    @Inject
    private InstallationManager installationManager;
    @InjectComponent
    private Form directoryForm;
    @InjectComponent
    private Form databaseForm;
    @Property
    private String homedirectory;
    @Property
    private String databaseUser;
    @Property
    private String databaseServer;
    @Property
    private String databasePort;
    @Property
    private String databasePassword;
    @Property
    private String databaseName;
    private Logger logger = LoggerFactory.getLogger(Index.class);

    public void onActivate()
    {
        databaseServer = "localhost";
        databasePort = "3306";
    }

    public boolean getHasWriteableHomeDirectory()
    {
        return installationManager.hasWriteableHomeDirectory();
    }

    public boolean isInstallStep1()
    {
        return !installationManager.hasWriteableHomeDirectory();
    }

    public boolean isInstallStep2()
    {
        return installationManager.hasWriteableHomeDirectory() && !installationManager.hasWorkingDatabaseConnection();
    }

    public void onValidateFormFromDirectoryForm()
    {
        File file = new File(homedirectory);

        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                directoryForm.recordError("Directory does not exist and could not be created");
            }
        } else {
            if (!file.canWrite()) {
                directoryForm.recordError("Cannot write directory");
            }
        }
    }

    public void onSuccessFromDirectoryForm()
    {
        installationManager.saveHomeDirectory(homedirectory);
    }

    public void onValidateFormFromDatabaseForm()
    {
        try {
            installationManager.testDatabaseConnection(installationManager.getDatabaseURL(databaseServer, databasePort, databaseName), databaseUser, databasePassword);
        } catch (Exception e) {
            databaseForm.recordError("Could not connect to database " + e.getMessage());
        }
    }

    public Object onSuccessFromDatabaseForm()
    {
        installationManager.writeDatabaseConfiguration(databaseServer, databaseUser, databasePort, databasePassword, databaseName);

        return new Restart();
    }
}
