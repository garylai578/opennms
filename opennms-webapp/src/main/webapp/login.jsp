<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.opennms.core.bank.BankLogWriter" %><%--
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

<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core'%>

<jsp:include page="/includes/header.jsp" flush="false">
  <jsp:param name="title" value="登录" />
  <jsp:param name="nonavbar" value="true" />
</jsp:include>

<%
  SimpleDateFormat df = new SimpleDateFormat("MM-dd");
  Long nowTime = new Date().getTime();
  Long expertTime = new SimpleDateFormat("yyyy-MM-dd").parse("2016-07-15").getTime();

  if(nowTime > expertTime){
    BankLogWriter.getSingle().setXml();
    out.print("<script language='javascript'>alert('试用期已到，点击确定关闭！' );</script>");
    return;
  }
%>

<%-- this form-login-page form is also used as the 
         form-error-page to ask for a login again.
         --%>
<c:if test="${not empty param.login_error}">
  <p style="color:red;">
    <strong>你登录失败，请重试</strong>
  </p>

  <%-- This is: AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY --%>
  <p>原因是:${SPRING_SECURITY_LAST_EXCEPTION.message}</p>
</c:if>

<div class="formOnly">
  <form action="<c:url value='j_spring_security_check'/>" method="post">
    <p>
      用户:<input type="text" id="input_j_username" name="j_username" <c:if test="${not empty param.login_error}">value='<c:out value="${SPRING_SECURITY_LAST_USERNAME}"/>'</c:if> /><br /><br />
      密码:<input type='password' name='j_password'>
    </p>
      
    <!--
    <p><input type="checkbox" name="_spring_security_remember_me"> Don't ask for my password for two weeks</p>
    -->
    
    <input name="Login" type="submit" value="登录" />
    <input name="j_usergroups" type="hidden" value=""/>
    <%-- input name="reset" type="reset" value="Reset" /> --%>

    <script type="text/javascript">
      if (document.getElementById) {
        document.getElementById('input_j_username').focus();
      }
    </script>
  
  </form>
</div>

<hr />

<jsp:include page="/includes/footer.jsp" flush="false" />
