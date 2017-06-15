/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelProvider;

public class PcaResultLabelProvider implements IConfigLabelProvider {

	public static final String COLUMN_LABEL_DATA = "COLUMN_DATA";
	public static final String COLUMN_LABEL_RETENTION_TIMES = "COLUMN_NAME";

	public PcaResultLabelProvider() {
	}

	@Override
	public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {

		if(columnPosition == AbstractPcaResulDataProvider.COLUMN_INDEX_RETENTION_TIMES) {
			configLabels.addLabel(COLUMN_LABEL_RETENTION_TIMES);
		} else {
			configLabels.addLabel(COLUMN_LABEL_DATA);
		}
	}

	@Override
	public Collection<String> getProvidedLabels() {

		Collection<String> result = new HashSet<String>();
		result.add(COLUMN_LABEL_DATA);
		result.add(COLUMN_LABEL_RETENTION_TIMES);
		return result;
	}
}
