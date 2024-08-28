/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public abstract class AbstractDataModificator extends AbstractPreprocessing implements IDataModificator {

	protected double getData(ISampleData<?> sampleData) {

		return sampleData.getModifiedData();
	}

	protected <V extends IVariable, S extends ISample> boolean useVariable(ISamples<V, S> samples, int row) {

		return samples.getVariables().get(row).isSelected() && samples.containsValidData(row);
	}
}