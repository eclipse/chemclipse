/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;

public class EditorServicesSupport {

	private static IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public static IEditorService getSelectedEditorService(String type) {

		IEditorService editorService = null;
		List<IEditorService> services = getEditorServices(type);
		String contributionURI = preferenceStore.getString(PreferenceSupplier.P_EDITOR_TSD);
		/*
		 * Selected Editor
		 */
		exitloop:
		for(IEditorService service : services) {
			if(contributionURI.equals(service.getContributionURI())) {
				editorService = service;
				break exitloop;
			}
		}
		/*
		 * Default Editor (Fallback)
		 */
		if(editorService == null) {
			editorService = new EditorServiceTSD();
		}
		//
		return editorService;
	}

	public static String[][] getAvailableEditors(String type) {

		/*
		 * Fetch services and sort items.
		 */
		List<IEditorService> editorServices = getEditorServices(type);
		Collections.sort(editorServices, (s1, s2) -> s2.getLabel().compareTo(s1.getLabel()));
		/*
		 * Create the options array.
		 */
		String[][] elements = new String[editorServices.size()][2];
		int counter = 0;
		for(IEditorService editorService : editorServices) {
			elements[counter][0] = editorService.getLabel();
			elements[counter][1] = editorService.getContributionURI();
			counter++;
		}
		//
		return elements;
	}

	private static List<IEditorService> getEditorServices(String type) {

		List<IEditorService> editorServices = new ArrayList<>();
		for(Object object : Activator.getDefault().getEditorServices()) {
			if(object instanceof IEditorService editorService) {
				if(editorService.getType().equals(type)) {
					editorServices.add(editorService);
				}
			}
		}
		//
		return editorServices;
	}
}
