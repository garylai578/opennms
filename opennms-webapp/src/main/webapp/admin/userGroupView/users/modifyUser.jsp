<%--
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2012 The OpenNMS Group, Inc.
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

<%@page language="java"
	contentType="text/html"
	session="true"
%>
<%@page import="org.opennms.netmgt.config.UserFactory"%>
<%@page import="org.opennms.netmgt.config.UserManager"%>
<%@page import="org.opennms.netmgt.config.users.Contact"%>
<%@page import="org.opennms.netmgt.config.users.DutySchedule"%>
<%@page import="org.opennms.netmgt.config.users.User" %>
<%@ page import="org.opennms.web.api.Util" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.ChoiceFormat" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Vector" %>
<%

        final HttpSession userSession = request.getSession(false);
        User user = null;
        String userid = "";
        UserManager userFactory;
        try {
            UserFactory.init();
            userFactory = UserFactory.getInstance();
        } catch (Throwable e) {
            throw new ServletException("UserFactory:modify() " + e);
        }

        if (userSession != null) {
            user = (User) userSession.getAttribute("user.modifyUser.jsp");
            userid = user.getUserId();
        }

        final String baseHref = Util.calculateUrlBase(request);

    Properties pro = new Properties();
    String path = application.getRealPath("/");
    try{
        //读取配置文件
        InputStream in = new FileInputStream(path + "/abcbank/abc-configuration.properties");
        BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        pro.load(bf);
    } catch(FileNotFoundException e){
        out.println(e);
    } catch(IOException e){
        out.println(e);
    }

    //通过key获取配置文件
    String[] bankNames = pro.getProperty("abc-bankname").split("/");
        %>

<jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="修改用户" />
  <jsp:param name="headTitle" value="修改" />
  <jsp:param name="headTitle" value="用户" />
  <jsp:param name="headTitle" value="管理" />
  <jsp:param name="breadcrumb" value="<a href='${baseHref}admin/index.jsp'>管理</a>" />
  <jsp:param name="breadcrumb" value="<a href='${baseHref}admin/userGroupView/index.jsp'>用户和组</a>" />
  <jsp:param name="breadcrumb" value="<a href='${baseHref}admin/userGroupView/users/list.jsp'>用户列表</a>" />
  <jsp:param name="breadcrumb" value="修改用户" />
</jsp:include>

<script type="text/javascript" >

    function validate()
    {
        var minDurationMinsWarning = 5;
        var warnMinDuration = true;
        var group = document.modifyUser.textService.value;
        if(group==0){
            alert("请选择所属支行！");
            return false;
        }

        for (var c = 0; c < document.modifyUser.dutySchedules.value; c++)
        {
            var beginName= "duty" + c + "Begin";
            var endName  = "duty" + c + "End";

            var beginValue = new Number(document.modifyUser.elements[beginName].value);
            var endValue = new Number(document.modifyUser.elements[endName].value);

            var beginHour = Math.floor(beginValue / 100), endHour = Math.floor(endValue / 100);
            var beginMin = beginValue % 100, endMin = endValue % 100
            var duration = ((endHour * 60) + endMin) - ((beginHour * 60) + beginMin);

            if (!document.modifyUser.elements["deleteDuty"+c].checked)
            {
            if (isNaN(beginValue))
            {
                alert("值班表的开始时间 " + (c+1) + " 必须是军事时间，没有其它字符，如800，不是8:00");
                return false;
            }
            if (isNaN(endValue))
            {
                alert("值班表的结束时间 " + (c+1) + " 必须是军事时间，没有其它字符，如800，不是8:00");
                return false;
            }
            if (beginValue > endValue)
            {
                alert("值班表的开始时间 " + (c+1) + " 必须小于结束时间。");
                return false;
            }
            if (beginValue < 0 || beginValue > 2359)
            {
                alert("值班表的开始时间 " + (c+1) + " 必须大于0并且小于2400");
                return false;
            }
            if (endValue < 0 || endValue > 2359)
            {
                alert("值班表的结束时间 " + (c+1) + " 必须大于0并且小于2400");
                return false;
            }
            if ((duration <= minDurationMinsWarning) && warnMinDuration)
            {
                if (!(confirm("Warning: One or more duty schedules are unusually short in duration (" + minDurationMinsWarning + " minutes or less)\n\nSave these schedules?")))
                    return false;
                else
                    warnMinDuration = false;    // only once
            }
            }
        }
        return true;
    }

    function resetPassword()
    {
        newUserWin = window.open("<%= Util.calculateUrlBase(request, "admin/userGroupView/users/newPassword.jsp") %>", "", "fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=500,height=300");
    }

    function addDutySchedules()
    {
        var ok = validate();

        if(ok)
        {
          document.modifyUser.redirect.value="/admin/userGroupView/users/addDutySchedules";
          document.modifyUser.action="<%= Util.calculateUrlBase(request, "admin/userGroupView/users/updateUser") %>";
          document.modifyUser.submit();
        }
    }

    function removeDutySchedules()
    {
        var ok = validate();

        if(ok)
        {
          document.modifyUser.redirect.value="/admin/userGroupView/users/modifyUser.jsp";
          document.modifyUser.action="<%= Util.calculateUrlBase(request, "admin/userGroupView/users/updateUser") %>";
          document.modifyUser.submit();
        }
    }

    function saveUser()
    {
        var ok = validate();

        if(ok)
        {
          document.modifyUser.redirect.value="/admin/userGroupView/users/saveUser";
          document.modifyUser.action="<%= Util.calculateUrlBase(request, "admin/userGroupView/users/updateUser") %>";
          document.modifyUser.submit();
        }
        else
          document.modifyUser.redirect.value="/admin/userGroupView/users/modifyUser.jsp";
    }

    function cancelUser()
    {
        document.modifyUser.action="<%= Util.calculateUrlBase(request, "admin/userGroupView/users/list.jsp") %>";
        document.modifyUser.submit();
    }

