/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.processing.converter.ISupplier;

public interface IMethodConverterSupport {

	void add(final ISupplier supplier);

	String[] getFilterExtensions() throws NoConverterAvailableException;

	String[] getFilterNames() throws NoConverterAvailableException;

	String getConverterId(int index) throws NoConverterAvailableException;

	String getConverterId(String name) throws NoConverterAvailableException;

	List<String> getAvailableConverterIds(File file) throws NoConverterAvailableException;

	List<ISupplier> getSupplier();

	ISupplier getSupplier(String id) throws NoConverterAvailableException;
}
