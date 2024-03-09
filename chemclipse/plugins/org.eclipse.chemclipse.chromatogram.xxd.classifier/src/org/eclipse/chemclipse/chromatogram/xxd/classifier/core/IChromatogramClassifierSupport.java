/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.classifier.exceptions.NoChromatogramClassifierSupplierAvailableException;

public interface IChromatogramClassifierSupport {

	String getClassifierId(int index) throws NoChromatogramClassifierSupplierAvailableException;

	IChromatogramClassifierSupplier getClassifierSupplier(String classifierId) throws NoChromatogramClassifierSupplierAvailableException;

	List<String> getAvailableClassifierIds() throws NoChromatogramClassifierSupplierAvailableException;

	String[] getClassifierNames() throws NoChromatogramClassifierSupplierAvailableException;
}
