/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.handlers;

import java.util.Collection;

import jakarta.inject.Named;

import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class NextChromatogramHandler {

	@CanExecute
	boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		if(part != null) {
			if(part.getObject() instanceof IChemClipseEditor) {
				return true;
			}
		}
		return false;
	}

	@Execute
	void execute(EPartService partService, @Named(IServiceConstants.ACTIVE_PART) MPart selectedPart) {

		if(selectedPart != null) {
			Collection<MPart> parts = partService.getParts();
			boolean activate = false;
			exitloop:
			for(MPart part : parts) {
				if(selectedPart == part) {
					activate = true;
				} else {
					if(activate) {
						partService.showPart(part, PartState.ACTIVATE);
						break exitloop;
					}
				}
			}
		}
	}
}
