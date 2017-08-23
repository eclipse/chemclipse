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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters;

import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;

public interface IFilter {

	List<Boolean> filter(IPcaResults pcaResults);

	String getDescription();

	default String getErrorMessage(String messagge) {

		return "Error: " + messagge;
	}

	String getName();

	default String getNumberSelectedRow(Collection<Boolean> selection) {

		long countSelectedData = selection.stream().filter(b -> b).count();
		return Long.toString(countSelectedData);
	}

	String getSelectionResult();

	boolean isOnlySelected();

	void setOnlySelected(boolean onlySelected);
}
