/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.activator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

@Creatable
public class ContextAddon {

	/**
	 * This add-on is created by the application, see Application.e4xmi.
	 * org.eclipse.chemclipse.rcp.app.ui/Application.e4xmi
	 * 
	 * We plan to switch to the e4.legacy model instead of having an own application model.
	 * As soon as the migration was successful, the ContextAddon can be included via a fragment:
	 * --
	 * fragment.e4xmi
	 * org.eclipse.e4.legacy.ide.application
	 * addons
	 */
	private static ContextAddon contextAddon = null;
	//
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;

	@PostConstruct
	public void postConstruct() {

		contextAddon = this;
	}

	public static MApplication getApplication() {

		return (contextAddon != null) ? contextAddon.application : null;
	}

	public static EModelService getModelService() {

		return (contextAddon != null) ? contextAddon.modelService : null;
	}

	/**
	 * Be aware, this is the 'ApplicationPartServiceImpl' instance:
	 * org.eclipse.e4.ui.internal.workbench.ApplicationPartServiceImpl
	 * ---
	 * In some cases, the 'PartServiceImpl' is needed:
	 * org.eclipse.e4.ui.internal.workbench.PartServiceImpl
	 * ---
	 * Inject it, when creating a part. Have a look also at:
	 * AbstractActivatorUI.updateEPartService(EPartService partService);
	 * 
	 * @return {@link EPartService}
	 */
	public static EPartService getPartService() {

		return (contextAddon != null) ? contextAddon.partService : null;
	}
}
