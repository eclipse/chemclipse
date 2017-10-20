/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

@SuppressWarnings({"restriction"})
public interface ISupplierFileEditorSupport extends ISupplierFileIdentifier {

	void openEditor(final File file);

	void openOverview(final File file);

	default void openEditor(File file, Object object, String elementId, String contributionURI, String iconURI, String tooltip) {

		EModelService modelService = ModelSupportAddon.getModelService();
		MApplication application = ModelSupportAddon.getApplication();
		EPartService partService = ModelSupportAddon.getPartService();
		/*
		 * Fix for: "Application does not have an active window"
		 * in org.eclipse.e4.ui.internal.workbench.ApplicationPartServiceImpl
		 */
		MWindow window = application.getChildren().get(0);
		application.getContext().set(EclipseContext.ACTIVE_CHILD, window.getContext());
		/*
		 * Get the editor part stack.
		 */
		MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
		/*
		 * Create the input part and prepare it.
		 */
		MPart part = MBasicFactory.INSTANCE.createInputPart();
		part.setElementId(elementId);
		part.setContributionURI(contributionURI);
		/*
		 * File or chromatogram/mass spectra are maybe null.
		 */
		if(file == null) {
			if(object != null) {
				part.setObject(object);
				if(object instanceof IChromatogram) {
					part.setLabel(((IChromatogram)object).getName());
				} else if(object instanceof IMassSpectra) {
					part.setLabel(((IMassSpectra)object).getName());
				}
			} else {
				part.setObject(null);
				part.setLabel("No valid chromatogram/mass spectra data");
			}
		} else {
			part.setObject(file.getAbsolutePath());
			part.setLabel(file.getName());
		}
		part.setIconURI(iconURI);
		part.setTooltip(tooltip);
		part.setCloseable(true);
		/*
		 * Add it to the stack and show it.
		 */
		partStack.getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}
}
