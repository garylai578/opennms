/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.protocols.http.collector;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.opennms.netmgt.collectd.CollectionAgent;
import org.opennms.netmgt.collectd.CollectionException;
import org.opennms.netmgt.collectd.ServiceCollector;
import org.opennms.netmgt.config.collector.AttributeGroupType;
import org.opennms.protocols.xml.collector.AbstractXmlCollectionHandler;
import org.opennms.protocols.xml.collector.UrlFactory;
import org.opennms.protocols.xml.collector.XmlCollectionAttributeType;
import org.opennms.protocols.xml.collector.XmlCollectionResource;
import org.opennms.protocols.xml.collector.XmlCollectionSet;
import org.opennms.protocols.xml.collector.XmlCollectorException;
import org.opennms.protocols.xml.config.Request;
import org.opennms.protocols.xml.config.XmlDataCollection;
import org.opennms.protocols.xml.config.XmlGroup;
import org.opennms.protocols.xml.config.XmlObject;
import org.opennms.protocols.xml.config.XmlSource;

/**
 * The Class HTTP Collection Handler.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class HttpCollectionHandler extends AbstractXmlCollectionHandler {

    /* (non-Javadoc)
     * @see org.opennms.protocols.xml.collector.XmlCollectionHandler#collect(org.opennms.netmgt.collectd.CollectionAgent, org.opennms.protocols.xml.config.XmlDataCollection, java.util.Map)
     */
    @Override
    public XmlCollectionSet collect(CollectionAgent agent, XmlDataCollection collection, Map<String, Object> parameters) throws CollectionException {
        XmlCollectionSet collectionSet = new XmlCollectionSet(agent);
        collectionSet.setCollectionTimestamp(new Date());
        collectionSet.setStatus(ServiceCollector.COLLECTION_UNKNOWN);
        try {
            for (XmlSource source : collection.getXmlSources()) {
                String urlStr = parseUrl(source.getUrl(), agent, collection.getXmlRrd().getStep());
                Request request = parseRequest(source.getRequest(), agent);
                Document doc = getJsoupDocument(urlStr, request);
                fillCollectionSet(agent, collectionSet, source, doc);
            }
            collectionSet.setStatus(ServiceCollector.COLLECTION_SUCCEEDED);
            return collectionSet;
        } catch (Exception e) {
            collectionSet.setStatus(ServiceCollector.COLLECTION_FAILED);
            throw new CollectionException(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.opennms.protocols.xml.collector.AbstractXmlCollectionHandler#processXmlResource(org.opennms.protocols.xml.collector.XmlCollectionResource, org.opennms.netmgt.config.collector.AttributeGroupType)
     */
    @Override
    protected void processXmlResource(XmlCollectionResource collectionResource, AttributeGroupType attribGroupType) {
    }

    /**
     * Fill collection set.
     *
     * @param agent the agent
     * @param collectionSet the collection set
     * @param source the source
     * @param document the JSoup document
     * @throws ParseException the parse exception
     */
    protected void fillCollectionSet(CollectionAgent agent, XmlCollectionSet collectionSet, XmlSource source, Document doc) throws ParseException {
        for (XmlGroup group : source.getXmlGroups()) {
            log().debug("fillCollectionSet: getting resources for XML group " + group.getName() + " using selector " + group.getResourceXpath());
            Date timestamp = getTimeStamp(doc, group);
            Elements elements = doc.select(group.getResourceXpath());
            log().debug("fillCollectionSet: " + group.getResourceXpath() + " => " + elements);
            String resourceName = getResourceName(elements, group);
            log().debug("fillCollectionSet: processing XML resource " + resourceName);
            XmlCollectionResource collectionResource = getCollectionResource(agent, resourceName, group.getResourceType(), timestamp);
            AttributeGroupType attribGroupType = new AttributeGroupType(group.getName(), group.getIfType());
            for (XmlObject object : group.getXmlObjects()) {
                Elements el = elements.select(object.getXpath());
                XmlCollectionAttributeType attribType = new XmlCollectionAttributeType(object, attribGroupType);
                collectionResource.setAttributeValue(attribType, el == null ? null : el.html());
            }
            processXmlResource(collectionResource, attribGroupType);
            collectionSet.getCollectionResources().add(collectionResource);
        }
    }

    /**
     * Gets the resource name.
     *
     * @param elements the JSoup elements
     * @param group the group
     * @return the resource name
     */
    private String getResourceName(Elements elements, XmlGroup group) {
        // Processing multiple-key resource name.
        if (group.hasMultipleResourceKey()) {
            List<String> keys = new ArrayList<String>();
            for (String key : group.getXmlResourceKey().getKeyXpathList()) {
                log().debug("getResourceName: getting key for resource's name using selector " + key);
                Elements el = elements.select(key);
                if (el != null) {
                    keys.add(el.html());
                }
            }
            return StringUtils.join(keys, "_");
        }
        // If key-xpath doesn't exist or not found, a node resource will be assumed.
        if (group.getKeyXpath() == null) {
            return "node";
        }
        // Processing single-key resource name.
        log().debug("getResourceName: getting key for resource's name using selector " + group.getKeyXpath());
        Elements el = elements.select(group.getKeyXpath());
        return el == null ? null : el.html();
    }

    /**
     * Gets the time stamp.
     * 
     * @param document the JSoup document
     * @param group the group
     * @return the time stamp
     */
    protected Date getTimeStamp(Document doc, XmlGroup group) {
        if (group.getTimestampXpath() == null) {
            return null;
        }
        String pattern = group.getTimestampFormat() == null ? "yyyy-MM-dd HH:mm:ss" : group.getTimestampFormat();
        log().debug("getTimeStamp: retrieving custom timestamp to be used when updating RRDs using selector " + group.getTimestampXpath() + " and pattern " + pattern);
        Elements el = doc.select(group.getTimestampXpath());
        if (el == null) {
            return null;
        }
        String value = el.html();
        Date date = null;
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
            DateTime dateTime = dtf.parseDateTime(value);
            date = dateTime.toDate();
        } catch (Exception e) {
            log().warn("getTimeStamp: can't convert custom timetime " + value + " using pattern " + pattern);
        }
        return date;
    }

    /**
     * Gets the JSoup document.
     *
     * @param urlString the URL string
     * @param request the request
     * @return the JSoup document
     */
    protected Document getJsoupDocument(String urlString, Request request) {
        InputStream is = null;
        URLConnection c = null;
        try {
            URL url = UrlFactory.getUrl(urlString, request);
            c = url.openConnection();
            is = c.getInputStream();
            final Document doc = Jsoup.parse(is, "UTF-8", "/");
            return doc;
        } catch (Exception e) {
            throw new XmlCollectorException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
            UrlFactory.disconnect(c);
        }
    }

}
