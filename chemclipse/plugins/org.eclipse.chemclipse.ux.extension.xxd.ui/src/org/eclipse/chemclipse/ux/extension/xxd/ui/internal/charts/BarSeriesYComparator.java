/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.util.Comparator;

public class BarSeriesYComparator implements Comparator<BarSeriesValue> {

	@Override
	public int compare(BarSeriesValue barSeriesIon1, BarSeriesValue barSeriesIon2) {

		return Double.compare(barSeriesIon2.getY(), barSeriesIon1.getY());
	}
}
