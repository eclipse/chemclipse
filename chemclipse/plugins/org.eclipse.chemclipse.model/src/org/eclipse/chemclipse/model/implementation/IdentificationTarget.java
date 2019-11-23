/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add delegate contructor
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.AbstractIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

public class IdentificationTarget extends AbstractIdentificationTarget implements IIdentificationTarget {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 4894831489940672007L;

	public IdentificationTarget(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {
		this(libraryInformation, comparisonResult, null);
	}

	public IdentificationTarget(ILibraryInformation libraryInformation, IComparisonResult comparisonResult, String identifier) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult);
		if(identifier != null) {
			setIdentifier(identifier);
		}
	}
}
