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

import javax.inject.Named;

import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

public class CreatePcaEvaluation {

	private static final String PCA_EDITOR = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.part.pcaeditor";
	private static final String PCA_PERSPECTIVE = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.perspective";

	/*
	 * public static void createPart() {
	 * MApplication application = ModelSupportAddon.getApplication();
	 * EModelService modelService = ModelSupportAddon.getModelService();
	 * EPartService partService = ModelSupportAddon.getPartService();
	 * MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
	 * MPart inputPart = MBasicFactory.INSTANCE.createInputPart();
	 * inputPart.setElementId(PCAEditorPart.ID);
	 * inputPart.setContributionURI(PCAEditorPart.CONTRIBUTION_URI);
	 * inputPart.setLabel("PCA Evaluation");
	 * inputPart.setIconURI(PCAEditorPart.ICON_URI);
	 * inputPart.setTooltip(PcaEditor.TOOLTIP);
	 * inputPart.setCloseable(true);
	 * partStack.getChildren().add(inputPart);
	 * partService.showPart(inputPart, PartState.ACTIVATE);
	 * }
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		PerspectiveSwitchHandler.focusPerspectiveAndView(PCA_PERSPECTIVE, PCA_EDITOR);
	}
}
