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

import org.eclipse.chemclipse.model.statistics.ISample;

public class SampleVisualization extends AbstractSampleVisualization {

	public SampleVisualization(ISample sampleModel) {

		super(sampleModel);
	}

	public SampleVisualization(ISample sampleModel, int r, int g, int b, double alpha) {

		super(sampleModel, r, g, b, alpha);
	}
}
