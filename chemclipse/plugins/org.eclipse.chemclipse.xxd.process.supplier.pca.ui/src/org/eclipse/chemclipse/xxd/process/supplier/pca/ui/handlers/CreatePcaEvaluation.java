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
 * Christoph Läubrich - remove ModelAddon dependency
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.handlers;

import jakarta.inject.Named;

import org.eclipse.chemclipse.rcp.app.ui.handlers.OpenSnippetHandler;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class CreatePcaEvaluation {

	public static final String PCA_PERSPECTIVE = "org.eclipse.chemclipse.xxd.process.supplier.pca.perspective";
	public static final String PCA_EDITOR_STACK = "org.eclipse.chemclipse.xxd.process.supplier.pca.ui.stackId.pcaeditorStack";
	//
	private static final String EDITOR_ID = "org.eclipse.chemclipse.xxd.process.supplier.pca.ui.part.pcaeditor";

	public static void createPart(ISamplesPCA<?, ?> samples, IEclipseContext context, String title) {

		OpenSnippetHandler.openSnippet(EDITOR_ID, context, PCA_EDITOR_STACK, (eclipseContext, part) -> {
			eclipseContext.set(ISamplesPCA.class, samples);
			if(title != null && !title.isEmpty() && !title.isBlank()) {
				part.setLabel(title);
			}
			return null;
		});
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, MApplication application, EModelService service) {

		if(application != null && service != null) {
			/*
			 * Why is element sometimes null?
			 */
			MUIElement element = service.find(PCA_PERSPECTIVE, application);
			if(element != null) {
				MPerspective perspective = service.getPerspectiveFor(element);
				if(perspective != null) {
					if(!perspective.isOnTop()) {
						if(shell != null) {
							MessageDialog.openInformation(shell, "PCA", "Please navigate to the PCA perspective to start an analysis.");
						}
					}
				}
			}
		}
	}
}