</script>


<form id="modifyUser" method="post" name="modifyUser">
<input id="userID" type="hidden" name="userID" value="<%=user.getUserId()%>"/>
<input id="password" type="hidden" name="password"/>
<input id="redirect" type="hidden" name="redirect"/>

<h3>修改用户:<%=userid%></h3>

<div id="contentleft">
  <p>
    <input type="button" value="重置密码" onClick="resetPassword()" />
  </p>

    <table width="100%" border="0" cellspacing="0" cellpadding="2">
            <tr>
              <td colspan="2">
                <p><b>用户信息</b></p>
              </td>
            </tr>
	    <%
	    String tuiPin = null;
	    String email = null;
        String pagerEmail = null;
        String xmppAddress = null;
        String numericPage = null;
        String numericPin = null;
        String textPage = null;
        String textPin = null;
        String workPhone = null;
        String mobilePhone = null;
        String homePhone = null;
        String microblog = null;
        String fullName = null;
        String comments = null;
        Boolean isReadOnly = false;
        try {
            User usertemp = userFactory.getUser(userid);
            if (usertemp != null) {
                    email = userFactory.getEmail(userid);
                    pagerEmail = userFactory.getPagerEmail(userid);
                    xmppAddress = userFactory.getXMPPAddress(userid);
                    numericPage = userFactory.getNumericPage(userid);
                    numericPin = userFactory.getNumericPin(userid);
                    textPage = userFactory.getTextPage(userid);
                    textPin = userFactory.getTextPin(userid);
                    workPhone = userFactory.getWorkPhone(userid);
                    mobilePhone = userFactory.getMobilePhone(userid);
                    homePhone = userFactory.getHomePhone(userid);
                    microblog = userFactory.getMicroblogName(userid);
            } else {
                    Contact[] contact = user.getContact();
                    for (int i = 0; i < contact.length; i++) {
                            Contact tempContact = contact[i];
                            if (contact[i].getType().equals("email")) {
                                    email = contact[i].getInfo();
                            } else if (contact[i].getType().equals("pagerEmail")) {
                                    pagerEmail = contact[i].getInfo();
                            } else if (contact[i].getType().equals("xmppAddress")) {
                                    xmppAddress = contact[i].getInfo();
                            } else if (contact[i].getType().equals("numericPage")) {
                                    numericPage = contact[i].getInfo();
                            } else if (contact[i].getType().equals("textPage")) {
                                    textPage = contact[i].getInfo();
                            } else if (contact[i].getType().equals("workPhone")) {
                                    workPhone = contact[i].getInfo();
                            } else if (contact[i].getType().equals("mobilePhone")) {
                                    mobilePhone = contact[i].getInfo();
                            } else if (contact[i].getType().equals("homePhone")) {
                                    homePhone = contact[i].getInfo();
                            } else if (contact[i].getType().equals("microblog")) {
                            		microblog = contact[i].getInfo();
                            }
                    }
            }
            fullName = user.getFullName();
            comments = user.getUserComments();
            tuiPin = user.getTuiPin();
            isReadOnly = user.isReadOnly();
        } catch (org.exolab.castor.xml.MarshalException e) {
            throw new ServletException("An Error occurred reading the users file", e);
        } catch (org.exolab.castor.xml.ValidationException e) {
            throw new ServletException("An Error occurred reading the users file", e);
        }

        %>
            <!--
            <tr>
              <td valign="top">
                <label id="readOnlyLabel" for="readOnly">只读:</label>
              </td>
              <td align="left" valign="top">
                <input id="readOnly" type="checkbox" name="readOnly"<%=  isReadOnly? " checked=\"true\"":"" %> />
              </td>
            </tr>
				-->
            <tr>
              <td valign="top">
                <label id="fullNameLabel" for="fullName">姓名:</label>
              </td>
              <td align="left" valign="top">
                <input id="fullName" type="text" size="35" name="fullName" value="<%=(fullName == null ? "":fullName) %>" />
              </td>
            </tr>
        <tr >
            <td valign="top">
                <label id="textServiceLabel" for="textService">*所属支行:</label>
            </td>
            <td valign="top">
                <select id="textService" name="textService">
                    <%
                        if(textPage == null || textPage.equals(""))
                            out.print("<option value=\"0\" selected=\"\">请选择</option>");
                    %>
                    <%
                        for(int i = 0; i < bankNames.length; ++i){
                    %>
                    <option value="<%=bankNames[i]%>" <%if(textPage != null && textPage.equals(bankNames[i])) out.print("selected=\"\"");%>><%=bankNames[i]%></option>
                    <%
                        }
                    %>
                </select>
            </td>
        </tr>
            <tr>
              <td valign="top">
                <label id="userCommentsLabel" for="userComments">注释:</label>
              </td>
              <td align="left" valign="top">
                <textarea rows="5" cols="33" id="userComments" name="userComments"><%=(comments == null ? "无" : comments)%></textarea>
              </td>
            </tr>
            <tr style="display: none">
              <td valign="top">
                <label id="tuiPinLabel" for="tuiPin">Telephone PIN:</label>
              </td>
              <td align="left" valign="top">
                <input id="tuiPin" type="text" size="8" name="tuiPin" value="<%=(tuiPin == null ? "" : tuiPin)%>" />
              </td>
            </tr>
            <tr>
              <td colspan="2">
                &nbsp;
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <p><b>通知信息</b></p>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="emailLabel" for="email">电子邮件:</label>
              </td>
              <td valign="top">
                <input id="email" type="text" size="35" name="email" value='<%= (email == null ? "":email) %>'/>
              </td>
            </tr>
            <tr style="display: none">
              <td valign="top" >
                <label id="pemailLabel" for="pemail">Pager Email:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="pemail" name="pemail" value='<%=(pagerEmail == null ? "":pagerEmail)%>'/>
              </td>
            </tr>
            <tr style="display: none">
              <td valign="top">
                <label id="xmppAddressLabel" for="xmppAddress">XMPP Address:</label>
              </td>
              <td valign="top">
                <input id="xmppAddress" type="text" size="35" name="xmppAddress" value='<%=(xmppAddress == null ? "":xmppAddress)%>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="microblogLabel" for="microblog">微博用户名:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="microblog" name="microblog" value='<%=(microblog == null ? "":microblog)%>'/>
              </td>
            </tr>
            <tr style="display: none">
              <td valign="top">
                <label id="numericalServiceLabel" for="numericalService">Numeric Service:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="numericalService" name="numericalService" value='<%=(numericPage == null ? "":numericPage) %>'/>
              </td>
            </tr>
            <tr style="display: none">
              <td valign="top">
                <label id="numericalPinLabel" for="numericalPin">Numeric PIN:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="numericalPin" name="numericalPin" value='<%= (numericPin == null ? "":numericPin)%>'/>
              </td>
            </tr>

            <tr style="display: none">
              <td valign="top">
                <label id="textPinLabel" for="textPin">Text PIN:</label>
              </td>
              <td valign="top">
                <input type="text" size="35" id="textPin" name="textPin" value='<%=(textPin == null ? "":textPin)%>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="workPhoneLabel" for="workPhone">工作电话:</label>
              </td>
              <td valign="top">
                <input type="text" size="16" id="workPhone" name="workPhone" value='<%=(workPhone == null ? "":workPhone)%>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="mobilePhoneLabel" for="mobilePhone">移动电话:</label>
              </td>
              <td valign="top">
                <input type="text" size="16" id="mobilePhone" name="mobilePhone" value='<%=(mobilePhone == null ? "":mobilePhone)%>'/>
              </td>
            </tr>
            <tr>
              <td valign="top">
                <label id="homePhoneLabel" for="homePhone">家庭电话:</label>
              </td>
              <td valign="top">
                <input type="text" size="16" id="homePhone" name="homePhone" value='<%=(homePhone == null ? "":homePhone)%>'/>
              </td>
            </tr>
          </table>
