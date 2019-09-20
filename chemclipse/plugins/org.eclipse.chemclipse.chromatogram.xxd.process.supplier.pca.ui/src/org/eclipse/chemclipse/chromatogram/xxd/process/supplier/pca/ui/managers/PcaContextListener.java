/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;

public interface PcaContextListener {

	default void variablesHasBeenUpdated(List<? extends IVariable> list, PcaContext context) {

	}

	default void settingsHasBeenChanged() {

	}

	default void variableSelectionChanged(IVariable variable, PcaContext context) {

	}

	default void sampleSelectionChanged(ISample sampleSelection, PcaContext pcaContext) {

	}

	default void pcaSelectionChanged(IPcaResultsVisualization pcaSelection, PcaContext pcaContext) {

	}
}
