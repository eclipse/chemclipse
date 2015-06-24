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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.pca.ui.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.pca.ui.editors.PcaEditor;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;

@SuppressWarnings("deprecation")
public class CreatePcaEvaluation {

	@Inject
	private EModelService modelService;
	@Inject
	private MApplication application;
	@Inject
	private EPartService partService;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		/*
		 * Get the editor part stack.
		 */
		MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
		/*
		 * Create the input part and prepare it.
		 */
		MInputPart inputPart = MBasicFactory.INSTANCE.createInputPart();
		inputPart.setElementId(PcaEditor.ID);
		inputPart.setContributionURI(PcaEditor.CONTRIBUTION_URI);
		inputPart.setInputURI(null);
		inputPart.setLabel("PCA Evaluation");
		inputPart.setIconURI(PcaEditor.ICON_URI);
		inputPart.setTooltip(PcaEditor.TOOLTIP);
		inputPart.setCloseable(true);
		/*
		 * Add it to the stack and show it.
		 */
		partStack.getChildren().add(inputPart);
		partService.showPart(inputPart, PartState.ACTIVATE);
	}
}
