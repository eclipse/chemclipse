/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.internal.provider;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.support.ui.processors.Processor;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ProcessorTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof Processor && e2 instanceof Processor) {
			//
			Processor processor1 = (Processor)e1;
			Processor processor2 = (Processor)e2;
			IProcessSupplier<?> processSupplier1 = processor1.getProcessSupplier();
			IProcessSupplier<?> processSupplier2 = processor2.getProcessSupplier();
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = processSupplier2.getName().compareTo(processSupplier1.getName());
					break;
				case 1:
					sortOrder = ProcessorLabelProvider.getDataTypes(processSupplier2.getSupportedDataTypes()).compareTo(ProcessorLabelProvider.getDataTypes(processSupplier1.getSupportedDataTypes()));
					break;
				case 2:
					sortOrder = processSupplier2.getCategory().compareTo(processSupplier1.getCategory());
					break;
				case 3:
					sortOrder = processSupplier2.getDescription().compareTo(processSupplier1.getDescription());
					break;
				case 4:
					sortOrder = processSupplier2.getId().compareTo(processSupplier1.getId());
					break;
				default:
					sortOrder = 0;
			}
		}
		//
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		//
		return sortOrder;
	}
}
