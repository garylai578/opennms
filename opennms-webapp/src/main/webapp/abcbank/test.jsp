<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script type="text/javascript" src="jquery-1.6.4.min.js"></script>

    <title>${pageContext.request.remoteUser}</title>
</head>


<body>
<%
    String name = (String)request.getRemoteUser();
    out.print("<br>name:" + name);
%>

<select id="select" onkeydown="Select.del(this,event)" onkeypress="Select.write(this,event)">
    <option value=""></option>
    <option value="aaa">aaa</option>
    <option value="bbb">bbb</option>
    <option value="ccc">ccc</option>
</select>
<input type="button" value="获取选择值" id="test" onclick="test();"/>
<script>
    var Select = {
        del : function(obj,e){
            if((e.keyCode||e.which||e.charCode) == 8){
                var opt = obj.options[0];
                opt.text = opt.value = opt.value.substring(0, opt.value.length>0?opt.value.length-1:0);
            }
        },
        write : function(obj,e){
            if((e.keyCode||e.which||e.charCode) == 8)return ;
            var opt = obj.options[0];
            opt.selected = "selected";
            opt.text = opt.value += String.fromCharCode(e.charCode||e.which||e.keyCode);
        }
    }
    function test(){
        alert(document.getElementById("select").value);
    }
</script><br />
</body>
</html>
