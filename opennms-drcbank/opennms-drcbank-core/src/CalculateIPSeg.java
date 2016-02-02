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

/**
 * Calculate the ip segments according to the giving gateway and mask
 */
public class CalculateIPSeg {

    private InetAddress gateway;
    private InetAddress startIP;
    private InetAddress endIP;
    private int num;

    public CalculateIPSeg(InetAddress gateway, int num){
        this.gateway = gateway;
        this.num = num;
        calculate();
    }

    private void calculate(){
        InetAddress start = getStartIPfrom(gateway);

    }

    private InetAddress getStartIPfrom(InetAddress gateway){
        InetAddress start = null;
        return start;
    }

    public InetAddress getStartIP(){
        return startIP;
    }

    public InetAddress getEndIP(){
        return endIP;
    }
}
