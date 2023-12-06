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
package org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.ui.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.rest.PowerUserGateway;
import org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.ui.Activator;
import org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.ui.preferences.PreferencePage;
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

		return "PubChem";
	}

	@Override
	public String getVersion() {

		Bundle bundle = Activator.getDefault().getBundle();
		Version version = bundle.getVersion();
		return version.getMajor() + "." + version.getMinor() + "." + version.getMicro();
	}

	@Override
	public String getDescription() {

		return "This service tries to fetch a structural formula from PubChem.";
	}

	@Override
	public boolean isOnline() {

		return true;
	}

	@Override
	public Image create(Display display, ILibraryInformation libraryInformation, int width, int height) {

		String uri = PowerUserGateway.getBestCompound(libraryInformation);
		try {
			String size = "?image_size=" + width + "x" + height;
			uri = uri + "/PNG" + size;
			ImageData data = new ImageData(new URI(uri).toURL().openStream());
			data.type = SWT.IMAGE_PNG;
			return new Image(display, data);
		} catch(FileNotFoundException e) {
			return null; // 404
		} catch(IOException e) {
			logger.warn(e);
		} catch(URISyntaxException e) {
			logger.warn(e);
		}
		return null;
	}

	@Override
	public Class<? extends IWorkbenchPreferencePage> getPreferencePage() {

		return PreferencePage.class;
	}
}
