/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
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

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class ModelSupportAddon {

	private static MApplication mApplication;
	private static EModelService eModelService;
	private static EPartService ePartService;
	private static IEventBroker eventBroker;
	private static IEclipseContext eclipseContext;

	@PostConstruct
	public void postConstruct(MApplication application, EModelService modelService, EPartService partService, IEventBroker broker, IEclipseContext context) {

		mApplication = application;
		eModelService = modelService;
		ePartService = partService;
		eventBroker = broker;
		eclipseContext = context;
	}

	public static MApplication getApplication() {

		return mApplication;
	}

	public static EModelService getModelService() {

		return eModelService;
	}

	public static EPartService getPartService() {

		return ePartService;
	}

	public static IEventBroker getEventBroker() {

		return eventBroker;
	}

	public static IEclipseContext getEclipseContext() {

		return eclipseContext;
	}

	public static void removeEditorsFromPartStack() {

		if(mApplication != null && eModelService != null && ePartService != null) {
			MPartStack partStack = (MPartStack)eModelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, mApplication);
			Collection<MPart> parts = ePartService.getParts();
			for(MPart part : parts) {
				if(part.getObject() != null) {
					part.setToBeRendered(false);
					part.setVisible(false);
					partStack.getChildren().remove(part);
				}
			}
		}
	}

	public static boolean saveDirtyParts() {

		return ePartService.saveAll(true);
	}

	public static String getActivePerspectiveId() {

		MPerspective perspective = getActiveMPerspective();
		if(perspective != null) {
			return perspective.getElementId();
		} else {
			return "";
		}
	}

	public static String getActivePerspective() {

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

	private static MPerspective getActiveMPerspective() {

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
	public static void changePerspective(String perspectiveId) {

		if(mApplication != null && eModelService != null && ePartService != null) {
			MUIElement element = eModelService.find(perspectiveId, mApplication);
			if(element instanceof MPerspective) {
				MPerspective perspective = (MPerspective)element;
				ePartService.switchPerspective(perspective);
			}
		}
	}

	/**
	 * Load and show the part.
	 * 
	 * @param partId
	 */
	public static void focusPart(String partId) {

		if(mApplication != null && eModelService != null && ePartService != null) {
			MUIElement element = eModelService.find(partId, mApplication);
			if(element instanceof MPart) {
				MPart part = (MPart)element;
				if(!ePartService.getParts().contains(part)) {
					ePartService.createPart(part.getElementId());
				}
				ePartService.showPart(part, PartState.ACTIVATE);
			}
		}
	}
}
