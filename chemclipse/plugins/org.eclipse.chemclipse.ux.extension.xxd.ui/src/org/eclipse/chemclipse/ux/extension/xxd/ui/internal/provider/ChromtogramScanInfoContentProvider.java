/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ChromtogramScanInfoContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)inputElement;
			IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
			int startRetentionTime = chromatogramSelectionMSD.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelectionMSD.getStopRetentionTime();
			List<IScan> selectedScans = new ArrayList<IScan>();
			for(IScan scan : chromatogramMSD.getScans()) {
				if(scan.getRetentionTime() >= startRetentionTime && scan.getRetentionTime() <= stopRetentionTime) {
					selectedScans.add(scan);
				}
			}
			return selectedScans.toArray();
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