</div>

<div id="contentright">
    <p></p>
  <p>
    这个面板允许你修改为每个用户的信息，包括他们的姓名，通知信息和值班表。
  </p>

  <p>
    <b>通知信息</b> 为你提供配置每个用户的联系信息。
    <%--contact information for each user, including any of <em>email</em>--%>
    <%--address, <em>pager email</em> (in the case that the pager can be reached--%>
    <%--as an email destination), <em>XMPP address</em> (for instant messages--%>
    <%--using the Jabber XMPP protocol), <em>numeric service</em> (for pagers--%>
    <%--that cannot display text messages), <em>text service</em> (for--%>
    <%--alphanumeric pagers), and <em>work phone</em>, <em>mobile phone</em>, and--%>
    <%--<em>home phone</em> for notifications by telephone. The <em>Telephone--%>
    <%--PIN</em> is an optional numeric field used to authenticate called users.--%>
  </p>

  <p>
    <b>值班表</b> 允许你对该用户进行值班安排
<%--      ，allow you to flexibility to determine when users
    should receive notifications.  A duty schedule consists of a list of
    days for which the time will apply and a time range, presented in
    military time with no punctuation.  Using this standard, days run from
    <em>0000</em> to <em>2359</em>.--%>
  </p>

<%--  <p>
    If your duty schedules span midnight, or if your users work multiple,
    non-contiguous time periods, you will need to configure multiple duty
    schedules.  To do so, select the number of duty schedules to add from
    the drop-down box next to <b>[Add This Many Schedules]</b>, and click
    the button.  Then, using the duty schedule fields you've just added,
    create a duty schedule from the start time to 2359 on one day, and
    enter a second duty schedule which begins at 0000 and ends at the end
    of that users coverage.
  </p>--%>

