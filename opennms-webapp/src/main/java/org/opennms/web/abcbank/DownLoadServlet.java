package org.opennms.web.abcbank;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * Created by laiguanhui on 2016/7/4.
 */

public class DownLoadServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String row = request.getParameter("rowID");
        //得到要下载的文件名
        String fileName = request.getParameter("attach-" + row);
        fileName = new String(fileName.getBytes("iso8859-1"), "UTF-8");
        //得到要下载的文件
        File file = new File(fileName);
        //如果文件不存在
        if(!file.exists()){
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter pw=response.getWriter();
            pw.print("<script language='javascript'>alert('您要下载的资源已被删除！' );window.location=('/opennms/abcbank/webline.jsp?update=true&curPage=" + request.getParameter("curPage")
                    + "&type=" + request.getParameter("type") + "&applicant=" + request.getParameter("applicant") + "&approver=" + request.getParameter("approver")
                    + "&bank=" + request.getParameter("bank") + "&dept=" + request.getParameter("dept") + "');</script>");
            pw.close();
        }else {
            //处理文件名
            String realname = fileName.substring(fileName.indexOf("_") + 1); // 文件路径中不能有_否则获取文件名会失败
            //设置响应头，控制浏览器下载该文件
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(realname, "UTF-8"));
            //读取要下载的文件，保存到文件输入流
            FileInputStream in = new FileInputStream(fileName);
            //创建输出流
            OutputStream out = response.getOutputStream();
            //创建缓冲区
            byte buffer[] = new byte[1024];
            int len;
            //循环将输入流中的内容读取到缓冲区当中
            while ((len = in.read(buffer)) > 0) {
                //输出缓冲区的内容到浏览器，实现文件下载
                out.write(buffer, 0, len);
            }
            //关闭文件输入流
            in.close();
            //关闭输出流
            out.close();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
