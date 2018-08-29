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
package org.eclipse.chemclipse.nmr.processor.core;

import java.util.List;

import org.eclipse.chemclipse.nmr.processor.exceptions.NoProcessorAvailableException;

public interface IScanProcessorSupport extends List<IScanProcessorSupplier> {

	String[] getProcessorNames() throws NoProcessorAvailableException;

	String getProcessorId(int index) throws NoProcessorAvailableException;

	String getProcessorId(String name) throws NoProcessorAvailableException;

	List<String> getAvailableProcessorIds() throws NoProcessorAvailableException;

	List<IScanProcessorSupplier> getSupplier();

	IScanProcessorSupplier getSupplier(String id) throws NoProcessorAvailableException;
}
