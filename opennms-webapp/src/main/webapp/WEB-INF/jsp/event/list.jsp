<%--
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2012 The OpenNMS Group, Inc.
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

--%>

<%@page language="java"	contentType="text/html"	session="true" %>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@page import="org.opennms.core.utils.WebSecurityUtils"%>
<%@page import="org.opennms.web.servlet.XssRequestWrapper"%>
<%@page import="org.opennms.web.springframework.security.Authentication"%>

<%@page import="org.opennms.web.admin.notification.noticeWizard.NotificationWizardServlet"%>

<%@page import="org.opennms.web.filter.Filter"%>

<%@page import="org.opennms.web.event.Event"%>
<%@page import="org.opennms.web.event.EventQueryParms"%>
<%@page import="org.opennms.web.event.EventUtil"%>

<%@page import="org.opennms.web.controller.event.AcknowledgeEventController"%>

<%@page import="org.opennms.web.event.filter.SeverityFilter"%>
<%@page import="org.opennms.web.event.filter.NegativeSeverityFilter"%>
<%@page import="org.opennms.web.event.filter.AfterDateFilter"%>
<%@page import="org.opennms.web.event.filter.BeforeDateFilter"%>
<%@page import="org.opennms.web.event.filter.NodeFilter"%>
<%@page import="org.opennms.web.event.filter.NegativeNodeFilter"%>
<%@page import="org.opennms.web.event.filter.InterfaceFilter"%>
<%@page import="org.opennms.web.event.filter.NegativeInterfaceFilter"%>
<%@page import="org.opennms.web.event.filter.ServiceFilter"%>
<%@page import="org.opennms.web.event.filter.NegativeServiceFilter"%>
<%@page import="org.opennms.web.event.filter.AcknowledgedByFilter"%>
<%@page import="org.opennms.web.event.filter.NegativeAcknowledgedByFilter"%>
<%@page import="org.opennms.web.event.filter.ExactUEIFilter"%>
<%@page import="org.opennms.web.event.filter.NegativeExactUEIFilter"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%--
  This page is written to be the display (view) portion of the EventFilterController
  at the /event/list.htm URL.  It will not work by itself, as it requires two request
  attributes be set:
  
  1) events: the list of org.opennms.web.element.Event instances to display
  2) parms: an org.opennms.web.event.EventQueryParms object that holds all the 
     parameters used to make this query
--%>

<%
	XssRequestWrapper req = new XssRequestWrapper(request);

    //required attributes
    Event[] events = (Event[])req.getAttribute( "events" );
    int eventCount = req.getAttribute( "eventCount" ) == null ? -1 : (Integer)req.getAttribute( "eventCount" );
    EventQueryParms parms = (EventQueryParms)req.getAttribute( "parms" );

    if( events == null || parms == null ) {
        throw new ServletException( "Missing either the events or parms request attribute." );
    }

    // Make 'action' the opposite of the current acknowledgement state
    String action = AcknowledgeType.ACKNOWLEDGED.getShortName();
    if (parms.ackType != null && parms.ackType == AcknowledgeType.ACKNOWLEDGED) {
    	action = AcknowledgeType.UNACKNOWLEDGED.getShortName();
    }

    pageContext.setAttribute("addPositiveFilter", "[+]");
    pageContext.setAttribute("addNegativeFilter", "[-]");
    pageContext.setAttribute("addBeforeFilter", "[&gt;]");
    pageContext.setAttribute("addAfterFilter", "[&lt;]");
%>




<%@page import="org.opennms.web.event.AcknowledgeType"%>
<%@page import="org.opennms.web.event.SortStyle"%><jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="事件列表" />
  <jsp:param name="headTitle" value="列表" />
  <jsp:param name="headTitle" value="事件" />
  <jsp:param name="breadcrumb" value="<a href= 'event/index.jsp' title='事件系统页面'>事件</a>" />
  <jsp:param name="breadcrumb" value="列表" />
