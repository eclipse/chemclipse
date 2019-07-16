/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.workbench;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * A helper class that can be injected into E4 parts to perform common tasks related to perspectives
 * 
 * @author Christoph Läubrich
 *
 */
@Creatable
public class PerspectiveSupport {

	@Inject
	private EModelService eModelService;
	@Inject
	private EPartService ePartService;
	@Inject
	private MApplication mApplication;

	public String getActivePerspectiveId() {

		MPerspective perspective = getActiveMPerspective();
		if(perspective != null) {
			return perspective.getElementId();
		} else {
			return "";
		}
	}

	public String getActivePerspective() {

		MPerspective perspective = getActiveMPerspective();
		if(perspective != null) {
			String perspectiveName = perspective.getLabel();
			perspectiveName = perspectiveName.replaceAll("<", "");
			perspectiveName = perspectiveName.replaceAll(">", "");
			return perspectiveName;
		} else {
			return "";
		}
	}

	private MPerspective getActiveMPerspective() {

		List<MPerspectiveStack> perspectiveStacks = eModelService.findElements(mApplication, null, MPerspectiveStack.class, null);
		if(perspectiveStacks.size() > 0) {
			MPerspectiveStack perspectiveStack = perspectiveStacks.get(0);
			return perspectiveStack.getSelectedElement();
		}
		return null;
	}

	/**
	 * Try to load the perspective.
	 * 
	 * @param perspectiveId
	 */
	public void changePerspective(String perspectiveId) {

		MUIElement element = eModelService.find(perspectiveId, mApplication);
		if(element instanceof MPerspective) {
			MPerspective perspective = (MPerspective)element;
			ePartService.switchPerspective(perspective);
		}
	}
}
