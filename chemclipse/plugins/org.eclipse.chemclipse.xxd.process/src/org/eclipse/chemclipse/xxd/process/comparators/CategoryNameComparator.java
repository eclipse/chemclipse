/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.comparators;

import java.util.Comparator;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;

public class CategoryNameComparator implements Comparator<IProcessSupplier<?>> {

	private static final NameComparator NAME = new NameComparator();
	private static final CategoryComparator CATEGORY = new CategoryComparator();

	@Override
	public int compare(IProcessSupplier<?> o1, IProcessSupplier<?> o2) {

		int cmp = CATEGORY.compare(o1, o2);
		if(cmp == 0) {
			cmp = NAME.compare(o1, o2);
		}
		return cmp;
	}
}
