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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.rcp.app.ui.handlers.OpenSnippetHandler;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class CreatePcaEvaluation {

	private static final String PCA_PERSPECTIVE = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.perspective";
	public static final String PCA_CREATE_NEW_EDITOR = "CREATE_NEW_EDITOR";

	public static void createPart(ISamplesVisualization<?, ?> samplesVisualization, MApplication application, EModelService modelService, EPartService partService, IEclipseContext context) {

		OpenSnippetHandler.openCompositeSnippet("org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.compositepart.editor", context, eclipseContext -> {
			eclipseContext.set(PcaContext.class, new PcaContext(samplesVisualization));
			return null;
		});
	}

	@Inject
	@Optional
	public void createNewEditor(@UIEventTopic(PCA_CREATE_NEW_EDITOR) ISamplesVisualization<?, ?> samplesVisualization, MApplication application, EModelService modelService, EPartService partService, IEclipseContext context) {

		switchPespective();
		createPart(samplesVisualization, application, modelService, partService, context);
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part, MApplication application, EModelService modelService, EPartService partService, IEclipseContext context) {

		switchPespective();
		createPart(null, application, modelService, partService, context);
	}

	private void switchPespective() {

		if(!PerspectiveSwitchHandler.isActivePerspective(PCA_PERSPECTIVE)) {
			PerspectiveSwitchHandler.focusPerspectiveAndView(PCA_PERSPECTIVE, new ArrayList<>());
		}
	}
}
