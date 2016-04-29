/**
 * Created by laiguanhui on 2016/4/25.
 */
function judgeIP(ipAddr){
    var regIps = /^(((25[0-5]|2[0-4]\d|1\d{2}|[1-9]\d|[0-9])\.){3}(25[0-5]|2[0-4]\d|1\d{2}|[1-9]\d|[0-9]))$/;
    return regIps.test(ipAddr);
}

function judgeIPAndMask(str) {
        var str = str.split("\/");
        if(str.length != 2)
            return false;
        if(!judgeIP(str[0]))
            return false;
        if(!judgeIP(str[1])){
            var mask = parseInt(str[1]);
            if(mask >= 0 && mask <= 32)
                return true;
            else
                return false;
        }else
            return true;
}

function trimStr(str){return str.replace(/(^\s*)|(\s*$)/g,"");}