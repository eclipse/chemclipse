/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public abstract class AbstractDataModificator extends AbstractPreprocessing implements IDataModificator {

	private boolean modifyOnlySelectedVariable;
	private boolean removeUselessVariables;

	public AbstractDataModificator() {

		this.modifyOnlySelectedVariable = false;
	}

	protected double getData(ISampleData data) {

		return data.getModifiedData();
	}

	@Override
	public boolean isModifyOnlySelectedVariable() {

		return modifyOnlySelectedVariable;
	}

	@Override
	public void setModifyOnlySelectedVariable(boolean b) {

		this.modifyOnlySelectedVariable = b;
	}

	@Override
	public boolean isRemoveUselessVariables() {

		return removeUselessVariables;
	}

	@Override
	public void setRemoveUselessVariables(boolean b) {

		this.removeUselessVariables = b;
	}

	protected <V extends IVariable, S extends ISample> boolean skipVariable(ISamples<V, S> samples, int order) {

		boolean b = (modifyOnlySelectedVariable && !samples.getVariables().get(order).isSelected()) || //
				(modifyOnlySelectedVariable && removeUselessVariables && !samples.selectVariable(order));
		// System.out.println(order + " - " + b);
		return b;
	}
}
