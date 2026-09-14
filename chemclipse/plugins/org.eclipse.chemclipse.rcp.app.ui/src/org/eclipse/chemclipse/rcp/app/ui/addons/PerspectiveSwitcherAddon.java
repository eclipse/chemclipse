/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Alexander Kurtakov - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.addons;

import java.util.List;

import org.eclipse.chemclipse.rcp.app.ui.preferences.PreferenceSupplier;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import jakarta.annotation.PostConstruct;

/**
 * Some installations only use a single perspective. The perspective switcher is
 * left out of the trim bar in such a case.
 */
public class PerspectiveSwitcherAddon {

	/*
	 * The element ids are defined by the Eclipse platform and must not be changed.
	 * PerspectiveSwitcher.PERSPECTIVE_SWITCHER_ID is the id for model contributed
	 * switchers. The id "PerspectiveSwitcher" is reserved for the compatibility
	 * layer, which detaches such an element from the model unless the perspective
	 * bar has been requested via IWorkbenchWindowConfigurer.
	 */
	private static final String ELEMENT_ID_PERSPECTIVE_SWITCHER = "org.eclipse.e4.ui.PerspectiveSwitcher";
	private static final String ELEMENT_ID_PERSPECTIVE_SPACER = "PerspectiveSpacer";

	@PostConstruct
	public void postConstruct(MApplication application, EModelService modelService) {

		/*
		 * The tool controls are adjusted before the trim bar is rendered.
		 * The value is applied in both directions, otherwise a persisted state
		 * would keep the switcher hidden once the preference is enabled again.
		 */
		boolean showPerspectiveSwitcher = PreferenceSupplier.getShowPerspectiveSwitcher();
		setToBeRendered(application, modelService, ELEMENT_ID_PERSPECTIVE_SWITCHER, showPerspectiveSwitcher);
		setToBeRendered(application, modelService, ELEMENT_ID_PERSPECTIVE_SPACER, showPerspectiveSwitcher);
	}

	private void setToBeRendered(MApplication application, EModelService modelService, String elementId, boolean toBeRendered) {

		List<MToolControl> toolControls = modelService.findElements(application, elementId, MToolControl.class, null);
		for(MToolControl toolControl : toolControls) {
			toolControl.setToBeRendered(toBeRendered);
		}
	}
}
