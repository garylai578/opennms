package org.opennms.web.abcbank;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opennms.core.bank.BankLogWriter;
import org.opennms.core.bank.WebLine;
import org.opennms.core.bank.WebLineOperator;

/**
 * Created by laiguanhui on 2016/7/4.
 */

public class UploadHandleServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
        File file = new File(savePath);
        //判断上传文件的保存目录是否存在
        if (!file.exists() && !file.isDirectory()) {
            BankLogWriter.getSingle().writeLog(savePath+"目录不存在，需要创建");
            //创建目录
            file.mkdir();
        }
        //消息提示
        String message = "成功添加!";
        WebLine line = new WebLine();
        try{

            //使用Apache文件上传组件处理文件上传步骤：
            //1、创建一个DiskFileItemFactory工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //2、创建一个文件上传解析器
            ServletFileUpload upload = new ServletFileUpload(factory);
            //解决上传文件名的中文乱码
            upload.setHeaderEncoding("UTF-8");
            //3、判断提交上来的数据是否是上传表单的数据
            if(!ServletFileUpload.isMultipartContent(request)){
                //按照传统方式获取数据
                return;
            }
            //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
            List<FileItem> list = upload.parseRequest(request);
            for(FileItem item : list){
                //如果fileitem中封装的是普通输入项的数据
                if(item.isFormField()){
                    String name = item.getFieldName();
                    //解决普通输入项的数据的中文乱码问题
                    String value = item.getString("UTF-8");
                    //value = new String(value.getBytes("iso8859-1"),"UTF-8");
                    if(name.equals("ip"))
                        line.setIp(value);
                    else if(name.equals("state"))
                        line.setState(value);
                    else if(name.equals("type"))
                        line.setType(value);
                    else if(name.equals("group"))
                        line.setGroup(value);
                    else if(name.equals("applicant"))
                        line.setApplicant(value);
                    else if(name.equals("approver"))
                        line.setApprover(value);
                    else if(name.equals("contact"))
                        line.setContact(value);
                    else if(name.equals("bank"))
                        line.setBank(value);
                    else if(name.equals("dept"))
                        line.setDept(value);
                    else if(name.equals("address"))
                        line.setAddress(value);
                    else if(name.equals("start_date"))
                        line.setStart_date(value);
                    else if(name.equals("rent"))
                        line.setRent(value);
                    else if(name.equals("vlan_num"))
                        line.setVlan_num(value);
                    else if(name.equals("port"))
                        line.setPort(value);
                    else if(name.equals("inter"))
                        line.setInter(value);
                    else if(name.equals("comment"))
                        line.setComment(value);
                }else{//如果fileitem中封装的是上传文件
                    //得到上传的文件名称，
                    String filename = item.getName();
                    BankLogWriter.getSingle().writeLog(filename);
                    if(filename==null || filename.trim().equals("")){
                        continue;
                    }
                    //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                    //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                    filename = filename.substring(filename.lastIndexOf("\\")+1);
                    //获取item中的上传文件的输入流
                    InputStream in = item.getInputStream();
                    //创建一个文件输出流
                    String outputFile = savePath + "\\" + filename;
                    line.setAttach(outputFile);
                    FileOutputStream out = new FileOutputStream(outputFile);
                    //创建一个缓冲区
                    byte buffer[] = new byte[1024];
                    //判断输入流中的数据是否已经读完的标识
                    int len = 0;
                    //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                    while((len=in.read(buffer))>0){
                        //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                        out.write(buffer, 0, len);
                    }
                    //关闭输入流
                    in.close();
                    //关闭输出流
                    out.close();
                    //删除处理文件上传时生成的临时文件
                    item.delete();
                }
            }
        }catch (Exception e) {
            message= "文件上传失败！";
            e.printStackTrace();
        }

        String userId = request.getRemoteUser();
        WebLineOperator op = new WebLineOperator();
        try {
            op.insert(line);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BankLogWriter.getSingle().writeLog("用户[" + userId + "]新增专线[" + line + "]");

        response.setContentType("text/html;charset=UTF-8");
        request.setAttribute("update", "true");
        PrintWriter pw=response.getWriter();
        pw.print("<script language='javascript'>alert('" + message + "' );window.location=('/opennms/abcbank/webline.jsp?update=true');</script>");
        pw.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }
}
