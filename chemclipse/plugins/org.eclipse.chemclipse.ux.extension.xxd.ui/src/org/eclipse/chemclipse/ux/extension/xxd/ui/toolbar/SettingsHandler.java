/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;

public class SettingsHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, MDirectMenuItem menuItem) {

		PreferenceManager preferenceManager = new PreferenceManager();
		List<IPreferencePage> preferencePages = getPreferencePages(menuItem);
		if(preferencePages.size() > 0) {
			/*
			 * n pages could exist.
			 */
			int i = 1;
			for(IPreferencePage preferencePage : preferencePages) {
				preferenceManager.addToRoot(new PreferenceNode(Integer.toString(i++), preferencePage));
			}
			//
			PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
			preferenceDialog.create();
			preferenceDialog.setMessage("Settings");
			preferenceDialog.open();
		} else {
			MessageDialog.openInformation(shell, "Settings", "No setting pages have been defined.");
		}
	}

	private List<IPreferencePage> getPreferencePages(MDirectMenuItem menuItem) {

		List<IPreferencePage> preferencePages = new ArrayList<>();
		if(menuItem != null) {
			preferencePages.addAll(GroupHandler.getPreferencePages(menuItem.getElementId()));
		}
		return preferencePages;
	}
}
