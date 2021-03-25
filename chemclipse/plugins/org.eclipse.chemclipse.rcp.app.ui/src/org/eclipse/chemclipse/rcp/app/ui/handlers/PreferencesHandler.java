/*******************************************************************************
 * Copyright (c) 2012, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;

@SuppressWarnings("restriction")
public class PreferencesHandler {

	@Execute
	void execute(ECommandService commandService, EHandlerService handlerService, Logger logger) {

		ParameterizedCommand command = commandService.createCommand("org.eclipse.ui.window.preferences", null);
		if(handlerService.canExecute(command)) {
			//
			List<String> preservePreferencePrefixes = new ArrayList<String>();
			preservePreferencePrefixes.add("org.eclipse.chemclipse");
			preservePreferencePrefixes.add("org.eclipse.swtchart");
			//
			List<String> preservePreferenceNodes = new ArrayList<String>();
			preservePreferenceNodes.add("org.eclipse.equinox.internal.p2.ui.sdk.ProvisioningPreferencePage"); // Install/Update
			preservePreferenceNodes.add("org.eclipse.equinox.internal.p2.ui.sdk.SitesPreferencePage"); // Available Software Sites
			preservePreferenceNodes.add("org.eclipse.equinox.internal.p2.ui.sdk.scheduler.AutomaticUpdatesPreferencePage"); // Automatic Updates
			preservePreferenceNodes.add("org.eclipse.ui.preferencePages.Workbench"); // General
			preservePreferenceNodes.add("org.eclipse.ui.net.NetPreferences"); // Network Connections
			preservePreferenceNodes.add("org.eclipse.jsch.ui.SSHPreferences"); // SSH2
			//
			PreferencesSupport.cleanPreferences(preservePreferencePrefixes, preservePreferenceNodes);
			//
			handlerService.executeHandler(command);
		} else {
			logger.warn("Can't handle to open the preference dialog.");
		}
	}
}
