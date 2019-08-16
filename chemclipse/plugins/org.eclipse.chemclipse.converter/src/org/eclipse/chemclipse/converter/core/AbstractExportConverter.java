/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public abstract class AbstractExportConverter implements IExportConverter {

	@Override
	public IProcessingInfo<File> validate(File file) {

		/*
		 * When no file is given.
		 */
		if(file == null) {
			return getProcessingInfo("The file couldn't be found.");
		}
		IProcessingInfo<File> processingInfo = createDirectoriesAndFiles(file);
		/*
		 * Tests if the file can be written.
		 */
		if(!file.canWrite()) {
			return getProcessingInfo("The file is not writeable: " + file.getAbsolutePath());
		}
		return processingInfo;
	}

	/**
	 * Creates missing directories and files.<br/>
	 * If a failure occurs an exception will be thrown.
	 *
	 * @param file
	 * @throws FileIsNotWriteableException
	 * @throws IOException
	 */
	private IProcessingInfo<File> createDirectoriesAndFiles(final File file) {

		/*
		 * Take a look if the given file is a directory or a file.<br/> Check
		 * that all directories exists. If not create them and the in case of a
		 * file also the file.
		 */
		if(file.isDirectory()) {
			if(!file.exists() && !file.mkdirs()) {
				return getProcessingInfo("The given directory couldn't be created: " + file.getAbsolutePath());
			}
		} else {
			File newFile = new File(file.getParentFile().getAbsolutePath());
			if(!newFile.exists() && !newFile.mkdirs()) {
				return getProcessingInfo("The given directory couldn't be created: " + file.getAbsolutePath());
			}
			try {
				/*
				 * Each file will be created, hence each converter is responsible to set its correct file extension.
				 */
				if(!file.exists() && !file.createNewFile()) {
					return getProcessingInfo("The given file couldn't be created:" + file.getAbsolutePath());
				}
			} catch(IOException e) {
				return getProcessingInfo("The given file couldn't be created:" + file.getAbsolutePath());
			}
		}
		return new ProcessingInfo<>();
	}

	private IProcessingInfo<File> getProcessingInfo(String message) {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Export Converter", message);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
