/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public interface IChromatogramEditorSupport extends IChromatogramIdentifier {

	/**
	 * Opens the chromatogram editor.
	 * 
	 * @param file
	 * @param modelService
	 * @param application
	 * @param partService
	 */
	void openEditor(final File file, EModelService modelService, MApplication application, EPartService partService);

	/**
	 * Opens the chromatogram editor.
	 * 
	 * @param file
	 * @param modelService
	 * @param application
	 * @param partService
	 */
	void openEditor(IChromatogram chromatogram, EModelService modelService, MApplication application, EPartService partService);

	/**
	 * Opens the chromatogram overview.
	 * 
	 * @param file
	 * @param eventBroker
	 */
	void openOverview(final File file, IEventBroker eventBroker);

	/**
	 * Opens the chromatogram in overview mode.
	 * 
	 * @param chromatogramOverview
	 * @param eventBroker
	 */
	void openOverview(IChromatogramOverview chromatogramOverview, IEventBroker eventBroker);
}
