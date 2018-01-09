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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public abstract class AbstractUpdateSupport implements IUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractUpdateSupport.class);
	//
	private EPartService partService;
	private MPart part;

	public AbstractUpdateSupport(MPart part) {
		this.partService = ModelSupportAddon.getPartService();
		this.part = part;
	}

	@Override
	public boolean doUpdate() {

		/*
		 * Exception "Application does not have an active window"
		 * is thrown here sometimes.
		 * Reason?
		 */
		try {
			if(partService.isPartVisible(part)) {
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			logger.warn(e);
			return false;
		}
	}
}
