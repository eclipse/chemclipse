/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.handlers;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class CreatePcaEvaluation {

	private static final String PCA_EDITOR_PART_STACK_ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.stackId.pcaeditorStack";
	private static final String PCA_EDITOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.part.pcaeditorfx";
	private static final String PCA_PERSPECTIVE = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.perspective";
	public static final String PCA_CREATE_NEW_EDITOR = "CREATE_NEW_EDITOR";
	public static final String DATA_SAMPLES = "DATA_SAMPLES";
	public static final String ALLOW_DATALOAD = "ALLOW_DATALOAD";

	public static void createPart(ISamplesVisualization<?, ?> samplesVisualization) {

		MApplication application = ModelSupportAddon.getApplication();
		EModelService modelService = ModelSupportAddon.getModelService();
		EPartService partService = ModelSupportAddon.getPartService();
		MPartStack partStack = (MPartStack)modelService.find(PCA_EDITOR_PART_STACK_ID, application);
		/*
		 * Create the input part and prepare it.
		 */
		MPart inputPart = partService.createPart(PCA_EDITOR_ID);
		inputPart.getTransientData().put(DATA_SAMPLES, samplesVisualization);
		/*
		 * Add it to the stack and show it.
		 */
		partStack.getChildren().add(inputPart);
		partService.showPart(inputPart, PartState.ACTIVATE);
	}

	@Inject
	@Optional
	public void createNewEditor(@UIEventTopic(PCA_CREATE_NEW_EDITOR) ISamplesVisualization<?, ?> samplesVisualization) {

		switchPespective();
		createPart(samplesVisualization);
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		switchPespective();
		createPart(null);
	}

	private void switchPespective() {

		if(!PerspectiveSwitchHandler.isActivePerspective(PCA_PERSPECTIVE)) {
			PerspectiveSwitchHandler.focusPerspectiveAndView(PCA_PERSPECTIVE, new ArrayList<>());
		}
	}
}
