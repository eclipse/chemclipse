/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.io.File;

public interface IMagicNumberMatcher {

	/**
	 * Shall be used to quickly identify potential files.
	 * Use IFileContentMatcher for more expensive checks.
	 * 
	 * @param file
	 * @return boolean
	 */
	boolean checkFileFormat(File file);
}
