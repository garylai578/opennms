/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
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

package org.opennms.features.vaadin.app;

import java.util.ArrayList;
import java.util.List;

import org.opennms.features.vaadin.topology.MenuBarBuilder;
import org.opennms.features.vaadin.topology.SimpleGraphContainer;
import org.opennms.features.vaadin.topology.TopologyComponent;

import com.vaadin.event.Action;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;


public class CommandManager  implements Action.Handler {

    private List<Command> m_commandList = new ArrayList<Command>();
    private List<Command> m_commandHistoryList = new ArrayList<Command>();

    public List<Command> getCommandList() {
        return m_commandList;
    }

    public void setCommandList(List<Command> commandList) {
        m_commandList = commandList;
    }
    
    public void addCommand(Command command, boolean addToContextMenu, String parentMenu) {
        if(addToContextMenu) {
            command.setAction();
        }
        command.setParentMenu(parentMenu);
        m_commandList.add(command);
    }
    
    public void addCommand(Command command, boolean addToContextMenu) {
        if(addToContextMenu) {
            command.setAction();
        }
        m_commandList.add(command);
    }

    MenuBar getMenuBar() {
        MenuBarBuilder menuBarBuilder = new MenuBarBuilder();
        for(Command command : getCommandList()) {
            String menuPosition = command.getMenuPosition();
            menuBarBuilder.addMenuCommand(menuCommand(command), menuPosition);
        }
        MenuBar menuBar = menuBarBuilder.get();
        return menuBar;
    }
    
    public MenuBar.Command menuCommand(final Command command){
        return new MenuBar.Command() {
            
            public void menuSelected(MenuItem selectedItem) {
                command.doCommand(null);
                m_commandHistoryList.add(command);
            }
        };
    }

    void addActionHandlers(TopologyComponent topologyComponent) {
        topologyComponent.addActionHandler(this);
    }

    public Action[] getActions(Object target, Object sender) {
        List<Action> actionList = new ArrayList<Action>();
        for(Command command : m_commandList) {
            if(command.isAction() && command.appliesToTarget(target)) {
                actionList.add(command);
            }
        }
        return actionList.toArray(new Action[actionList.size()]);
    }

    public void handleAction(Action action, Object sender, Object target) {
        if(action instanceof Command) {
            Command command = (Command) action;
            command.doCommand(target);
            
            m_commandHistoryList.add(command);
        }
    }
    
    public List<Command> getHistoryList(){
        return m_commandHistoryList;
    }

    public void updateMenuBar(SimpleGraphContainer graphContainer) {
        
    }
    
}
