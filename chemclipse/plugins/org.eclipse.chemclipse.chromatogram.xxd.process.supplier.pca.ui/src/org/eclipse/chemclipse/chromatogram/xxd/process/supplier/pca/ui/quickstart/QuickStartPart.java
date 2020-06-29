/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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

public class QuickStartPart {

	@Inject
	private IEclipseContext context;

	@PostConstruct
	public void create(Composite parent) {

		TaskTileContainer tileContainer = new TaskTileContainer(parent, 3, () -> context);
		/*
		 * Default
		 */
		tileContainer.addTaskTile(new PeakTileDefinition());
		tileContainer.addTaskTile(new ScanTileDefinition());
		tileContainer.addTaskTile(new FileTileDefinition());
		/*
		 * Additional
		 */
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(WelcomeViewExtensionHandler.PREFERENCE_MAX_TILES, 9);
		store.setDefault(WelcomeViewExtensionHandler.PREFERENCE_MIN_TILES, 3);
		//
		new WelcomeViewExtensionHandler(tileContainer, store, CreatePcaEvaluation.PCA_PERSPECTIVE);
	}
}
