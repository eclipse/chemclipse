/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;

@SuppressWarnings("deprecation")
public class NextChromatogramHandler {

	@CanExecute
	boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		if(part != null) {
			if(part instanceof MInputPart) {
				MInputPart inputPart = (MInputPart)part;
				if(inputPart.getObject() instanceof IChemClipseEditor) {
					return true;
				}
			}
		}
		return false;
	}

	@Execute
	void execute(EPartService partService, @Named(IServiceConstants.ACTIVE_PART) MInputPart inputPart) {

		if(inputPart != null) {
			Collection<MPart> parts = partService.getParts();
			boolean activate = false;
			exitloop:
			for(MPart part : parts) {
				if(part instanceof MInputPart) {
					MInputPart inputPartSelected = (MInputPart)part;
					if(inputPart == inputPartSelected) {
						activate = true;
					} else {
						if(activate) {
							partService.showPart(inputPartSelected, PartState.ACTIVATE);
							break exitloop;
						}
					}
				}
			}
		}
	}
}
