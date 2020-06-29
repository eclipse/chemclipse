/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import org.eclipse.chemclipse.model.statistics.AbstractSample;

public class PeakSample extends AbstractSample<PeakSampleData> {

	public PeakSample(IDataInputEntry dataInputEntry) {

		this(dataInputEntry.getName(), dataInputEntry.getGroupName());
	}

	public PeakSample(String name, String groupName) {

		super(name);
		setGroupName(groupName);
	}
}
