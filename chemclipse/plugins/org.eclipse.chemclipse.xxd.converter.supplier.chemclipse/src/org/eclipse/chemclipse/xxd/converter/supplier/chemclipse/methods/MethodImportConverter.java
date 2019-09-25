/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - improve log output
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.methods;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.methods.AbstractMethodImportConverter;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.IMethodReader;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodReader_1000;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodReader_1001;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class MethodImportConverter extends AbstractMethodImportConverter<IProcessMethod> {

	private static IMethodReader[] READER = new IMethodReader[]{new MethodReader_1001(), new MethodReader_1000()};

	@Override
	public IProcessingInfo<IProcessMethod> convert(File file, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, READER.length * 100);
		try {
			for(IMethodReader reader : READER) {
				IProcessMethod processMethod = reader.convert(file, subMonitor.split(100));
				if(processMethod != null) {
					return new ProcessingInfo<IProcessMethod>(processMethod);
				}
			}
		} catch(IOException e) {
			ProcessingInfo<IProcessMethod> info = new ProcessingInfo<>();
			info.addErrorMessage("Method Converter (*.ocm)", "Something has gone wrong to read the file: " + file, e);
			return info;
		}
		ProcessingInfo<IProcessMethod> info = new ProcessingInfo<>();
		info.addErrorMessage("Method Converter (*.ocm)", "No avaiable converter could read: " + file);
		return info;
	}
}
