/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.Set;

import org.eclipse.chemclipse.model.core.IPeak;

public interface ISampleData {

	double getData();

	double getModifiedData();

	Set<IPeak> getPeaks();

	boolean isEmpty();

	void setModifiedData(double normalizedData);

	void setPeaks(Set<IPeak> peaks);
}
