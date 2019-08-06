/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolItem;

public class OpenMenuHandler {

	@Execute
	public void execute(MToolItem mToolItem) {

		Object widget = mToolItem.getWidget();
		if(widget instanceof ToolItem) {
			ToolItem toolItem = (ToolItem)widget;
			Listener[] listeners = toolItem.getListeners(SWT.Selection);
			if(listeners.length > 0) {
				Listener listener = listeners[0];
				Event e = new Event();
				e.type = SWT.Selection;
				e.widget = toolItem;
				e.detail = SWT.DROP_DOWN;
				listener.handleEvent(e);
			}
		}
	}
}