/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public abstract class AbstractUpdateSupport implements IUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractUpdateSupport.class);
	//
	private MPart part;
	private EPartService partService = ModelSupportAddon.getPartService();
	private MApplication application = ModelSupportAddon.getApplication();

	public AbstractUpdateSupport(MPart part) {
		this.part = part;
	}

	@Override
	public boolean doUpdate() {

		/*
		 * TODO: Resolve why and when this happens!
		 * Exception "Application does not have an active window"
		 * is thrown here sometimes.
		 */
		try {
			boolean isVisible = false;
			if(part != null) {
				IEclipseContext activeWindowContext = application.getContext().getActiveChild();
				if(activeWindowContext != null) {
					if(partService.isPartVisible(part)) {
						isVisible = true;
					}
				}
			}
			return isVisible;
		} catch(Exception e) {
			/**
			 * Application does not have an active window
			 * Should not happen, if yes have a look at the usage of Display or shell.
			 * 
			 * DON'T USE:
			 * Display display = Display.getCurrent();
			 * Display.getCurrent().getActiveShell()
			 * ...
			 * MessageDialog.openError(Display.getCurrent().getActiveShell(), "...", "...");
			 * MessageDialog.openError(Display.getDefault().getActiveShell(), "...", "...");
			 * 
			 * USE:
			 * private Display display = Display.getDefault();
			 * private Shell shell = Display.getDefault().getActiveShell();
			 * ...
			 * public Constructor() ...
			 * ...
			 * MessageDialog.openError(shell, "...", "...");
			 */
			logger.warn(e + "\t" + part);
			return false;
		}
	}
}
