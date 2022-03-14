/*******************************************************************************
 * Copyright (c) 2012, 2022 Lablicate GmbH.
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

import javax.inject.Named;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SaveAsHandler {

	private static final Logger logger = Logger.getLogger(SaveAsHandler.class);

	@CanExecute
	boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		if(part != null) {
			if(part.isDirty() || part.getObject() instanceof IChemClipseEditor) {
				return true;
			}
		}
		return false;
	}

	@Execute
	void execute(ECommandService commandService, EHandlerService handlerService, EPartService partService, @Named(IServiceConstants.ACTIVE_PART) MPart part) {

		if(part != null) {
			Object object = part.getObject();
			if(object != null) {
				/*
				 * Save the data as ...
				 */
				if(object instanceof IChemClipseEditor) {
					IChemClipseEditor editor = (IChemClipseEditor)object;
					editor.saveAs();
				} else {
					ParameterizedCommand command = commandService.createCommand("org.eclipse.ui.file.saveAs", null); // $NON-NLS-1$
					if(handlerService.canExecute(command)) {
						handlerService.executeHandler(command);
					} else {
						logger.warn("Couldn't run the Save As... command.");
					}
				}
			}
		}
	}
}
