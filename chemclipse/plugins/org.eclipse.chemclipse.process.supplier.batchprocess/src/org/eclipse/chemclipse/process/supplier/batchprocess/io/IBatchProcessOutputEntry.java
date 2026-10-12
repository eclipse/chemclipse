/*******************************************************************************
 * Copyright (c) 2010, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess.io;

/**
 * @author Dr. Philip Wenig
 * 
 */
public interface IBatchProcessOutputEntry {

	/**
	 * Returns the output folder.
	 * 
	 * @return String
	 */
	String getOutputFolder();

	/**
	 * Returns the converter id.
	 * 
	 * @return String
	 */
	String getConverterId();
}