<%--  <p>
    To remove configured duty schedules, put a check in the <em>Delete</em>
    column and click <b>[Remove Checked Schedules]</b>.
  </p>--%>

  <p>
   修改完成后请切记要点击本页左下角的 <b>完成</b> 按钮.
  </p>
</div>

<div class="spacer"><!-- --></div>

<p>
  <b>值班表</b>
</p>
                                  <%
Collection dutySchedules = user.getDutyScheduleCollection();
        %>
				<input type="hidden" name="dutySchedules" value="<%=user.getDutyScheduleCount()%>"/>

          <table width="100%" border="1" cellspacing="0" cellpadding="2" >
            <tr bgcolor="#999999">
              <td>&nbsp;</td>
              <td><b>删除</b></td>
              <td><b>星期一</b></td>
              <td><b>星期二</b></td>
              <td><b>星期三</b></td>
              <td><b>星期四</b></td>
              <td><b>星期五</b></td>
              <td><b>星期六</b></td>
              <td><b>星期日</b></td>
              <td><b>开始时间</b></td>
              <td><b>结束时间</b></td>
            </tr>
                        <%
int i = 0;
        Iterator iter = dutySchedules.iterator();
        while (iter.hasNext()) {
            DutySchedule tmp = new DutySchedule((String) iter.next());
            Vector curSched = tmp.getAsVector();

            %>
                        <tr>
                          <td width="1%"><%=(i + 1)%></td>
                          <td width="1%">
                            <input type="checkbox" name="deleteDuty<%=i%>"/>
                          </td>
                          <%ChoiceFormat days = new ChoiceFormat("0#Mo|1#Tu|2#We|3#Th|4#Fr|5#Sa|6#Su");
            for (int j = 0; j < 7; j++) {
                Boolean curDay = (Boolean) curSched.get(j);

                %>
                          <td width="5%">
                            <input type="checkbox" name="duty<%=i+days.format(j)%>" <%= (curDay.booleanValue() ? "checked=\"true\"" : "")%>/>
                          </td>
                          <%}
            %>
                          <td width="5%">
                            <input type="text" size="4" name="duty<%=i%>Begin" value="<%=curSched.get(7)%>"/>
                          </td>
                          <td width="5%">
                            <input type="text" size="4" name="duty<%=i%>End" value="<%=curSched.get(8)%>"/>
                          </td>
                        </tr>
                        <%i++;
        }
        %>
          </table>

<p>
  <input id="addSchedulesButton" type="button" name="addSchedule"
         value="添加值班表" onclick="addDutySchedules()"/>

  <select name="numSchedules" value="3" size="1">
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
        <option value="6">6</option>
        <option value="7">7</option>
  </select>
</p>

<p>
  <input id="removeSchedulesButton" type="button" name="addSchedule"
         value="删除选中的值班表" onclick="removeDutySchedules()"/>
</p>

<p>
  <input id="saveUserButton" type="submit" name="finish" value="完成"
         onclick="saveUser()"/>
  &nbsp;&nbsp;&nbsp;
  <input id="cancelButton" type="button" name="cancel" value="取消"
         onclick="cancelUser()"/>
</p>

</form>

<jsp:include page="/includes/footer.jsp" flush="false" />
