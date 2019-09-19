/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove ModelAddon dependency
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.handlers;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.PcaContext;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.rcp.app.ui.handlers.OpenSnippetHandler;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

public class CreatePcaEvaluation {

	private static final String EDITOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.compositepart.editor";
	private static final String PCA_PERSPECTIVE = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.perspective";
	public static final String PCA_CREATE_NEW_EDITOR = "CREATE_NEW_EDITOR";

	public static void createPart(ISamplesVisualization<?, ?> samplesVisualization, IEclipseContext context, String title) {

		OpenSnippetHandler.openCompositeSnippet(EDITOR_ID, context, (eclipseContext, part) -> {
			eclipseContext.set(PcaContext.class, new PcaContext(samplesVisualization));
			eclipseContext.set(SelectionManagerSamples.class, new SelectionManagerSamples());
			if(title != null) {
				part.setLabel(title);
			}
			return null;
		});
	}

	@Inject
	@Optional
	public void createNewEditor(@UIEventTopic(PCA_CREATE_NEW_EDITOR) ISamplesVisualization<?, ?> samplesVisualization, IEclipseContext context) {

		switchPespective();
		createPart(samplesVisualization, context, null);
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part, IEclipseContext context) {

		switchPespective();
	}

	private void switchPespective() {

		if(!PerspectiveSwitchHandler.isActivePerspective(PCA_PERSPECTIVE)) {
			PerspectiveSwitchHandler.focusPerspectiveAndView(PCA_PERSPECTIVE, new ArrayList<>());
		}
	}
}
