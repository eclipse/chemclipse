/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - use generic interface
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.comparators;

import java.util.Comparator;

import org.eclipse.chemclipse.xxd.process.support.IProcessSupplier;

public class NameComparator implements Comparator<IProcessSupplier> {

	@Override
	public int compare(IProcessSupplier supplier1, IProcessSupplier supplier2) {

		return supplier1.getName().compareTo(supplier2.getName());
	}
}
