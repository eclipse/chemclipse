/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import org.eclipse.chemclipse.model.statistics.AbstractSample;

public class Sample extends AbstractSample<PeakSampleData> {

	public Sample(String name, String groupName) {

		super(name);
		setGroupName(groupName);
	}

	public Sample(String name, String groupName, String classification, String description) {

		super(name);
		setGroupName(groupName);
		setClassification(classification);
		setDescription(description);
	}
}
