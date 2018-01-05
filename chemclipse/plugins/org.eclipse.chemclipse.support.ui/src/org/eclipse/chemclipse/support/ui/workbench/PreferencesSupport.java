/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.workbench;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.PlatformUI;

public class PreferencesSupport {

	public static void cleanPreferencesByPrefix(List<String> preservePreferencePrefixes) {

		List<String> preservePreferenceNodes = new ArrayList<String>();
		cleanPreferences(preservePreferencePrefixes, preservePreferenceNodes);
	}

	public static void cleanPreferencesByNodeId(List<String> preservePreferenceNodes) {

		List<String> preservePreferencePrefixes = new ArrayList<String>();
		cleanPreferences(preservePreferencePrefixes, preservePreferenceNodes);
	}

	/**
	 * Cleans the preference tree, e.g.:
	 * 
	 * List<String> preservePreferencePrefixes = new ArrayList<String>();
	 * preservePreferencePrefixes.add("org.eclipse.chemclipse");
	 * 
	 * List<String> preservePreferenceNodes = new ArrayList<String>();
	 * preservePreferenceNodes.add("org.eclipse.equinox.internal.p2.ui.sdk.ProvisioningPreferencePage"); // Install/Update
	 * preservePreferenceNodes.add("org.eclipse.equinox.internal.p2.ui.sdk.SitesPreferencePage"); // Available Software Sites
	 * preservePreferenceNodes.add("org.eclipse.equinox.internal.p2.ui.sdk.scheduler.AutomaticUpdatesPreferencePage"); // Automatic Updates
	 * 
	 * 
	 * @param preservePreferencePrefix
	 * @param preservePreferenceNodes
	 */
	public static void cleanPreferences(List<String> preservePreferencePrefixes, List<String> preservePreferenceNodes) {

		PreferenceManager preferenceManager = PlatformUI.getWorkbench().getPreferenceManager();
		for(IPreferenceNode preferenceNode : preferenceManager.getElements(PreferenceManager.POST_ORDER)) {
			String preferenceNodeId = preferenceNode.getId();
			if(!nodeIdStartsWithPrefix(preferenceNodeId, preservePreferencePrefixes) && !preservePreferenceNodes.contains(preferenceNodeId)) {
				preferenceManager.remove(preferenceNode);
			}
		}
	}

	private static boolean nodeIdStartsWithPrefix(String preferenceNodeId, List<String> preservePreferencePrefixes) {

		if(preservePreferencePrefixes.size() == 0) {
			return false;
		} else {
			for(String preservePreferencePrefix : preservePreferencePrefixes) {
				if(preferenceNodeId.startsWith(preservePreferencePrefix)) {
					return true;
				}
			}
			return false;
		}
	}
}
