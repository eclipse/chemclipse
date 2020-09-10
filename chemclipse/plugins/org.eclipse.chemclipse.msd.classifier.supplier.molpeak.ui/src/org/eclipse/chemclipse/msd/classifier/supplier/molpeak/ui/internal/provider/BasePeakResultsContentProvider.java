/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.ui.internal.provider;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.ILigninRatios;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class BasePeakResultsContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof IMeasurementResult) {
			IMeasurementResult<?> measurementResult = (IMeasurementResult<?>)inputElement;
			Object object = measurementResult.getResult();
			if(object instanceof ILigninRatios) {
				ILigninRatios ligninRatios = (ILigninRatios)object;
				return ligninRatios.getResults().entrySet().toArray();
			}
		} else if(inputElement instanceof ILigninRatios) {
			ILigninRatios ligninRatios = (ILigninRatios)inputElement;
			return ligninRatios.getResults().entrySet().toArray();
		}
		//
		return null;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}
}
