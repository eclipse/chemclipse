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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.quickstart;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.handlers.CreatePcaEvaluation;
import org.eclipse.chemclipse.ux.extension.ui.swt.TaskTileContainer;
import org.eclipse.chemclipse.ux.extension.ui.views.WelcomeViewExtensionHandler;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

public class PCAQuickStartPart {

	@Inject
	private IEclipseContext context;

	@PostConstruct
	public void create(Composite parent) {

		TaskTileContainer tileContainer = new TaskTileContainer(parent, 2, () -> context);
		// add default tiles
		tileContainer.addTaskTile(new PcaPeakTileDefinition());
		tileContainer.addTaskTile(new PcaScanTileDefinition());
		// support enhancements
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(WelcomeViewExtensionHandler.PREFERENCE_MAX_TILES, 8);
		store.setDefault(WelcomeViewExtensionHandler.PREFERENCE_MIN_TILES, 2);
		new WelcomeViewExtensionHandler(tileContainer, store, CreatePcaEvaluation.PCA_PERSPECTIVE);
	}
}
