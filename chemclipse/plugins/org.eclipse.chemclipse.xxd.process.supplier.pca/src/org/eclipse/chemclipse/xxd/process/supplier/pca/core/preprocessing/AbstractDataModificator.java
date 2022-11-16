/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
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

	private boolean modifyOnlySelectedVariable = false;
	private boolean removeUselessVariables = true;

	@SuppressWarnings("rawtypes")
	protected double getData(ISampleData data) {

		return data.getModifiedData();
	}

	@Override
	public boolean isModifyOnlySelectedVariable() {

		return modifyOnlySelectedVariable;
	}

	@Override
	public void setModifyOnlySelectedVariable(boolean modifyOnlySelectedVariable) {

		this.modifyOnlySelectedVariable = modifyOnlySelectedVariable;
	}

	@Override
	public boolean isRemoveUselessVariables() {

		return removeUselessVariables;
	}

	@Override
	public void setRemoveUselessVariables(boolean removeUselessVariables) {

		this.removeUselessVariables = removeUselessVariables;
	}

	protected <V extends IVariable, S extends ISample> boolean skipVariable(ISamples<V, S> samples, int order) {

		return (modifyOnlySelectedVariable && !samples.getVariables().get(order).isSelected()) || (modifyOnlySelectedVariable && removeUselessVariables && !samples.selectVariable(order));
	}
}
