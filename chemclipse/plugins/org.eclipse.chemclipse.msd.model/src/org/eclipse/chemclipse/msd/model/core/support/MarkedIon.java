/*******************************************************************************
 * Copyright (c) 2014, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import org.eclipse.chemclipse.model.core.AbstractMarkedTrace;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;

public class MarkedIon extends AbstractMarkedTrace implements IMarkedIon {

	public MarkedIon(double trace, int magnification) {

		super(trace, magnification);
	}

	public MarkedIon(double trace) {

		super(trace);
	}

	@Override
	public int castTrace() {

		return AbstractIon.getIon(getTrace());
	}
}