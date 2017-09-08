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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDataModification {

	static void resetData(ISamples samples) {

		for(ISample sample : samples.getSampleList()) {
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = d.getData();
				d.setModifiedData(data);
			});
		}
	}

	boolean isEnableModificationData();

	boolean isOnlySelected();

	void process(ISamples samples, IProgressMonitor monitor);

	void setEnableModificationData(boolean enable);

	void setOnlySelected(boolean onlySelected);
}
