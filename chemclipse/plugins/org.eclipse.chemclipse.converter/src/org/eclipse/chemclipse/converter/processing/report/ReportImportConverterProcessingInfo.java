/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.processing.report;

import org.eclipse.chemclipse.converter.model.IReportRowModel;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class ReportImportConverterProcessingInfo extends AbstractProcessingInfo implements IReportImportConverterProcessingInfo {

	@Override
	public IReportRowModel getReportRowModel() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IReportRowModel) {
			return (IReportRowModel)object;
		} else {
			throw createTypeCastException("Report Import Converter", object.getClass(), IReportRowModel.class);
		}
	}

	@Override
	public void setReportRowModel(IReportRowModel reportRowModel) {

		setProcessingResult(reportRowModel);
	}
}
