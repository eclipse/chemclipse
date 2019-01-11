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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;

import javafx.collections.ObservableList;

public interface IPcaResultsVisualization extends IPcaResults<IPcaResultVisualization, IVariableExtractedVisalization> {

	IPcaSettingsVisualization getPcaSettingsVisualization();

	ObservableList<IVariableExtractedVisalization> getExtractedVariables();

	ObservableList<IPcaResultVisualization> getPcaResultList();
}