</jsp:include>

  <script type="text/javascript">
    function checkAllCheckboxes() {
       if( document.acknowledge_form.event.length ) {  
         for( i = 0; i < document.acknowledge_form.event.length; i++ ) {
           document.acknowledge_form.event[i].checked = true
         }
       }
       else {
         document.acknowledge_form.event.checked = true
       }
         
    }
    
    function submitForm(anAction)
    {
        var isChecked = false
        var numChecked = 0;
 
        if (document.acknowledge_form.event.length)
        {
            for( i = 0; i < document.acknowledge_form.event.length; i++ ) 
            {
              //make sure something is checked before proceeding
              if (document.acknowledge_form.event[i].checked)
              {
                isChecked=true;
                numChecked+=1;
              }
            }
            
            if (isChecked && document.acknowledge_form.multiple)
            {
              if (numChecked == parseInt(document.acknowledge_form.event.length)) 
              { 
                var newPageNum = parseInt(document.acknowledge_form.multiple.value) - 1;
                var findVal = "multiple=" + document.acknowledge_form.multiple.value;
                var replaceWith = "multiple=" + newPageNum;
                var tmpRedirect = document.acknowledge_form.redirectParms.value;
                document.acknowledge_form.redirectParms.value = tmpRedirect.replace(findVal, replaceWith);
                document.acknowledge_form.submit();
              } 
              else 
              {
                document.acknowledge_form.submit();
              }
            }
            else if (isChecked)
            {
              document.acknowledge_form.submit();
            }
            else
            {
                alert("请选中你想" + anAction + "的事件。");
            }
        }
        else
        {
            if (document.acknowledge_form.event.checked)
            {
                document.acknowledge_form.submit();
            }
            else
            {
                alert("请选中你想" + anAction + "的事件。");
            }
        }
    }
    
    function submitNewNotificationForm(uei) {
    	document.getElementById("uei").value=uei;
    	document.add_notification_form.submit();
    }

  </script>


      <!-- menu -->
      <div id="linkbar">
      <ul>
        <li><a href="<%=this.makeLink( parms, new ArrayList<Filter>())%>" title="删除所有查询条件" >查看所有事件</a></li>
        <li><a href="<%=org.opennms.web.api.Util.calculateUrlBase(req, "event/advsearch.jsp")%>" title="更多高级查询和排序选项">高级查询</a></li>
        <li><a href="<%=org.opennms.web.api.Util.calculateUrlBase(req, "event/severity.jsp")%>">级别图例</a></li>

        <% if( req.isUserInRole( Authentication.ROLE_ADMIN ) || !req.isUserInRole( Authentication.ROLE_READONLY ) ) { %>
          <% if ( eventCount > 0 ) { %>
            <li>
              <!-- hidden form for acknowledging the result set -->
              <form style="display:inline" action="event/acknowledgeByFilter" method="post" name="acknowledge_by_filter_form">
                <input type="hidden" name="redirectParms" value="<c:out value="<%=req.getQueryString()%>"/>" />
                <input type="hidden" name="actionCode" value="<%=action%>" />
                <%=org.opennms.web.api.Util.makeHiddenTags(req)%>
              </form>

              <% if( parms.ackType == AcknowledgeType.UNACKNOWLEDGED ) { %> 
                <a href="javascript:void()" onclick="if (confirm('你确定要确认当前查询下的所有事件，包括那些在屏幕上没有显示的？  (<%=eventCount%> 条事件)')) {  document.acknowledge_by_filter_form.submit(); }" title="确认当前查询条件下的所有事件，甚至那些没有显示在屏幕上的">确认整个查询</a>
              <% } else { %>
                <a href="javascript:void()" onclick="if (confirm('你确定要取消确认当前查询下的所有事件，包括那些在屏幕上没有显示的？  (<%=eventCount%> 条事件)')) { document.acknowledge_by_filter_form.submit(); }" title="取消确认当前查询条件下的所有事件，甚至那些没有显示在屏幕上的">取消确认整个查询</a>
              <% } %>
            </li>
          <% } %>
        <% } %>
      </ul>
      </div>
      <!-- end menu -->

	  <!-- hidden form for adding a new Notification -->
	  <form action="admin/notification/noticeWizard/notificationWizard" method="post" name="add_notification_form">
	  	<input type="hidden" name="sourcePage" value="<%=NotificationWizardServlet.SOURCE_PAGE_OTHER_WEBUI%>" />
	  	<input type="hidden" name="uei" id="uei" value="" /> <!-- Set by java script -->
	  </form>
	  

      <jsp:include page="/includes/event-querypanel.jsp" flush="false" />
          
            <% if( events.length > 0 ) { %>
              <% String baseUrl = this.makeLink(parms); %>
              <% if ( eventCount == -1 ) { %>
                <jsp:include page="/includes/resultsIndexNoCount.jsp" flush="false" >
                  <jsp:param name="itemCount"    value="<%=events.length%>" />
                  <jsp:param name="baseurl"  value="<%=baseUrl%>"    />
                  <jsp:param name="limit"    value="<%=parms.limit%>"      />
                  <jsp:param name="multiple" value="<%=parms.multiple%>"   />
                </jsp:include>
              <% } else { %>
                <jsp:include page="/includes/resultsIndex.jsp" flush="false" >
                  <jsp:param name="count"    value="<%=eventCount%>" />
                  <jsp:param name="baseurl"  value="<%=baseUrl%>"    />
                  <jsp:param name="limit"    value="<%=parms.limit%>"      />
                  <jsp:param name="multiple" value="<%=parms.multiple%>"   />
                </jsp:include>
              <% } %>
            <% } %>          


            <% if( parms.filters.size() > 0 || parms.ackType == AcknowledgeType.UNACKNOWLEDGED || parms.ackType == AcknowledgeType.ACKNOWLEDGED ) { %>
              <% int length = parms.filters.size(); %>
              <p>查询条件:
                  <% if( parms.ackType == AcknowledgeType.UNACKNOWLEDGED ) { %>
                    <span class="filter">活动事件 <a href="<%=this.makeLink(parms, AcknowledgeType.ACKNOWLEDGED)%>" title="显示已确认事件">[-]</a></span>
                  <% } else if( parms.ackType == AcknowledgeType.ACKNOWLEDGED ) { %>
                    <span class="filter">已确认事件 <a href="<%=this.makeLink(parms, AcknowledgeType.UNACKNOWLEDGED)%>" title="显示活动事件">[-]</a></span>
                  <% } %>
                  
                  <% for( int i=0; i < length; i++ ) { %>
                    <% Filter filter = (Filter)parms.filters.get(i); %>
                    &nbsp; <span class="filter"><%= WebSecurityUtils.sanitizeString(filter.getTextDescription()) %><a href="<%=this.makeLink( parms, filter, false)%>" title="删除过滤条件">[-]</a></span>
                  <% } %>
              </p>
            <% } %>

    <% if( req.isUserInRole( Authentication.ROLE_ADMIN ) || !req.isUserInRole( Authentication.ROLE_READONLY ) ) { %>
      <form action="event/acknowledge" method="post" name="acknowledge_form">
        <input type="hidden" name="redirectParms" value="<c:out value="<%=req.getQueryString()%>"/>" />
        <input type="hidden" name="actionCode" value="<%=action%>" />
        <%=org.opennms.web.api.Util.makeHiddenTags(req)%>
    <% } %>
                <jsp:include page="/includes/key.jsp" flush="false" />

    <% String acknowledgeEvent = System.getProperty("opennms.eventlist.acknowledge"); %>

      <table>
        <thead>
        <tr>
          <% if( "true".equals(acknowledgeEvent) ) { %>
						<% if( req.isUserInRole( Authentication.ROLE_ADMIN ) || !req.isUserInRole( Authentication.ROLE_READONLY ) ) { %>
							<% if ( parms.ackType == AcknowledgeType.UNACKNOWLEDGED ) { %>
							<th width="1%">确认</th>
							<% } else { %>
							<th width="1%">取消确认</th>
							<% } %>
						<% } else { %>
							<th width="1%">&nbsp;</th>
						<% } %>
          <% } %>
          <th width="1%"> <%=this.makeSortLink( parms, SortStyle.ID,        SortStyle.REVERSE_ID,        "id",        "ID"        )%></th>
          <th width="10%"><%=this.makeSortLink( parms, SortStyle.SEVERITY,  SortStyle.REVERSE_SEVERITY,  "severity",  "级别"  )%></th>
          <th width="19%"><%=this.makeSortLink( parms, SortStyle.TIME,      SortStyle.REVERSE_TIME,      "time",      "时间"      )%></th>
          <th width="25%"><%=this.makeSortLink( parms, SortStyle.NODE,      SortStyle.REVERSE_NODE,      "node",      "节点"      )%></th>
          <th width="16%"><%=this.makeSortLink( parms, SortStyle.INTERFACE, SortStyle.REVERSE_INTERFACE, "interface", "接口" )%></th>
          <th width="15%"><%=this.makeSortLink( parms, SortStyle.SERVICE,   SortStyle.REVERSE_SERVICE,   "service",   "服务"   )%></th>
        </tr>
        </thead>     
      <% for( int i=0; i < events.length; i++ ) {
        Event event = events[i];
      	pageContext.setAttribute("event", event);
      %>
      
        <tr valign="top" class="<%=events[i].getSeverity().getLabel()%>">
          <% if( "true".equals(acknowledgeEvent) ) { %>
						<% if( request.isUserInRole( Authentication.ROLE_ADMIN ) || !req.isUserInRole( Authentication.ROLE_READONLY ) ) { %>
						<td valign="top" rowspan="3" class="divider">
									<input type="checkbox" name="event" value="<%=events[i].getId()%>" /> 
							</td>
							<% } else { %>
								<td valign="top" rowspan="3" class="divider">&nbsp;</td>
							<% } %>
            <% } %>

          <td valign="top" rowspan="3" class="divider"><a href="event/detail.jsp?id=<%=events[i].getId()%>"><%=events[i].getId()%></a></td>
          
          <td valign="top" rowspan="3" class="divider bright"> 
            <strong><%
                if("Normal".equals(events[i].getSeverity().getLabel())){
                    out.print("正常");
                }else if("Warning".equals(events[i].getSeverity().getLabel())){
                    out.print("警告");
                }else if("Major".equals(events[i].getSeverity().getLabel())){
                    out.print("主要");
                }else if("Minor".equals(events[i].getSeverity().getLabel())){
                    out.print("次要");
                }else if("Critical".equals(events[i].getSeverity().getLabel())){
                    out.print("严重");
                }else if("Indeterminate".equals(events[i].getSeverity().getLabel())){
                    out.println("不确定");
                }else if("Cleared".equals(events[i].getSeverity().getLabel())){
                    out.print("已清除");
                }
            %></strong>
            <% Filter severityFilter = new SeverityFilter(events[i].getSeverity()); %>      
            <% if( !parms.filters.contains( severityFilter )) { %>
              <nobr>
                <a href="<%=this.makeLink( parms, severityFilter, true)%>" class="filterLink" title="只显示此级别的事件">${addPositiveFilter}</a>
                <a href="<%=this.makeLink( parms, new NegativeSeverityFilter(events[i].getSeverity()), true)%>" class="filterLink" title="不显示此级别的事件">${addNegativeFilter}</a>
              </nobr>
            <% } %>
          </td>
          <td class="divider">
            <nobr><fmt:formatDate value="${event.time}" type="date" dateStyle="default"/>&nbsp;<fmt:formatDate value="${event.time}" type="time" pattern="HH:mm:ss"/></nobr>
            <nobr>
              <a href="<%=this.makeLink( parms, new AfterDateFilter(events[i].getTime()), true)%>"  class="filterLink" title="只显示此事件发生之后">${addAfterFilter}</a>
              <a href="<%=this.makeLink( parms, new BeforeDateFilter(events[i].getTime()), true)%>" class="filterLink" title="只显示此事件发生之前">${addBeforeFilter}</a>
            </nobr>
          </td>
          <td class="divider">
	    <% if(events[i].getNodeId() != 0 && events[i].getNodeLabel()!= null ) { %>
              <% Filter nodeFilter = new NodeFilter(events[i].getNodeId(), getServletContext()); %>             
              <% String[] labels = this.getNodeLabels( events[i].getNodeLabel() ); %>
              <a href="element/node.jsp?node=<%=events[i].getNodeId()%>" title="<%=labels[1]%>"><%=labels[0]%></a>
                    
              <% if( !parms.filters.contains(nodeFilter) ) { %>
                <nobr>
                  <a href="<%=this.makeLink( parms, nodeFilter, true)%>" class="filterLink" title="只显示此节点的事件">${addPositiveFilter}</a>
                  <a href="<%=this.makeLink( parms, new NegativeNodeFilter(events[i].getNodeId(), getServletContext()), true)%>" class="filterLink" title="不显示此节点的事件">${addNegativeFilter}</a>
                </nobr>
              <% } %>
            <% } else { %>
              &nbsp;
            <% } %>
          </td>
          <td class="divider">
            <% if(events[i].getIpAddress() != null ) { %>
              <% Filter intfFilter = new InterfaceFilter(events[i].getIpAddress()); %>
              <% if( events[i].getNodeId() != 0 ) { %>
                <c:url var="interfaceLink" value="element/interface.jsp">
                  <c:param name="node" value="<%=String.valueOf(events[i].getNodeId())%>"/>
                  <c:param name="intf" value="<%=events[i].getIpAddress()%>"/>
                </c:url>
                <a href="<c:out value="${interfaceLink}"/>" title="这个接口上的更多信息"><%=events[i].getIpAddress()%></a>
              <% } else { %>
                 <%=events[i].getIpAddress()%>
              <% } %>
              <% if( !parms.filters.contains(intfFilter) ) { %>
                <nobr>
                  <a href="<%=this.makeLink( parms, intfFilter, true)%>" class="filterLink" title="只显示此IP地址的事件">${addPositiveFilter}</a>
                  <a href="<%=this.makeLink( parms, new NegativeInterfaceFilter(events[i].getIpAddress()), true)%>" class="filterLink" title="不显示此接口的事件">${addNegativeFilter}</a>
                </nobr>
              <% } %>
            <% } else { %>
              &nbsp;
            <% } %>
          </td>
          <td class="divider">
            <% if(events[i].getServiceName() != null && events[i].getServiceName() != "") { %>
              <% Filter serviceFilter = new ServiceFilter(events[i].getServiceId(), getServletContext()); %>
              <% if( events[i].getNodeId() != 0 && events[i].getIpAddress() != null ) { %>
                <c:url var="serviceLink" value="element/service.jsp">
                  <c:param name="node" value="<%=String.valueOf(events[i].getNodeId())%>"/>
                  <c:param name="intf" value="<%=events[i].getIpAddress()%>"/>
                  <c:param name="service" value="<%=String.valueOf(events[i].getServiceId())%>"/>
                </c:url>
                <a href="<c:out value="${serviceLink}"/>" title="这个服务上的更多信息"><c:out value="<%=events[i].getServiceName()%>"/></a>
              <% } else { %>
                <c:out value="<%=events[i].getServiceName()%>"/>
              <% } %>
              <% if( !parms.filters.contains( serviceFilter )) { %>
                <nobr>
                  <a href="<%=this.makeLink( parms, serviceFilter, true)%>" class="filterLink" title="只显示此服务类型的事件">${addPositiveFilter}</a>
                  <a href="<%=this.makeLink( parms, new NegativeServiceFilter(events[i].getServiceId(), getServletContext()), true)%>" class="filterLink" title="不显示此服务的事件">${addNegativeFilter}</a>
                </nobr>
              <% } %>                            
            <% } else { %>
              &nbsp;
            <% } %>
          </td>
          
        </tr>
        
        <tr valign="top" class="<%= events[i].getSeverity().getLabel() %>">
          <td colspan="4">
            <% if(events[i].getUei() != null) { %>
              <% Filter exactUEIFilter = new ExactUEIFilter(events[i].getUei()); %>
                <%=events[i].getUei()%>
              <% if( !parms.filters.contains( exactUEIFilter )) { %>
                <nobr>
                  <a href="<%=this.makeLink( parms, exactUEIFilter, true)%>" class="filterLink" title="只显示此UEI的事件">${addPositiveFilter}</a>
                  <a href="<%=this.makeLink( parms, new NegativeExactUEIFilter(events[i].getUei()), true)%>" class="filterLink" title="不显示此UEI的事件">${addNegativeFilter}</a>
                </nobr>
              <% } %>
              <% if (req.isUserInRole(Authentication.ROLE_ADMIN)) { %>
               	  <a href="javascript:void()" onclick="submitNewNotificationForm('<%=events[i].getUei()%>');" title="编辑此UEI事件的通知">编辑事件通知</a>
              <% } %>
            <% } else { %>
              &nbsp;
            <% } %>
          </td>
        </tr>
       
        <tr valign="top" class="<%= events[i].getSeverity().getLabel() %>">
          <td colspan="5"><%=events[i].getLogMessage()%></td>
        </tr>
       
      <% } /*end for*/%>
      </table>
        
        <p><%=events.length%>条事件
          <% 
          if( (req.isUserInRole( Authentication.ROLE_ADMIN ) || !req.isUserInRole( Authentication.ROLE_READONLY )) && "true".equals(acknowledgeEvent)) { %>
            <% if( parms.ackType == AcknowledgeType.UNACKNOWLEDGED ) { %>
              <input type="button" value="确认事件" onClick="submitForm('<%= AcknowledgeType.UNACKNOWLEDGED.getShortName() %>')"/>
              <input TYPE="button" VALUE="选择所有" onClick="checkAllCheckboxes()"/>
              <input TYPE="reset" />
            <% } else if( parms.ackType == AcknowledgeType.ACKNOWLEDGED ) { %>
              <input type="button" value="取消确认事件" onClick="submitForm('<%= AcknowledgeType.ACKNOWLEDGED.getShortName() %>')"/>
              <input TYPE="button" VALUE="选择所有" onClick="checkAllCheckboxes()"/>
              <input TYPE="reset" />
            <% } %>
          <% } %>
        </p>
      </form>

            <% if( events.length > 0 ) { %>
              <% String baseUrl = this.makeLink(parms); %>
              <% if ( eventCount == -1 ) { %>
                <jsp:include page="/includes/resultsIndexNoCount.jsp" flush="false" >
                  <jsp:param name="itemCount"    value="<%=events.length%>" />
                  <jsp:param name="baseurl"  value="<%=baseUrl%>"    />
                  <jsp:param name="limit"    value="<%=parms.limit%>"      />
                  <jsp:param name="multiple" value="<%=parms.multiple%>"   />
                </jsp:include>
              <% } else { %>
                <jsp:include page="/includes/resultsIndex.jsp" flush="false" >
                  <jsp:param name="count"    value="<%=eventCount%>" />
                  <jsp:param name="baseurl"  value="<%=baseUrl%>"    />
                  <jsp:param name="limit"    value="<%=parms.limit%>"      />
                  <jsp:param name="multiple" value="<%=parms.multiple%>"   />
                </jsp:include>
              <% } %>
            <% } %>          

