/*******************************************************************************
 * Copyright (c) 2014, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

public class MarkedTrace extends AbstractMarkedTrace implements IMarkedTrace {

	public MarkedTrace(double trace) {

		super(trace, 1);
	}

	public MarkedTrace(double trace, int magnification) {

		super(trace, magnification);
	}

	@Override
	public int castTrace() {

		return (int)Math.round(getTrace());
	}
}