/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ScanListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private TargetExtendedComparator targetExtendedComparator;

	public ScanListTableComparator() {
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IScan && e2 instanceof IScan) {
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}

	private ILibraryInformation getLibraryInformation(List<IPeakTarget> targets) {

		ILibraryInformation libraryInformation = null;
		targets = new ArrayList<IPeakTarget>(targets);
		Collections.sort(targets, targetExtendedComparator);
		if(targets.size() >= 1) {
			libraryInformation = targets.get(0).getLibraryInformation();
		}
		return libraryInformation;
	}
}
