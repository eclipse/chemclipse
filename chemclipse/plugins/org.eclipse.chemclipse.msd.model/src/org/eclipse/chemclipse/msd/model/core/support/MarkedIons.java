/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for IonMarkMode
 * Alexander Kerner - add constructor
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.chemclipse.model.core.MarkedTraceModus;

public class MarkedIons extends AbstractMarkedIons implements IMarkedIons {

	private MarkedTraceModus markedTraceModus = MarkedTraceModus.INCLUDE;

	public MarkedIons(int[] ionsList, MarkedTraceModus markedTraceModus) {

		super(ionsList);
		this.markedTraceModus = markedTraceModus;
	}

	public MarkedIons(Collection<? extends Number> ionsList, MarkedTraceModus markedTraceModus) {

		super(ionsList);
		this.markedTraceModus = markedTraceModus;
	}

	public MarkedIons(MarkedTraceModus markedTraceModus) {

		this(new ArrayList<>(), markedTraceModus);
	}

	@Override
	public MarkedTraceModus getMarkedTraceModus() {

		return markedTraceModus;
	}
}