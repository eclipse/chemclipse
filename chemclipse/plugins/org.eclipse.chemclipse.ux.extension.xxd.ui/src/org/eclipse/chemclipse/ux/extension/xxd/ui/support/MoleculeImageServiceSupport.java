/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;

public class MoleculeImageServiceSupport {

	public static Object[] getMoleculeImageServices() {

		return Activator.getDefault().getMoleculeImageServices();
	}

	public static IMoleculeImageService getMoleculeImageServiceSelection() {

		return getMoleculeImageServiceSelection(getMoleculeImageServices());
	}

	public static IMoleculeImageService getMoleculeImageServiceSelection(Object[] moleculeImageServices) {

		IMoleculeImageService moleculeImageServiceSelection = null;
		if(moleculeImageServices.length > 0) {
			/*
			 * Get the stored service preference or the
			 * first available offline service.
			 */
			moleculeImageServiceSelection = getMoleculeImageServicesPreference(moleculeImageServices);
			if(moleculeImageServiceSelection == null) {
				List<IMoleculeImageService> moleculeImageServicesOffline = getMoleculeImageServicesOffline(moleculeImageServices);
				if(!moleculeImageServicesOffline.isEmpty()) {
					moleculeImageServiceSelection = moleculeImageServicesOffline.get(0);
				}
			}
		}
		//
		return moleculeImageServiceSelection;
	}

	/*
	 * May return null.
	 */
	private static IMoleculeImageService getMoleculeImageServicesPreference(Object[] moleculeImageServices) {

		String imageServiceSelection = PreferenceSupplier.getMoleculeImageServiceId();
		for(int i = 0; i < moleculeImageServices.length; i++) {
			IMoleculeImageService moleculeImageService = (IMoleculeImageService)moleculeImageServices[i];
			if(moleculeImageService.getClass().getName().equals(imageServiceSelection)) {
				return moleculeImageService;
			}
		}
		//
		return null;
	}

	private static List<IMoleculeImageService> getMoleculeImageServicesOffline(Object[] moleculeImageServices) {

		List<IMoleculeImageService> moleculeImageServicesOffline = new ArrayList<IMoleculeImageService>();
		for(int i = 0; i < moleculeImageServices.length; i++) {
			IMoleculeImageService moleculeImageService = (IMoleculeImageService)moleculeImageServices[i];
			if(!moleculeImageService.isOnline()) {
				moleculeImageServicesOffline.add(moleculeImageService);
			}
		}
		//
		return moleculeImageServicesOffline;
	}
}