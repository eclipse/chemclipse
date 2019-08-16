/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.compilation.community.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.support.ui.p2.UpdateSiteSupport;
import org.eclipse.ui.IStartup;

public class PluginStartup implements IStartup {

	@Override
	public void earlyStartup() {

		UpdateSiteSupport updateSiteSupport = new UpdateSiteSupport();
		/*
		 * Set unique new update sites.
		 * Marketplace Plugins
		 * Don't remove the provisioning repositories to enable that
		 * additionally installed plugins can be updated.
		 * updateSiteSupport.removeProvisioningRepositories();
		 */
		Map<String, String> updateSites = new HashMap<String, String>();
		updateSites.put("ChemClipse", "http://download.eclipse.org/chemclipse/updates/0.8.x/repository");
		updateSiteSupport.addProvisioningRepositories(updateSites);
	}
}
