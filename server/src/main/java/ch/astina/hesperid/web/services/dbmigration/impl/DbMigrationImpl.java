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
package ch.astina.hesperid.web.services.dbmigration.impl;

import ch.astina.hesperid.web.services.dbmigration.DbMigration;
import com.carbonfive.db.migration.DataSourceMigrationManager;
import org.hibernate.Session;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2010-02-09 08:49:03 +0100 (Tue, 09 Feb
 *          2010) $
 */
public class DbMigrationImpl implements DbMigration
{
    private Session session;
    private Logger logger;

    public DbMigrationImpl(Session session, Logger logger) throws Exception
    {
        this.session = session;
        this.logger = logger;
    }

    @Override
    public void updateAllChangelogs() throws Exception
    {
        logger.info("Updating changelogs");

        try {
            ClasspathMigrationResolver rmr = new ClasspathMigrationResolver("dbmigration/");

            DataSourceMigrationManager migrationManager = new DataSourceMigrationManager(
                    new SimpleDataSource(session));
            migrationManager.setMigrationResolver(rmr);
            migrationManager.migrate();
        } catch (RuntimeException ex) {
            logger.error("Error while executing migrations", ex);
        }

        logger.info("Changelogs updated");
    }

    private class SimpleDataSource implements DataSource
    {
        private Session session;

        public SimpleDataSource(Session session)
        {
            this.session = session;
        }

        public Connection getConnection() throws SQLException
        {
            Connection conn = session.getSessionFactory().openSession().connection();
            conn.setAutoCommit(true);
            return conn;
        }

        public Connection getConnection(String string, String string1) throws SQLException
        {
            Connection conn = session.getSessionFactory().openSession().connection();
            conn.setAutoCommit(true);
            return conn;
        }

        public PrintWriter getLogWriter() throws SQLException
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setLogWriter(PrintWriter writer) throws SQLException
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setLoginTimeout(int i) throws SQLException
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getLoginTimeout() throws SQLException
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public <T> T unwrap(Class<T> type) throws SQLException
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isWrapperFor(Class<?> type) throws SQLException
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException
        {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
