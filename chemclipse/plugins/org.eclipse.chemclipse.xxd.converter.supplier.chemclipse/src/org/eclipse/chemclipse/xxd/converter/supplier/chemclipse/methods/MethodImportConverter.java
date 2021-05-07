/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add stream support
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.methods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.chemclipse.converter.core.AbstractImportConverter;
import org.eclipse.chemclipse.converter.methods.IMethodImportConverter;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.IMethodReader;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodReaderWriter_1003;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodReaderWriter_1004;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodReaderWriter_1401;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodReader_1000;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodReader_1001;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class MethodImportConverter extends AbstractImportConverter implements IMethodImportConverter {

	private static final IMethodReader[] READER = new IMethodReader[]{ //
			new MethodReaderWriter_1401(), //
			new MethodReaderWriter_1004(), //
			new MethodReaderWriter_1003(), //
			new MethodReader_1001(), //
			new MethodReader_1000() //
	};

	@Override
	public IProcessingInfo<IProcessMethod> convert(File file, IProgressMonitor monitor) throws IOException {

		SubMonitor subMonitor = SubMonitor.convert(monitor, READER.length * 100);
		for(IMethodReader reader : READER) {
			ProcessingInfo<IProcessMethod> info = new ProcessingInfo<IProcessMethod>();
			IProcessMethod processMethod = reader.convert(file, info, subMonitor.split(100));
			if(processMethod != null) {
				info.setProcessingResult(processMethod);
				return info;
			}
		}
		ProcessingInfo<IProcessMethod> info = new ProcessingInfo<>();
		info.addErrorMessage("Method Converter (*.ocm)", "No avaiable format could read: " + file);
		return info;
	}

	@Override
	public IProcessingInfo<IProcessMethod> readFrom(InputStream stream, String nameHint, IProgressMonitor monitor) throws IOException {

		SubMonitor subMonitor = SubMonitor.convert(monitor, READER.length * 100);
		for(IMethodReader reader : READER) {
			ProcessingInfo<IProcessMethod> info = new ProcessingInfo<IProcessMethod>();
			IProcessMethod processMethod = reader.convert(stream, nameHint, info, subMonitor.split(100));
			if(processMethod != null) {
				info.setProcessingResult(processMethod);
				return info;
			}
		}
		ProcessingInfo<IProcessMethod> info = new ProcessingInfo<>();
		info.addErrorMessage("Method Converter (*.ocm)", "No avaiable format could read: " + nameHint);
		return info;
	}
}
