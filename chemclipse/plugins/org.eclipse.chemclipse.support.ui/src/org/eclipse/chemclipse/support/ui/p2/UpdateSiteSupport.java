/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.p2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.RepositoryTracker;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;

public class UpdateSiteSupport {

	/**
	 * This method removes all available update sites.
	 */
	public void removeProvisioningRepositories() {

		ProvisioningSession session = ProvisioningUI.getDefaultUI().getSession();
		RepositoryTracker repositoryTracker = ProvisioningUI.getDefaultUI().getRepositoryTracker();
		/*
		 * Remove the enabled locations.
		 */
		URI[] locationsEnabled = repositoryTracker.getKnownRepositories(session);
		repositoryTracker.removeRepositories(locationsEnabled, session);
		/*
		 * Get the disabled locations.
		 */
		IMetadataRepositoryManager metadataRepositoryManager = (IMetadataRepositoryManager)session.getProvisioningAgent().getService(IMetadataRepositoryManager.SERVICE_NAME);
		URI[] locationsDisabled = metadataRepositoryManager.getKnownRepositories(IRepositoryManager.REPOSITORIES_DISABLED | repositoryTracker.getMetadataRepositoryFlags());
		repositoryTracker.removeRepositories(locationsDisabled, session);
	}

	/**
	 * Adds additional repositories, like Eclipse Project Updates and the e4 Tools.
	 */
	public void addDefaultProvisioningRepositories() {

		Map<String, String> updateSites = new HashMap<String, String>();
		updateSites.put("Neon", "http://download.eclipse.org/releases/neon");
		updateSites.put("The Eclipse Project Updates", "http://download.eclipse.org/eclipse/updates/4.6");
		updateSites.put("Eclipse Orbit", "http://download.eclipse.org/tools/orbit/downloads/drops/S20190726194335/repository");
		/*
		 * Add the additional p2 repositories.
		 */
		addProvisioningRepositories(updateSites);
	}

	/**
	 * The p2 repositories need to be set only once.
	 * Map<String, String> updateSites = new HashMap<String, String>();
	 * updateSites.put("XReport", "http://www.lablicate.com/updates/xreport/0.1.x/repository");
	 * ...
	 * 
	 * It is used for example, if a product supplies its own update site.
	 * 
	 * @param updateSites
	 */
	public void addProvisioningRepositories(Map<String, String> updateSites) {

		/*
		 * Get the known repositories.
		 */
		ProvisioningSession session = ProvisioningUI.getDefaultUI().getSession();
		RepositoryTracker repositoryTracker = ProvisioningUI.getDefaultUI().getRepositoryTracker();
		URI[] locations = repositoryTracker.getKnownRepositories(session);
		/*
		 * Check each p2 repository and add it if needed.
		 */
		for(Map.Entry<String, String> updateSite : updateSites.entrySet()) {
			if(updateSiteNeedsToBeAdded(locations, updateSite.getValue())) {
				addUpdateSite(updateSite, session, repositoryTracker);
			}
		}
	}

	private boolean updateSiteNeedsToBeAdded(URI[] locations, String updateSite) {

		for(URI location : locations) {
			/*
			 * Check the URL, if both are equal, the repository is still available.
			 */
			if(updateSite.equals(getCleanURL(location))) {
				return false;
			}
		}
		return true;
	}

	private String getCleanURL(URI uri) {

		/*
		 * Remove tailing "/"
		 */
		String url = uri.toString();
		if(url.endsWith("/")) {
			url = url.substring(0, url.length());
		}
		return url;
	}

	/**
	 * Adds the update site to the p2 repository.
	 */
	private void addUpdateSite(Map.Entry<String, String> updateSite, ProvisioningSession session, RepositoryTracker repositoryTracker) {

		URI location;
		try {
			location = new URI(updateSite.getValue());
			repositoryTracker.addRepository(location, updateSite.getKey(), session);
		} catch(URISyntaxException e) {
			// logger.warn(e);
		}
	}
}
