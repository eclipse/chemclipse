/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.traces;

public class TraceGenericDelta extends AbstractTrace {

	private double delta = 0.0d;

	public boolean isUseRange() {

		return delta > 0;
	}

	public double getDelta() {

		return delta;
	}

	/**
	 * Delta must be >= 0.
	 * 
	 * @param delta
	 * @return {@link TraceGenericDelta}
	 */
	public TraceGenericDelta setDelta(double delta) {

		if(delta >= 0) {
			this.delta = delta;
		}
		return this;
	}
}