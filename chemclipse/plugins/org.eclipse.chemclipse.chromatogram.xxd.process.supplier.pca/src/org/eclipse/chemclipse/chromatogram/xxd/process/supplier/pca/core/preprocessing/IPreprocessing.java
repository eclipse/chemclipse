/*******************************************************************************
 * Copyright (c) 2017 Jan Holy
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public interface IPreprocessing {

	enum DATA_TYPE_PROCESSING {
		RAW_DATA("Raw Data"), //
		MODIFIED_DATA("Modified Data"), //
		VARIABLES("Variables");

		private String string;

		private DATA_TYPE_PROCESSING(String string) {
			this.string = string;
		}

		@Override
		public String toString() {

			return string;
		}
	}

	String getDescription();

	String getName();

	boolean isOnlySelected();

	<V extends IVariable, S extends ISample<? extends ISampleData>> void process(ISamples<V, S> samples);

	void setOnlySelected(boolean onlySelected);

	void setDataTypeProcessing(DATA_TYPE_PROCESSING processType);

	DATA_TYPE_PROCESSING getDataTypeProcessing();
}