<jsp:include page="/includes/bookmark.jsp" flush="false" />

<jsp:include page="/includes/footer.jsp" flush="false" />


<%!
    String urlBase = "event/list";

    protected String makeSortLink( EventQueryParms parms, SortStyle style, SortStyle revStyle, String sortString, String title ) {
      StringBuffer buffer = new StringBuffer();

      buffer.append( "<nobr>" );
      
      if( parms.sortStyle == style ) {
          buffer.append( "<img src=\"images/arrowdown.gif\" hspace=\"0\" vspace=\"0\" border=\"0\" alt=\"" );
          buffer.append( title );
          buffer.append( " 升序排序\"/>" );
          buffer.append( "&nbsp;<a href=\"" );
          buffer.append( this.makeLink( parms, revStyle ));
          buffer.append( "\" title=\"反向排序\">" );
      } else if( parms.sortStyle == revStyle ) {
          buffer.append( "<img src=\"images/arrowup.gif\" hspace=\"0\" vspace=\"0\" border=\"0\" alt=\"" );
          buffer.append( title );
          buffer.append( " 降序排序\"/>" );
          buffer.append( "&nbsp;<a href=\"" );
          buffer.append( this.makeLink( parms, style )); 
          buffer.append( "\" title=\"反向排序\">" );
      } else {
          buffer.append( "<a href=\"" );
          buffer.append( this.makeLink( parms, style ));
          buffer.append( "\" title=\"排序 " );
          buffer.append( sortString );
          buffer.append( "\">" );   
      }

      buffer.append( title );
      buffer.append( "</a>" );

      buffer.append( "</nobr>" );

      return( buffer.toString() );
    }

    
    public String getFiltersAsString(List<Filter> filters ) {
        StringBuffer buffer = new StringBuffer();
    
        if( filters != null ) {
            for( int i=0; i < filters.size(); i++ ) {
                buffer.append( "&amp;filter=" );
                String filterString = EventUtil.getFilterString((Filter)filters.get(i));
                buffer.append( java.net.URLEncoder.encode(filterString) );
            }
        }      
    
        return( buffer.toString() );
    }

    public String makeLink( SortStyle sortStyle, AcknowledgeType ackType, List<Filter> filters, int limit ) {
      StringBuffer buffer = new StringBuffer( this.urlBase );
      buffer.append( "?sortby=" );
      buffer.append( sortStyle.getShortName() );
      buffer.append( "&amp;acktype=" );
      buffer.append( ackType.getShortName() );
      if (limit > 0) {
          buffer.append( "&amp;limit=" ).append(limit);
      }
      buffer.append( this.getFiltersAsString(filters) );

      return( buffer.toString() );
    }


    public String makeLink( EventQueryParms parms ) {
      return( this.makeLink( parms.sortStyle, parms.ackType, parms.filters, parms.limit) );
    }


    public String makeLink( EventQueryParms parms, SortStyle sortStyle ) {
      return( this.makeLink( sortStyle, parms.ackType, parms.filters, parms.limit) );
    }


    public String makeLink( EventQueryParms parms, AcknowledgeType ackType ) {
      return( this.makeLink( parms.sortStyle, ackType, parms.filters, parms.limit) );
    }


    public String makeLink( EventQueryParms parms, List<Filter> filters ) {
      return( this.makeLink( parms.sortStyle, parms.ackType, filters, parms.limit) );
    }


    public String makeLink( EventQueryParms parms, Filter filter, boolean add ) {
      List<Filter> newList = new ArrayList<Filter>( parms.filters );
      if( add ) {
        newList.add( filter );
      }
      else {
        newList.remove( filter );
      }

      return( this.makeLink( parms.sortStyle, parms.ackType, newList, parms.limit ));
    }


    public String[] getNodeLabels( String nodeLabel ) {
        String[] labels = null;

        if( nodeLabel.length() > 32 ) {
            String shortLabel = nodeLabel.substring( 0, 31 ) + "...";                        
            labels = new String[] { shortLabel, nodeLabel };
        }
        else {
            labels = new String[] { nodeLabel, nodeLabel };
        }

        return( labels );
    }

%>
