/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public abstract class AbstractPreprocessing implements IPreprocessing {

	private boolean onlySelected;

	protected AbstractPreprocessing() {

		this.onlySelected = true;
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected;
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}

	protected <V extends IVariable, S extends ISample> List<S> selectSamples(ISamples<V, S> samples) {

		return samples.getSampleList().stream().filter(s -> s.isSelected() || !onlySelected).toList();
	}
}
