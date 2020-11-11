/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

public class CommandPartHandler {

	@Execute
	public void execute(MMenuItem menuItem) {

		String elementId = menuItem.getElementId();
		IPartHandler partHandler = GroupHandler.getPartHandler(elementId);
		if(partHandler != null) {
			partHandler.toggleVisibility();
			GroupHandler.updateGroupHandlerMenu();
		}
	}
}
