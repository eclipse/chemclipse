/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IntegrationType;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class IntegrationAreaContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof IPeak peak) {
			/*
			 * Peaks
			 */
			List<IIntegrationEntry> integrationEntries = peak.getIntegrationEntries();
			adjustIntegrationType(integrationEntries, IntegrationType.PEAK);
			return integrationEntries.toArray();
		} else if(inputElement instanceof IChromatogram<?> chromatogram) {
			/*
			 * Chromatogram
			 */
			List<IIntegrationEntry> integrationEntriesChromatogram = new ArrayList<>(chromatogram.getChromatogramIntegrationEntries());
			adjustIntegrationType(integrationEntriesChromatogram, IntegrationType.CHROMATOGRAM);
			/*
			 * Background
			 */
			List<IIntegrationEntry> integrationEntriesBackground = new ArrayList<>(chromatogram.getBackgroundIntegrationEntries());
			adjustIntegrationType(integrationEntriesBackground, IntegrationType.BACKGROUND);
			/*
			 * Merged
			 */
			List<IIntegrationEntry> integrationEntries = new ArrayList<>();
			integrationEntries.addAll(integrationEntriesChromatogram);
			integrationEntries.addAll(integrationEntriesBackground);
			return integrationEntries.toArray();
		} else {
			return null;
		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	private void adjustIntegrationType(List<IIntegrationEntry> integrationEntries, IntegrationType integrationType) {

		for(IIntegrationEntry integrationEntry : integrationEntries) {
			integrationEntry.setIntegrationType(integrationType);
		}
	}
}