/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for IonMarkMode
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

public class MarkedIons extends AbstractMarkedIons implements IMarkedIons {

	private IonMarkMode mode;

	@Deprecated
	public MarkedIons(int[] ionsList) {
		this(ionsList, IonMarkMode.INCLUDE);
	}

	@Deprecated
	public MarkedIons() {
		this(null, IonMarkMode.INCLUDE);
	}

	public MarkedIons(int[] ionsList, IonMarkMode mode) {
		super(ionsList);
		this.mode = mode;
	}

	public MarkedIons(IonMarkMode mode) {
		this(null, mode);
	}

	@Override
	public IonMarkMode getMode() {

		return mode;
	}
}
