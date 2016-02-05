/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
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

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class ModelSupportAddon {

	private static MApplication mApplication;
	private static EModelService eModelService;
	private static EPartService ePartService;
	private static IEventBroker eventBroker;

	@PostConstruct
	public void postConstruct(MApplication application, EModelService modelService, EPartService partService, IEventBroker broker) {

		mApplication = application;
		eModelService = modelService;
		ePartService = partService;
		eventBroker = broker;
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
}
