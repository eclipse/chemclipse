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
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.addons;

import jakarta.annotation.PostConstruct;
import java.util.Properties;


import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class PerspectiveApplicationAddon {

	@PostConstruct
	public void postConstruct(MApplication application, EModelService modelService, EPartService partService, IEventBroker eventBroker) {

		/*
		 * The default perspective can be defined in the product definition, e.g.:
		 * -Dapplication.perspective=org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.perspective.main
		 * If no value has been set, the default perspective will.
		 */
		String perspectiveId;
		Properties properties = System.getProperties();
		Object value = properties.get("application.perspective");
		if(value != null && value instanceof String text) {
			perspectiveId = text;
		} else {
			perspectiveId = IPerspectiveAndViewIds.PERSPECTIVE_WELCOME;
		}
		/*
		 * The Bug #408678 has been fixed since Eclipse 4.3.2
		 */
		MPerspective perspective = (MPerspective)modelService.find(perspectiveId, application);
		if(perspective == null) {
			perspectiveId = IPerspectiveAndViewIds.PERSPECTIVE_WELCOME;
			perspective = (MPerspective)modelService.find(perspectiveId, application);
		}
		//
		MPerspectiveStack perspectiveStack = (MPerspectiveStack)modelService.find(IPerspectiveAndViewIds.STACK_PERSPECTIVES, application);
		perspectiveStack.setSelectedElement(perspective);
		if(eventBroker != null) {
			eventBroker.send(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, perspective.getLabel());
		}
	}
}
