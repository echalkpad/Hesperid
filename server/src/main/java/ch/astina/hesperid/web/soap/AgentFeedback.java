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
package ch.astina.hesperid.web.soap;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.dao.hibernate.AssetDAOHibernate;
import ch.astina.hesperid.dao.hibernate.ObserverDAOHibernate;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.Date;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@WebService
@SOAPBinding(parameterStyle = ParameterStyle.BARE)
public class AgentFeedback
{
    @Resource
    private WebServiceContext webServiceContext;
    private final Logger logger = LoggerFactory.getLogger(AgentFeedback.class);

    protected ObjectLocator getObjectLocator()
    {
        ServletContext servletContext = (ServletContext) webServiceContext.getMessageContext().get(
                MessageContext.SERVLET_CONTEXT);
        ObjectLocator objectLocator = (ObjectLocator) servletContext.getAttribute("org.apache.tapestry5.application-registry");

        return objectLocator;
    }

	/**
	 * Returns all observers for the agent client.
	 * @param asset The asset on which the agent runs.
	 * @return All client observers.
	 */
    @WebMethod
    public Observer[] observers(Asset asset)
    {
        logger.info("Asset Information request: " + asset.getAssetIdentifier());

        HibernateSessionSource hibernateSessionSource = getObjectLocator().getService(
                HibernateSessionSource.class);
        Session session = hibernateSessionSource.create();

        try {

            AssetDAO assetDAO = new AssetDAOHibernate(session, new ObserverDAOHibernate(session));
            ObserverDAO observerDAO = new ObserverDAOHibernate(session);
            asset = assetDAO.getAssetForAssetIdentifier(asset.getAssetIdentifier());
            List<Observer> observers = observerDAO.getClientObservers(asset);

            return observers.toArray(new Observer[]{});

        } catch (Exception e) {
            logger.error("Error while sending Client information", e);
        } finally {
            session.close();
        }

        return null;
    }

	/**
	 * Delivers the date of the last edit for any of the client observers for the given asset.
	 * This method also registers a successful request for the agent running on the asset.
	 *
	 * @param asset The asset on which the agent runs.
	 * @return The last edit date of any asset client observer.
	 */
    @WebMethod
    public Date lastUpdatedObserver(Asset asset)
    {
        logger.info("Getting last updated asset information date");

        HibernateSessionSource hibernateSessionSource = getObjectLocator().getService(
                HibernateSessionSource.class);
        Session session = hibernateSessionSource.create();

        try {
            session.beginTransaction();

            AssetDAO assetDAO = new AssetDAOHibernate(session, new ObserverDAOHibernate(session));

            Asset newAsset = assetDAO.getAssetForAssetIdentifier(asset.getAssetIdentifier());

            if (newAsset == null) {
                newAsset = new Asset();
                newAsset.setAssetIdentifier(asset.getAssetIdentifier());
                newAsset.setAssetName(asset.getAssetIdentifier());
                newAsset.setManaged(false);
            }

            newAsset.setLastTickReceived(new Date());

            assetDAO.saveOrUpdateAsset(newAsset);
            session.getTransaction().commit();

            return newAsset.getLastUpdatedObserver();
        } catch (Exception e) {
            logger.error("Error while sending last updated Client information", e);
        } finally {
            session.close();
        }

        return null;
    }

    @WebMethod
    public boolean deliverObserverParameter(ObserverParameter parameter)
    {
        HibernateSessionSource hibernateSessionSource = getObjectLocator().getService(
                HibernateSessionSource.class);
        Session session = hibernateSessionSource.create();

        try {
            session.beginTransaction();

            ObserverDAO observerDAO = new ObserverDAOHibernate(session);

            Observer observer = observerDAO.getObserver(parameter.getObserver().getId());

            parameter.setUpdated(new Date());
            parameter.setObserver(observer);
            observerDAO.save(parameter);

            session.getTransaction().commit();

            return true;

        } catch (Exception e) {
            logger.error("Error while retrieving client information", e);
        } finally {
            session.close();
        }

        return false;
    }
}
