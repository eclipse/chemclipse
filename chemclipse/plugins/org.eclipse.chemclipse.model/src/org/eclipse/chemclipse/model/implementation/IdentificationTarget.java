/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add delegate constructor, add support for adding a libraryscan
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.AbstractIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.core.runtime.IAdaptable;

public class IdentificationTarget extends AbstractIdentificationTarget implements IIdentificationTarget, IAdaptable {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 4894831489940672007L;
	private IScan libraryScan;

	public IdentificationTarget(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {

		this(libraryInformation, comparisonResult, null);
	}

	public IdentificationTarget(ILibraryInformation libraryInformation, IComparisonResult comparisonResult, String identifier) throws ReferenceMustNotBeNullException {

		super(libraryInformation, comparisonResult);
		if(identifier != null) {
			setIdentifier(identifier);
		}
	}

	/**
	 * Set the scan that was used to identify the target in the library if available
	 * 
	 * @param libraryScan
	 */
	public void setLibraryScan(IScan libraryScan) {

		this.libraryScan = libraryScan;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if(adapter.isInstance(libraryScan)) {
			return adapter.cast(libraryScan);
		}
		return null;
	}

	@Override
	public IIdentificationTarget makeDeepCopy() {

		return new IdentificationTarget(getLibraryInformation(), getComparisonResult(), getIdentifier());
	}
}
