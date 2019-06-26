/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
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

	public MPart getPart() {

		return part;
	}

	@Override
	public boolean doUpdate() {

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
			/*
			 * Handle "Application does not have an active window"
			 */
			logger.warn(e);
			logger.info("Use alternate isVisible check for part.");
			//
			MApplication application = ModelSupportAddon.getApplication();
			if(application != null) {
				EModelService service = ModelSupportAddon.getModelService();
				if(service != null) {
					List<MPart> parts = service.findElements(application, null, MPart.class, null);
					if(parts != null) {
						for(MPart part : parts) {
							if(this.part == part) {
								if(this.part.isVisible()) {
									List<String> tags = part.getTags();
									return (tags.contains(IPresentationEngine.MINIMIZED) || tags.contains(IPresentationEngine.MINIMIZED_BY_ZOOM));
								}
							}
						}
					}
				}
			}
			return false;
		}
	}
}
