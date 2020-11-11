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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferencePage;

public class GroupHandler {

	private static Map<String, IGroupHandler> groupHandlerMap = new HashMap<>();
	private static Map<String, Boolean> visibilityMap = new HashMap<>();
	private static Map<String, IPartHandler> partHandlerMap = new HashMap<>();
	//
	static {
		/*
		 * Group Handlers
		 */
		initialize(new GroupHandlerOverview());
		initialize(new GroupHandlerOverlay());
		initialize(new GroupHandlerScans());
		initialize(new GroupHandlerPeaks());
		initialize(new GroupHandlerChromatogram());
		initialize(new GroupHandlerISTD());
		initialize(new GroupHandlerESTD());
		initialize(new GroupHandlerMiscellaneous());
	}

	/*
	 * Only use static methods.
	 */
	private GroupHandler() {

	}

	public static void activateReferencedParts() {

		IGroupHandler groupHandler = getGroupHandler(GroupHandlerScans.NAME);
		if(groupHandler != null) {
			groupHandler.activateParts();
		}
	}

	public static void updateGroupHandlerMenu() {

		for(IGroupHandler groupHandler : groupHandlerMap.values()) {
			groupHandler.updateMenu();
		}
	}

	/**
	 * Returns the group handler, identified by the name or null
	 * if none exists with the given name.
	 * 
	 * @param name
	 * @return {@link IGroupHandler}
	 */
	public static IGroupHandler getGroupHandler(String name) {

		return groupHandlerMap.get(name);
	}

	public static List<IPreferencePage> getPreferencePages(String elementId) {

		for(IGroupHandler groupHandler : groupHandlerMap.values()) {
			if(groupHandler.getSettingsElementId().equals(elementId)) {
				return groupHandler.getPreferencePages();
			}
		}
		//
		return Collections.emptyList();
	}

	public static boolean toggleShow(String name) {

		boolean partsAreActivated = !visibilityMap.getOrDefault(name, false);
		visibilityMap.put(name, partsAreActivated);
		return partsAreActivated;
	}

	public static IPartHandler getPartHandler(String elementId) {

		return partHandlerMap.get(elementId);
	}

	private static void initialize(IGroupHandler groupHandler) {

		String name = groupHandler.getName();
		groupHandlerMap.put(name, groupHandler);
		visibilityMap.put(name, false);
		//
		for(IPartHandler partHandler : groupHandler.getPartHandler()) {
			String elementId = groupHandler.getPartElementId(partHandler);
			partHandlerMap.put(elementId, partHandler);
		}
	}
}
