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
package org.eclipse.chemclipse.converter.io;

import java.io.File;

public interface IFileHelper {

	/**
	 * Returns the file directory.
	 * 
	 * @param file
	 * @return String
	 */
	String getBaseFileDirectory(File file);

	/**
	 * Returns the base file name:
	 * G160113E.DAT_001;1 -> G160113E
	 * 
	 * @param file
	 * @return String
	 */
	String getBaseFileName(File file);
}
