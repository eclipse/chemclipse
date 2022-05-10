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
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

import org.eclipse.chemclipse.model.core.AbstractMarkedTrace;

public class MarkedWavelength extends AbstractMarkedTrace implements IMarkedWavelength {

	public MarkedWavelength(double trace, int magnification) {

		super(trace, magnification);
	}

	public MarkedWavelength(double trace) {

		super(trace);
	}

	@Override
	public int castTrace() {

		return (int)Math.round(getTrace());
	}
}