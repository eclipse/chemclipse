/*******************************************************************************
 * Copyright (c) 2018 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVaribleExtracted;

import javafx.beans.Observable;
import javafx.util.Callback;

public interface IVariableExtractedVisalization extends IVariableVisualization, IVaribleExtracted {

	static Callback<IVariableExtractedVisalization, Observable[]> extractor() {

		return (IVariableExtractedVisalization v) -> new Observable[]{v.colorProperty(), v.getVariableOrigin().selectedProperty()};
	}

	@Override
	IVariableVisualization getVariableOrigin();
}
