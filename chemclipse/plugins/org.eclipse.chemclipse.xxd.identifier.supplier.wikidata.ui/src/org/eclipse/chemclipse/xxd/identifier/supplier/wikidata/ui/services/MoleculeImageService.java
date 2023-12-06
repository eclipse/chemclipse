/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.ui.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.query.QueryStructuralFormula;
import org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.ui.Activator;
import org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.ui.preferences.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(service = {IMoleculeImageService.class}, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class MoleculeImageService implements IMoleculeImageService {

	private static final Logger logger = Logger.getLogger(MoleculeImageService.class);

	@Activate
	public void start() {

		logger.info("Service started: " + getName());
	}

	@Override
	public String getName() {

		return "Wikidata";
	}

	@Override
	public String getVersion() {

		Bundle bundle = Activator.getDefault().getBundle();
		Version version = bundle.getVersion();
		return version.getMajor() + "." + version.getMinor() + "." + version.getMicro();
	}

	@Override
	public String getDescription() {

		return "This service tries to fetch a structural formula from Wikidata.";
	}

	@Override
	public boolean isOnline() {

		return true;
	}

	@Override
	public Image create(Display display, ILibraryInformation libraryInformation, int width, int height) {

		String uri = null;
		String smiles = libraryInformation.getSmiles().trim();
		if(!smiles.isEmpty()) {
			uri = QueryStructuralFormula.fromSMILES(smiles);
		}
		if(uri == null) {
			String cas = libraryInformation.getCasNumber().trim();
			if(!cas.isEmpty()) {
				uri = QueryStructuralFormula.fromCAS(cas);
			}
		}
		if(uri == null) {
			String inchi = libraryInformation.getInChI().trim();
			if(!inchi.isEmpty()) {
				uri = QueryStructuralFormula.fromInChI(inchi);
			}
		}
		if(uri == null) {
			String inchiKey = libraryInformation.getInChIKey().trim();
			if(!inchiKey.isEmpty()) {
				uri = QueryStructuralFormula.fromInChIKey(inchiKey);
			}
		}
		if(uri == null) {
			String name = libraryInformation.getName().trim();
			if(!name.isEmpty()) {
				uri = QueryStructuralFormula.fromName(name);
			}
		}
		if(uri != null) {
			uri = uri.replace("wiki/Special:FilePath", "w/index.php?title=Special:Redirect/file");
			uri = uri + "&width=" + width + "&height=" + height;
			ImageData data = null;
			try {
				uri = resolveRedirects(uri);
				data = new ImageData(new URI(uri).toURL().openStream());
				data.type = SWT.IMAGE_PNG;
			} catch(IOException e) {
				logger.warn(e);
			} catch(URISyntaxException e) {
				logger.warn(e);
			}
			return new Image(display, data);
		}
		return null;
	}

	@Override
	public Class<? extends IWorkbenchPreferencePage> getPreferencePage() {

		return PreferencePage.class;
	}

	private String resolveRedirects(String unresolvedURI) throws IOException, URISyntaxException {

		URL url = new URI(unresolvedURI).toURL();
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setInstanceFollowRedirects(false);
		connection.connect();
		int responseCode = connection.getResponseCode();
		if(responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
			String location = connection.getHeaderField("Location");
			URL newUrl = new URI(location).toURL();
			connection = (HttpURLConnection)newUrl.openConnection();
			connection.connect();
		}
		String resolvedURL = connection.getURL().toString();
		connection.disconnect();
		return resolvedURL;
	}
}
