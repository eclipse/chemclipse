/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class IdentificationDataSupport {

	private TargetExtendedComparator targetExtendedComparator;

	public IdentificationDataSupport() {
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

	public ILibraryInformation getBestLibraryInformation(Set<IIdentificationTarget> targets) {

		ILibraryInformation libraryInformation = null;
		IIdentificationTarget identificationTarget = getBestIdentificationTarget(targets);
		if(identificationTarget != null) {
			libraryInformation = identificationTarget.getLibraryInformation();
		}
		return libraryInformation;
	}

	public IIdentificationTarget getBestIdentificationTarget(Set<IIdentificationTarget> targets) {

		IIdentificationTarget identificationTarget = null;
		List<IIdentificationTarget> targetsList = new ArrayList<IIdentificationTarget>(targets);
		Collections.sort(targetsList, targetExtendedComparator);
		if(targetsList.size() >= 1) {
			identificationTarget = targetsList.get(0);
		}
		return identificationTarget;
	}
}
