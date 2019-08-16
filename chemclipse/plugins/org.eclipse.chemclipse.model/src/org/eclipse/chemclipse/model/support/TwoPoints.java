/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.exceptions.PointIsNullException;

public class TwoPoints implements ITwoPoints {

	private IPoint p1;
	private IPoint p2;

	public TwoPoints(IPoint p1, IPoint p2) throws PointIsNullException {
		if(p1 == null || p2 == null) {
			throw new PointIsNullException("The points p1 and p2 must not be null.");
		}
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public LinearEquation getLinearEquation() {

		return Equations.createLinearEquation(p1, p2);
	}

	@Override
	public IPoint getPoint1() {

		return p1;
	}

	@Override
	public IPoint getPoint2() {

		return p2;
	}

	@Override
	public double getSlope() {

		return Equations.calculateSlope(p1, p2);
	}
}
