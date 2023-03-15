/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.result.IWncClassifierResult;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.jface.viewers.IStructuredContentProvider;

public class WncResultsContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof IMeasurementResult) {
			IMeasurementResult<?> measurementResult = (IMeasurementResult<?>)inputElement;
			Object object = measurementResult.getResult();
			if(object instanceof IWncClassifierResult result) {
				IWncIons wncIons = result.getWNCIons();
				return wncIons.toArray();
			}
		} else if(inputElement instanceof IWncIons wncIons) {
			return wncIons.toArray();
		}
		//
		return new Object[0];
	}
}
