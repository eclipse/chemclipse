/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support;

import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.INistSupport;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IAdaptable;

public class NISTIdentificationTarget extends IdentificationTarget implements IAdaptable {

	private static final long serialVersionUID = 1L;
	private IScanMSD identifiedScan;

	public NISTIdentificationTarget(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {

		super(libraryInformation, comparisonResult);
	}

	@Override
	public String getIdentifier() {

		return INistSupport.NIST_IDENTIFIER;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if(identifiedScan != null && adapter.isInstance(identifiedScan)) {
			return adapter.cast(identifiedScan);
		}
		return null;
	}

	public void setIdentifiedScan(IScanMSD identifiedScan) {

		this.identifiedScan = identifiedScan;
	}
}
