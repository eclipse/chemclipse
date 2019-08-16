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
package org.eclipse.chemclipse.pcr.model.comparators;

import java.util.Comparator;

import org.eclipse.chemclipse.pcr.model.core.IDetectionFormat;

public class DetectionFormatComparator implements Comparator<IDetectionFormat> {

	@Override
	public int compare(IDetectionFormat detectionFormat1, IDetectionFormat detectionFormat2) {

		return detectionFormat1.getName().compareTo(detectionFormat2.getName());
	}
}
