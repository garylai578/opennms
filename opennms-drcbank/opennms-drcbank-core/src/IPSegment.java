/*******************************************************************************
 * This file is part of OpenNMS(R).
 * <p/>
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 * <p/>
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * <p/>
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p/>
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 * http://www.gnu.org/licenses/
 * <p/>
 * For more information contact:
 * OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/
 * http://www.opennms.com/
 *******************************************************************************/

import java.net.InetAddress;
import java.util.Date;

/**
 * IP Addresses Segment, corresponding the requirements of ""
 */
public class IPSegment {
    private InetAddress gateway;
    private InetAddress mask;
    private InetAddress startIP;
    private InetAddress endIP;
    private String nameOfSite;
    private String typeOfSite;
    private Date startDate;
    private String state;
    private String remarks;

    public InetAddress getGateway() {
        return gateway;
    }

    public void setGateway(InetAddress gateway) {
        this.gateway = gateway;
    }

    public InetAddress getMask() {
        return mask;
    }

    public void setMask(InetAddress mask) {
        this.mask = mask;
    }

    public InetAddress getStartIP() {
        return startIP;
    }

    public void setStartIP(InetAddress startIP) {
        this.startIP = startIP;
    }

    public InetAddress getEndIP() {
        return endIP;
    }

    public void setEndIP(InetAddress endIP) {
        this.endIP = endIP;
    }

    public String getNameOfSite() {
        return nameOfSite;
    }

    public void setNameOfSite(String nameOfSite) {
        this.nameOfSite = nameOfSite;
    }

    public String getTypeOfSite() {
        return typeOfSite;
    }

    public void setTypeOfSite(String typeOfSite) {
        this.typeOfSite = typeOfSite;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
