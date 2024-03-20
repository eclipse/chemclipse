/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Janos Binder - small fix with error messages
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.io.File;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractImportConverter implements IImportConverter {

	private static final String DESCRIPTION = "Import Converter";

	/**
	 * This method validates the given file.<br/>
	 * A failure will be thrown if the file is not in a valid state.<br/>
	 * It is possible, that there is no file stored on disk or the file is not
	 * readable.<br/>
	 * If the file is empty is handled also.
	 *
	 * @param file
	 * @return {@link IProcessingInfo}
	 */
	public <T> IProcessingInfo<T> validate(File file) {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		if(file == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The given file couldn't be found.");
		} else {
			if(!file.exists()) {
				/*
				 * Of course if the file or directory does not exist, it does not worth to check the else branch
				 */
				processingInfo.addErrorMessage(DESCRIPTION, "The given file doesn't exists: " + file.getAbsolutePath());
			} else {
				/*
				 * Under unix/linux/mac everything is a file.<br/> To check only the
				 * file length causes an exception running on Microsoft Windows if the
				 * file is a directory.<br/> So, we do a short circuit check if the file
				 * is a real file.
				 */
				if(file.isFile() && file.length() == 0) {
					processingInfo.addErrorMessage(DESCRIPTION, "The given file is empty: " + file.getAbsolutePath());
				}
				if(!file.canRead()) {
					processingInfo.addErrorMessage(DESCRIPTION, "The given file is not readable: " + file.getAbsolutePath());
				}
			}
		}
		/*
		 * Everything is ok.
		 */
		return processingInfo;
	}
}