/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.processing;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class LibraryServiceProcessingInfo extends AbstractProcessingInfo implements ILibraryServiceProcessingInfo {

	@Override
	public IMassSpectra getMassSpectra() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IMassSpectra) {
			return (IMassSpectra)object;
		} else {
			throw createTypeCastException("MassSpectra", object.getClass(), IMassSpectra.class);
		}
	}

	@Override
	public void setMassSpectra(IMassSpectra massSpectra) {

		setProcessingResult(massSpectra);
	}
}
