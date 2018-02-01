/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.SingularOps;

public class PcaCalculatorSvd extends AbstractPcaCalculator {

	@Override
	public void compute(int numComps) {

		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(sampleData.numRows, sampleData.numCols, false, true, false);
		svd.decompose(sampleData);
		loadings = svd.getV(null, true);
		DenseMatrix64F W = svd.getW(null);
		SingularOps.descendingOrder(null, false, W, loadings, true);
		loadings.reshape(numComps, mean.length, true);
	}
}
