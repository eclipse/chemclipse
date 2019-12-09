/*******************************************************************************
 * Copyright (c) 2014, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - refactor code to use {@link PartSupport} and {@link PerspectiveSupport}, deprecate methods
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.addons;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.support.ui.workbench.PartSupport;
import org.eclipse.chemclipse.support.ui.workbench.PerspectiveSupport;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * @deprecated Using this class is unsave due to ist static nature and can lead to unexpected "Application does not have active window", use E4DI instead, maybe with helperclasses {@link PartSupport} and {@link PerspectiveSupport} injected.
 *
 */
@Deprecated
public class ModelSupportAddon {

	private static ModelSupportAddon instance;
	@Inject
	private PartSupport partSupport;
	@Inject
	private PerspectiveSupport perspectiveSupport;
	@Inject
	private MApplication mApplication;
	@Inject
	private EModelService eModelService;
	@Inject
	private EPartService ePartService;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private IEclipseContext eclipseContext;

	@PostConstruct
	public void postConstruct() {

		instance = this;
	}

	private static ModelSupportAddon getInstance() {

		if(instance == null) {
			throw new IllegalStateException("E4 not started!?!");
		}
		return instance;
	}

	/**
	 * use E4-DI instead
	 * 
	 * @return
	 */
	public static MApplication getApplication() {

		return getInstance().mApplication;
	}

	/**
	 * use E4-DI instead
	 * 
	 * @return
	 */
	public static EModelService getModelService() {

		return getInstance().eModelService;
	}

	/**
	 * use E4-DI instead
	 * 
	 * @return
	 */
	public static EPartService getPartService() {

		return getInstance().ePartService;
	}

	/**
	 * use E4-DI instead or Activator.getDefault().getEventBroker()
	 * 
	 * @return
	 */
	public static IEventBroker getEventBroker() {

		return getInstance().eventBroker;
	}

	/**
	 * use use E4-DI instead
	 * 
	 * @return
	 */
	public static IEclipseContext getEclipseContext() {

		return getInstance().eclipseContext;
	}

	/**
	 * use {@link PartSupport}
	 * 
	 * @return
	 */
	public static void removeEditorsFromPartStack() {

		getInstance().partSupport.removeEditorsFromPartStack();
	}

	/**
	 * use {@link PartSupport}
	 * 
	 * @return
	 */
	public static boolean saveDirtyParts() {

		return getInstance().partSupport.saveDirtyParts();
	}

	/**
	 * use {@link PartSupport}
	 * 
	 * @return
	 */
	public static String getActivePerspectiveId() {

		return getInstance().perspectiveSupport.getActivePerspectiveId();
	}

	/**
	 * use {@link PartSupport}
	 * 
	 * @return
	 */
	public static String getActivePerspective() {

		return getInstance().perspectiveSupport.getActivePerspective();
	}

	/**
	 * Try to load the perspective.
	 * 
	 * use {@link PartSupport}
	 * 
	 * @param perspectiveId
	 */
	public static void changePerspective(String perspectiveId) {

		getInstance().perspectiveSupport.changePerspective(perspectiveId);
	}

	/**
	 * Load and show the part.
	 * 
	 * use {@link PartSupport}
	 * 
	 * @param partId
	 */
	public static void focusPart(String partId) {

		getInstance().partSupport.focusPart(partId);
	}

	public static PartSupport getPartSupport() {

		return getInstance().partSupport;
	}
}
