/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.mzml.io;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.CVParamType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.DataProcessingType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.InstrumentConfigurationType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.MzMLType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ParamGroupType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ProcessingMethodType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SampleListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SampleType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SoftwareType;

public class MetadataReader110 {

	public static IChromatogram<?> readMetadata(MzMLType mzML, IChromatogram<?> chromatogram) {

		for(ParamGroupType contact : mzML.getFileDescription().getContact()) {
			for(CVParamType cvParam : contact.getCvParam()) {
				if(chromatogram.getOperator().isEmpty()) {
					chromatogram.setOperator(cvParam.getValue());
				} else {
					chromatogram.setOperator(String.join(", ", chromatogram.getOperator(), cvParam.getValue()));
				}
			}
		}
		SampleListType sampleList = mzML.getSampleList();
		if(sampleList != null) {
			for(SampleType sample : sampleList.getSample()) {
				if(sample.getName() != null) {
					chromatogram.setSampleName(sample.getName());
				}
			}
		}
		for(InstrumentConfigurationType instrument : mzML.getInstrumentConfigurationList().getInstrumentConfiguration()) {
			for(CVParamType cvParam : instrument.getCvParam()) {
				if(cvParam.getAccession().equals("MS:1000554")) {
					chromatogram.setInstrument(cvParam.getName());
				}
			}
		}
		for(DataProcessingType dataProcessing : mzML.getDataProcessingList().getDataProcessing()) {
			for(ProcessingMethodType processingMethod : dataProcessing.getProcessingMethod()) {
				SoftwareType software = (SoftwareType)processingMethod.getSoftwareRef();
				for(CVParamType cvParam : processingMethod.getCvParam()) {
					String operation = cvParam.getName();
					String editor = software.getId() + " " + software.getVersion();
					chromatogram.getEditHistory().add(new EditInformation(operation, editor));
				}
			}
		}
		return chromatogram;
	}
}
