/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import java.util.List;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PeakIntegrationEntriesContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof IPeakMSD) {
			IPeakMSD peak = (IPeakMSD)inputElement;
			List<? extends IIntegrationEntry> integrationEntries = peak.getIntegrationEntries();
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
}
