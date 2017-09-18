/*******************************************************************************
 * Copyright (c) 2017 jan.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.core.runtime.IProgressMonitor;

public class NormalizationData implements IDataModification {

	public enum Normalization {
		MEAN_NORMALIZATION, MEDIAN_NORMALIZATION;
	}

	@Override
	public boolean isEnableModificationData() {

		return false;
	}

	@Override
	public boolean isOnlySelected() {

		return false;
	}

	@Override
	public void process(ISamples samples, IProgressMonitor monitor) {

	}

	@Override
	public void setEnableModificationData(boolean enable) {

	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

	}
}
