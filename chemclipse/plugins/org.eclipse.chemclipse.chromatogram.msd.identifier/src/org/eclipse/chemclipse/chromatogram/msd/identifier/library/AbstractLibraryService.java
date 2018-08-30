/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.library;

import org.eclipse.chemclipse.model.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public abstract class AbstractLibraryService implements ILibraryService {

	public void validateIdentificationTarget(IIdentificationTarget identificationTarget) throws ValueMustNotBeNullException {

		if(identificationTarget == null) {
			throw new ValueMustNotBeNullException("The identification target must not be null.");
		}
	}
}
