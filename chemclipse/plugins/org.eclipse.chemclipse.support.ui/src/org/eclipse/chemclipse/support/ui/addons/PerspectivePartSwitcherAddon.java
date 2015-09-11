/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.addons;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * Please register this class as an addon in your
 * Application.e4xmi model:
 * bundleclass://org.eclipse.chemclipse.support.ui/org.eclipse.chemclipse.support.ui.addons.PerspectivePartSwitcherAddon
 */
public class PerspectivePartSwitcherAddon {

	/*
	 * Context and services
	 */
	@Inject
	private static MApplication application;
	@Inject
	private static EModelService modelService;
	@Inject
	private static EPartService partService;

	/**
	 * Try to load the perspective.
	 * 
	 * @param perspectiveId
	 */
	public static void changePerspective(String perspectiveId) {

		if(application != null && modelService != null && partService != null) {
			MUIElement element = modelService.find(perspectiveId, application);
			if(element instanceof MPerspective) {
				MPerspective perspective = (MPerspective)element;
				partService.switchPerspective(perspective);
			}
		}
	}

	/**
	 * Load and show the part.
	 * 
	 * @param partId
	 */
	public static void focusPart(String partId) {

		if(application != null && modelService != null && partService != null) {
			MUIElement element = modelService.find(partId, application);
			if(element instanceof MPart) {
				MPart part = (MPart)element;
				if(!partService.getParts().contains(part)) {
					partService.createPart(part.getElementId());
				}
				partService.showPart(part, PartState.ACTIVATE);
			}
		}
	}
}
