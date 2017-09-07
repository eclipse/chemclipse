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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDataModification {

	static void resetData(IPcaResults pcaResults) {

		for(ISample sample : pcaResults.getSampleList()) {
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = d.getData();
				d.setNormalizedData(data);
			});
		}
	}

	boolean isEnableModificationData();

	boolean isOnlySelected();

	void process(IPcaResults pcaResults, IProgressMonitor monitor);

	void setEnableModificationData(boolean enable);

	void setOnlySelected(boolean onlySelected);
}
