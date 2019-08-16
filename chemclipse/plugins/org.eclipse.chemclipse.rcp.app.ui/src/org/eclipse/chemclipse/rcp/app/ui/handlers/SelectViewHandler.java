/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.handlers;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;

import org.eclipse.chemclipse.rcp.app.ui.dialogs.SelectViewDialog;

public class SelectViewHandler {

	@Execute
	public void execute(MWindow window) {

		SelectViewDialog selectViewDialog = ContextInjectionFactory.make(SelectViewDialog.class, window.getContext());
		selectViewDialog.open();
	}
}