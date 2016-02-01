<%--
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2013 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2013 The OpenNMS Group, Inc.
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

--%>

<%-- 
  This page is included by other JSPs to create a box containing an
  abbreviated status of the node.
  
  It expects that a <base> tag has been set in the including page
  that directs all URLs to be relative to the servlet context.
--%>

<%@page language="java"
        contentType="text/html"
        session="true"
        import="org.opennms.web.alarm.*,org.opennms.web.servlet.MissingParameterException,org.opennms.core.utils.WebSecurityUtils,org.opennms.netmgt.model.OnmsSeverity" %>

<%
    int nodeId = -1;
    int maxSeverity = 3;
    int ackCount    = 0;
    int unackCount  = 0;
    Alarm[] alarms = new Alarm[0];

    String nodeIdStr = request.getParameter("nodeId");

    if (nodeIdStr == null) {
        throw new MissingParameterException("node");
    } else {
        nodeId = WebSecurityUtils.safeParseInt(nodeIdStr);
        alarms = AlarmFactory.getAlarmsForNode(nodeId, SortStyle.ID, AcknowledgeType.BOTH, getServletContext());
    }

    boolean nodeDown = false;
    int intfDown = 0;
    int servDown = 0;
    for (Alarm alarm : alarms) {
        if (alarm.getSeverity().getId() <= 3) {
            continue;
        }
        if (alarm.getUei().contains("nodeDown")) {
            nodeDown = true;
        }
        if (alarm.getUei().contains("interfaceDown")) {
            intfDown++;
        }
        if (alarm.getUei().contains("nodeLostService")) {
            servDown++;
        }
        if (alarm.isAcknowledged()) {
            ackCount++;
        } else {
            unackCount++;
        }
        if (alarm.getSeverity().getId() > maxSeverity) {
            maxSeverity = alarm.getSeverity().getId();
        }
    }
    String status  = OnmsSeverity.get(maxSeverity).getLabel();
    String message = "节点 " + (maxSeverity == 3 ? "没有" : status.toLowerCase()) + " 问题。";
    if (nodeDown) {
        message = "节点当前Down。";
    } else if (intfDown > 0 && servDown == 0) {
        message = "节点 " + intfDown + " " + (intfDown > 1 ? "接口" : "接口") + " Down。";
    } else if (servDown > 0 && intfDown == 0) {
        message = "节点 " + servDown + " " + (servDown > 1 ? "服务s" : "服务") + " Down。";
    } else if (servDown > 0 && intfDown > 0) {
        message = "节点 " + intfDown + " " + (intfDown > 1 ? "接口s" : "接口") + " 和 " + servDown + " " + (servDown > 1 ? "服务s" : "服务") + " Down。";
    }
    String details = "";
    if (maxSeverity > 3) {
        String unackText = "<a href='alarm/list.htm?filter=node%3d" + nodeId + "&amp;acktype=unack'><b>"
            + unackCount + "</b> 未确认</a>";
        String ackText = "<a href='alarm/list.htm?filter=node%3d" + nodeId + "&amp;acktype=ack'><b>"
            + ackCount + "</b> 确认</a>";
        details = " 有 " + unackText + " 问题，和 " + ackText + " 问题。";
    }
%>

<table class="o-box">
  <tr class="CellStatus">
    <td align="left" class="<%=status%>">
      <b><%=message%></b><%=details%>
    </td>
  </tr>
</table>

