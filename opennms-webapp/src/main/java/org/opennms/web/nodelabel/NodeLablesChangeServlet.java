package org.opennms.web.nodelabel;

import org.opennms.core.utils.WebSecurityUtils;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.model.events.EventProxy;
import org.opennms.netmgt.model.events.EventProxyException;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.opennms.netmgt.utils.NodeLabel;
import org.opennms.web.api.Util;
import org.opennms.web.element.NetworkElementFactory;
import org.opennms.web.rest.MultivaluedMapImpl;
import org.opennms.web.rest.RequisitionAccessService;
import org.opennms.web.servlet.MissingParameterException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by laiguanhui on 2016/6/8.
 */
public class NodeLablesChangeServlet extends HttpServlet {

    private static final long serialVersionUID = -1960992894217452285L;
    protected EventProxy proxy;

    /**
     * <p>init</p>
     *
     * @throws javax.servlet.ServletException if any.
     */
    public void init() throws ServletException {
        try {
            this.proxy = Util.createEventProxy();
        } catch (Throwable e) {
            throw new ServletException("JMS Exception", e);
        }
    }

    /** {@inheritDoc} */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nodeidString = request.getParameter("nodeids");
        String nodeLableSting = request.getParameter("nodeLables");
        String[] nodeIds = nodeidString.split("\t");
        String[] nodeLables = nodeLableSting.split("\t");

        for(int i =0; i < nodeIds.length; ++i) {
            String nodeIdString = nodeIds[i];
            String userLabel = nodeLables[i];

            if (nodeIdString == null) {
                throw new MissingParameterException("node", new String[]{"node", "labeltype", "userlabel"});
            }
            if (userLabel == null) {
                throw new MissingParameterException("userlabel", new String[]{"node", "labeltype", "userlabel"});
            }

            try {
                final int nodeId = WebSecurityUtils.safeParseInt(nodeIdString);
                final OnmsNode node = NetworkElementFactory.getInstance(getServletContext()).getNode(nodeId);
                NodeLabel oldLabel = new NodeLabel(node.getLabel(), node.getLabelSource());
                NodeLabel newLabel = null;

                newLabel = new NodeLabel(userLabel, NodeLabel.SOURCE_USERDEFINED);

                final String newNodeLabel = newLabel.getLabel();
                boolean managedByProvisiond = node.getForeignSource() != null && node.getForeignId() != null;
                if (managedByProvisiond) {
                    WebApplicationContext beanFactory = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
                    final TransactionTemplate transactionTemplate = beanFactory.getBean(TransactionTemplate.class);
                    final RequisitionAccessService requisitionService = beanFactory.getBean(RequisitionAccessService.class);
                    transactionTemplate.execute(new TransactionCallback<RequisitionNode>() {
                        public RequisitionNode doInTransaction(TransactionStatus status) {
                            MultivaluedMapImpl params = new MultivaluedMapImpl();
                            params.putSingle("node-label", newNodeLabel);
                            requisitionService.updateNode(node.getForeignSource(), node.getForeignId(), params);
                            return requisitionService.getNode(node.getForeignSource(), node.getForeignId());
                        }
                    });
                }

                this.sendLabelChangeEvent(nodeId, oldLabel, newLabel);

                if (managedByProvisiond) {
//                    response.sendRedirect(Util.calculateUrlBase(request, "admin/nodelabelProvisioned.jsp?node=" + nodeIdString + "&foreignSource=" + node.getForeignSource()));
                } else {
                    NodeLabel.assignLabel(nodeId, newLabel);
//                    response.sendRedirect(Util.calculateUrlBase(request, "element/node.jsp?node=" + nodeIdString));
                }
            } catch (SQLException e) {
                throw new ServletException("Database exception", e);
            } catch (Throwable e) {
                throw new ServletException("Exception sending node label change event", e);
            }
        }

        response.setContentType("text/html;charset=gb2312");
        PrintWriter pw=response.getWriter();
        pw.print("<script language='javascript'>alert('修改完成！' );window.location=('/opennms/admin/nodemanagement/modifyNodeLables.jsp');</script>");
        pw.close();
    }
    /**
     * <p>sendLabelChangeEvent</p>
     *
     * @param nodeId a int.
     * @param oldNodeLabel a {@link org.opennms.netmgt.utils.NodeLabel} object.
     * @param newNodeLabel a {@link org.opennms.netmgt.utils.NodeLabel} object.
     * @throws org.opennms.netmgt.model.events.EventProxyException if any.
     */
    protected void sendLabelChangeEvent(int nodeId, NodeLabel oldNodeLabel, NodeLabel newNodeLabel) throws EventProxyException {

        EventBuilder bldr = new EventBuilder(EventConstants.NODE_LABEL_CHANGED_EVENT_UEI, "NodeLabelChangeServlet");

        bldr.setNodeid(nodeId);
        bldr.setHost("host");

        if (oldNodeLabel != null) {
            bldr.addParam(EventConstants.PARM_OLD_NODE_LABEL, oldNodeLabel.getLabel());
            bldr.addParam(EventConstants.PARM_OLD_NODE_LABEL_SOURCE,oldNodeLabel.getSource() );
        }

        if (newNodeLabel != null) {
            bldr.addParam(EventConstants.PARM_NEW_NODE_LABEL, newNodeLabel.getLabel());
            bldr.addParam(EventConstants.PARM_NEW_NODE_LABEL_SOURCE, newNodeLabel.getSource());
        }

        this.proxy.send(bldr.getEvent());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
