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
package ch.astina.hesperid.installer.web.services;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class InstallationManager 
{
    public static final String HOME_DIRECTORY = "hesperid_home_directory";
    
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private Logger logger = LoggerFactory.getLogger(InstallationManager.class);
    
    public boolean hasWriteableHomeDirectory()
    {
        String installDir = getApplicationHomeDirectoryPath();
        
        if (installDir == null || installDir.isEmpty()) {
            return false;
        }
        
        File dir = new File(installDir);
        
        return (dir.isDirectory() && dir.canWrite());
    }
    
    public String getApplicationHomeDirectoryPath()
    {
        Preferences prefs = Preferences.userNodeForPackage(InstallationManager.class);
        
        String installDir = prefs.get(HOME_DIRECTORY, null);
        
        return installDir;
    }
    
    public void saveHomeDirectory(String homedirectory)
    {
        Preferences prefs = Preferences.userNodeForPackage(InstallationManager.class);
        
        prefs.put(HOME_DIRECTORY, homedirectory);
        
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            logger.error("Could not store home directory", ex);
        }    
    }
    
    public String getConfigurationDirectory()
    {
        return getApplicationHomeDirectoryPath() + FILE_SEPARATOR + "conf";
    }
    
    public String getConfigurationFilePath()
    {
        return getConfigurationDirectory() + FILE_SEPARATOR + "conf.xml";
    }
    
    public void createDirectoryStructure()
    {
        new File(getConfigurationDirectory()).mkdirs();
    }
    
    public boolean hasConfigurationFiles()
    {
        return new File(getConfigurationFilePath()).exists();
    }
    
    public boolean hasWorkingDatabaseConnection()
    {
        if (!hasConfigurationFiles()) {
            return false;
        }
        
        try {
            testDatabaseConnection(getMainConfiguration().getString("database.databaseURL"), getMainConfiguration().getString("database.username"), getMainConfiguration().getString("database.password"));
            return true;
        } catch (Exception e) {
            logger.error("Test database connection failed " + e);
        }
        
        return false;
    }
    
    public String getDatabaseURL(String databaseServer, String databasePort, String databaseName)
    {
        return "jdbc:mysql://"+databaseServer+":"+databasePort+"/"+databaseName;
    }
    
    public void testDatabaseConnection(String databaseUrl, String databaseUser, String databaseSecret) throws Exception
    {
        Connection con = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(databaseUrl,databaseUser,databaseSecret);

            if(!con.isClosed()) {
                logger.info("Successfully connected to " +
                    "MySQL server using TCP/IP...");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    logger.error("Error while closing connection");
                }
            }
        }
    }
    
    public void writeDatabaseConfiguration(String databseServer, String databaseUser, String databasePort, String databaseSecret, String databaseName)
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream("configtemplate.xml");
        
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, "UTF-8");
            String theString = writer.toString();
           
            logger.info("Resource loaded " + theString);
            
            if (!hasConfigurationFiles()) {
                createDirectoryStructure();
            }
            
            File configFile = new File(getConfigurationFilePath());
            
            FileUtils.writeStringToFile(configFile, String.format(theString, databseServer, databasePort, databaseName, databaseUser, databaseSecret));
            
        } catch (Exception e) {
            logger.error("Error while writing database configuration", e);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                logger.error("Error while closing input stream", ex);
            }
        }
    }
    
    public Configuration getMainConfiguration()
    {
        try {
                Configuration mainConfiguration = new XMLConfiguration(getConfigurationFilePath());
                return mainConfiguration;
        } catch (ConfigurationException e) {
                logger.error("Could not get configuration!", e);
                
                return new XMLConfiguration();
        }
    }
}
