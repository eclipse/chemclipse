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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.handlers;

import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.ScorePlot2DPart;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferenceScorePlot2DPage;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class ScorePlot2DSettingsHandler {

	@Execute
	private void execute(MPart part, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		IPreferencePage preferenceScorePlot2DPage = new PreferenceScorePlot2DPage();
		preferenceScorePlot2DPage.setTitle("Score Plot 2D Settings ");
		//
		PreferenceManager preferenceManager = new PreferenceManager();
		preferenceManager.addToRoot(new PreferenceNode("1", preferenceScorePlot2DPage));
		//
		PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
		preferenceDialog.create();
		preferenceDialog.setMessage("Settings");
		if(preferenceDialog.open() == Window.OK) {
			((ScorePlot2DPart)part.getObject()).refresh();
		}
	}
}
