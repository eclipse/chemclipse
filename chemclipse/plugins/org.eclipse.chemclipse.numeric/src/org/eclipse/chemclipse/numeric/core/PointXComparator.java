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
package org.eclipse.chemclipse.numeric.core;

import java.util.Comparator;

/**
 * This comparator compares the x values of an {@link IPoint} instance.
 * 
 * @author eselmeister
 */
public class PointXComparator implements Comparator<IPoint> {

	@Override
	public int compare(IPoint point1, IPoint point2) {

		if(point1 == null || point2 == null) {
			return 0;
		}
		return Double.compare(point1.getX(), point2.getX());
	}
}
