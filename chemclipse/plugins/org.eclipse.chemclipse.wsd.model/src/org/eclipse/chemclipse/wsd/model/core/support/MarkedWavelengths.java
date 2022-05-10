/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add inclusive/exclusive mode
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.MarkedTraceModus;

public class MarkedWavelengths extends AbstractMarkedWavelengths implements IMarkedWavelengths {

	private MarkedTraceModus markedTraceModus = MarkedTraceModus.INCLUDE;

	public MarkedWavelengths() {

	}

	public MarkedWavelengths(MarkedTraceModus markedTraceModus) {

		this.markedTraceModus = markedTraceModus;
	}

	public MarkedWavelengths(Collection<? extends Number> wavelengths, MarkedTraceModus markedTraceModus) {

		super(wavelengths);
		this.markedTraceModus = markedTraceModus;
	}

	@Override
	public MarkedTraceModus getMarkedTraceModus() {

		return markedTraceModus;
	}
}