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

public abstract class AbstractTrace implements ITrace {

	private double value = 0.0d;
	private double scaleFactor = 1.0d;

	@Override
	public double getValue() {

		return value;
	}

	@Override
	public AbstractTrace setValue(double value) {

		if(value >= 0) {
			this.value = value;
		}
		return this;
	}

	@Override
	public double getScaleFactor() {

		return scaleFactor;
	}

	@Override
	public AbstractTrace setScaleFactor(double scaleFactor) {

		if(scaleFactor > 0) {
			this.scaleFactor = scaleFactor;
		}
		return this;
	}

	protected int getValueAsInt() {

		return (int)(Math.round(value));
	}
}