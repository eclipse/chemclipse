/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Move custom made toolbar to the native toolbar with E4Handlers
 * Philip Wenig - modularization
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers;

import javax.inject.Named;

import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.parts.DataExplorerPart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageFileExplorer;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class DataExplorerSettingsHandler {

	@Execute
	public void execute(MPart part, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		Object object = part.getObject();
		if(object instanceof DataExplorerPart) {
			DataExplorerPart dataExplorerPart = (DataExplorerPart)object;
			PreferenceManager preferenceManager = new PreferenceManager();
			preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageFileExplorer()));
			preferenceManager.addToRoot(new PreferenceNode("2", new PreferencePage()));
			//
			PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
			preferenceDialog.create();
			preferenceDialog.setMessage("Settings");
			if(preferenceDialog.open() == Window.OK) {
				try {
					dataExplorerPart.setSupplierFileEditorSupport();
				} catch(Exception e1) {
					MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the settings.");
				}
			}
		}
	}
}
