/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class PeakScanListSupport {

	/**
	 * Returns the classifier as String or "" if none is set yet.
	 * 
	 * @param object
	 * @return
	 */
	public static String getClassifier(Object object) {

		if(object instanceof IPeak) {
			List<String> classifier = new ArrayList<>(((IPeak)object).getClassifier());
			Collections.sort(classifier);
			return StringUtils.join(classifier, " | ");
		}
		//
		return "";
	}

	/**
	 * Returns the name or "" if none is available.
	 * 
	 * @param object
	 * @return {@link String}
	 */
	public static String getName(Object object) {

		if(object instanceof ITargetSupplier) {
			/*
			 * Sort by Manually Verified, Delta RI, ...
			 */
			float retentionIndex = 0;
			if(object instanceof IPeak) {
				retentionIndex = ((IPeak)object).getPeakModel().getPeakMaximum().getRetentionIndex();
			} else if(object instanceof IScan) {
				retentionIndex = ((IScan)object).getRetentionIndex();
			}
			IdentificationTargetComparator comparator = new IdentificationTargetComparator(SortOrder.DESC, retentionIndex);
			/*
			 * Best Match
			 */
			ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(((ITargetSupplier)object).getTargets(), comparator);
			if(libraryInformation != null) {
				return libraryInformation.getName();
			}
		}
		//
		return "";
	}
}
