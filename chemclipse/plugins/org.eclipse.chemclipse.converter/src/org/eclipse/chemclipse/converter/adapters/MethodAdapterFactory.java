/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.adapters;

import java.io.File;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * This AdapterFactory allows for adaption of resource files to {@link ProcessMethod}s, so one can simply call Adapters#adapt(eclipseResource, ProcessMethod.class)} to read a process method stored inside Eclipse resource tree
 *
 * @author Christoph Läubrich
 *
 */
public class MethodAdapterFactory implements IAdapterFactory {

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {

		if(adapterType == IProcessMethod.class) {
			if(adaptableObject instanceof IFile resourceFile) {
				IPath location = resourceFile.getLocation();
				if(location != null) {
					File localfile = location.toFile();
					if(localfile.exists()) {
						return adapterType.cast(convertFile(localfile));
					}
				}
			} else if(adaptableObject instanceof File file) {
				return adapterType.cast(convertFile(file));
			}
		}
		return null;
	}

	public IProcessMethod convertFile(File localfile) {

		IProcessingInfo<IProcessMethod> processingInfo = MethodConverter.convert(localfile, new NullProgressMonitor());
		if(processingInfo != null) {
			return processingInfo.getProcessingResult();
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {

		return new Class<?>[]{IProcessMethod.class};
	}
}
