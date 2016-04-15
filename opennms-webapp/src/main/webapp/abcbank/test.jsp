<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <style type="text/css">
        #box,#box2,#box3,#box4{padding:10px;border:1px solid green;}
    </style>
    <script type="text/javascript">
        //===========================点击展开关闭效果====================================
        function openShutManager(oSourceObj,oTargetObj,shutAble,oOpenTip,oShutTip){
            var sourceObj = typeof oSourceObj == "string" ? document.getElementById(oSourceObj) : oSourceObj;
            var targetObj = typeof oTargetObj == "string" ? document.getElementById(oTargetObj) : oTargetObj;
            var openTip = oOpenTip || "";
            var shutTip = oShutTip || "";
            if(targetObj.style.display!="none"){
                if(shutAble) return;
                targetObj.style.display="none";
                if(openTip  &&  shutTip){
                    sourceObj.innerHTML = shutTip;
                }
            } else {
                targetObj.style.display="block";
                if(openTip  &&  shutTip){
                    sourceObj.innerHTML = openTip;
                }
            }
        }
    </script>
    <title>无标题文档</title>
</head>
<body>
<p><a href="###" onclick="openShutManager(this,'box')">点击展开</a></p>
<form id="box" style="display:none">
    这里面放的是box的内容.
</form>
<p><a href="###" onclick="openShutManager(this,'box2',true)">点击展开</a></p>
<p id="box2" style="display:none">
    这里面放的是box的内容.
</p>
<p><a href="###" onclick="openShutManager(this,'box3',false,'点击关闭','点击展开')">点击展开</a></p>
<p id="box3" style="display:none">
    这里面放的是box的内容.
</p>
<p><button onclick="openShutManager(this,'box4',false,'点击关闭','点击展开')">点击展开</button></p>
<p id="box4" style="display:none">
    这里面放的是box的内容.
</p>
</body>
</html>