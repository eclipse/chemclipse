/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;

import javafx.beans.Observable;
import javafx.util.Callback;

public interface IPcaResultVisualization extends IPcaResult, IColor {

	static Callback<IPcaResultVisualization, Observable[]> extractor() {

		return (IPcaResultVisualization r) -> new Observable[]{r.getSample().selectedProperty(), r.colorProperty()};
	}

	@Override
	ISampleVisualization getSample();
}
