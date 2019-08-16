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
package org.eclipse.chemclipse.converter.core;

public interface IConverterSupportSetter extends IConverterSupport {

	/**
	 * Adds a ({@link ISupplier}) to the ConverterSupport.
	 * 
	 * @param supplier
	 */
	void add(final ISupplier supplier);
}
