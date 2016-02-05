/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
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
public abstract class AbstractChromatogramEditorSupport implements IChromatogramEditorSupport {

	/*
	 * A protected member shall be not used normally. This could be improved.
	 */
	protected EModelService modelService;
	protected MApplication application;
	protected EPartService partService;

	/**
	 * Opens the chromatogram editor.
	 * If file is null, chromatogram must be set.
	 * If the chromatogram is null, the file must be set.
	 * 
	 * @param file
	 * @param chromatogram
	 * @param modelService
	 * @param application
	 * @param partService
	 */
	public void openEditor(File file, IChromatogram chromatogram, String elementId, String contributionURI, String iconURI, String tooltip) {

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
		 * File or chromatogram are maybe null.
		 */
		if(file == null) {
			part.setObject(null);
			if(chromatogram != null) {
				part.setLabel(chromatogram.getName());
			} else {
				part.setLabel("No valid chromatogram");
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
