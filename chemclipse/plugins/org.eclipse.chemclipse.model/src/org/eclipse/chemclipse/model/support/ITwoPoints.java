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
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public interface ITwoPoints {

	/**
	 * Returns the first point.
	 * 
	 * @return IPoint
	 */
	IPoint getPoint1();

	/**
	 * Returns the second point.
	 * 
	 * @return IPoint
	 */
	IPoint getPoint2();

	/**
	 * Returns a linear equation, which can describe the two points.
	 * 
	 * @return {@link LinearEquation}
	 */
	LinearEquation getLinearEquation();

	/**
	 * Returns the slope between both points.
	 * 
	 * @return double
	 */
	double getSlope();
}
