/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.ISavitzkyGolayFilterRating;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ClassifierResultTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof ISavitzkyGolayFilterRating && e2 instanceof ISavitzkyGolayFilterRating) {
			ISavitzkyGolayFilterRating result1 = (ISavitzkyGolayFilterRating)e1;
			ISavitzkyGolayFilterRating result2 = (ISavitzkyGolayFilterRating)e2;
			switch(getPropertyIndex()) {
				case 0: // Rating
					sortOrder = Double.compare(result2.getRating(), result1.getRating());
					break;
				case 1: // Derivative
					sortOrder = result2.getFilterSettings().getDerivative() - result1.getFilterSettings().getDerivative();
					break;
				case 2: // Order
					sortOrder = result2.getFilterSettings().getOrder() - result1.getFilterSettings().getOrder();
					break;
				case 3: // Width
					sortOrder = result2.getFilterSettings().getWidth() - result1.getFilterSettings().getWidth();
					break;
			}
			if(getDirection() == ASCENDING) {
				sortOrder = -sortOrder;
			}
		}
		return sortOrder;
	}
}
