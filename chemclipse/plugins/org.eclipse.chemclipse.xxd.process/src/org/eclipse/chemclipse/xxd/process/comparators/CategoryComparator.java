/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.comparators;

import java.util.Comparator;

import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;

public class CategoryComparator implements Comparator<IProcessTypeSupplier> {

	@Override
	public int compare(IProcessTypeSupplier supplier1, IProcessTypeSupplier supplier2) {

		return supplier1.getCategory().compareTo(supplier2.getCategory());
	}
}
