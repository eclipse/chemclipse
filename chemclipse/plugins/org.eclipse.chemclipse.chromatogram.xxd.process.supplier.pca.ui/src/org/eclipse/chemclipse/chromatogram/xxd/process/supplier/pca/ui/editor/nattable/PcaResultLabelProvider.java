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

	private TableProvider tableProvider;

	public PcaResultLabelProvider(TableProvider tableProvider) {
		this.tableProvider = tableProvider;
	}

	@Override
	public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {

		configLabels.addLabel(tableProvider.getColumnLable(columnPosition));
	}

	@Override
	public Collection<String> getProvidedLabels() {

		Collection<String> result = new HashSet<String>();
		result.add(TableProvider.COLUMN_LABEL_GROUP_DATA);
		result.add(TableProvider.COLUMN_LABEL_RETENTION_TIMES);
		result.add(TableProvider.COLUMN_LABEL_SAMPLE_DATA);
		result.add(TableProvider.COLUMN_LABEL_PEAKS_NAMES);
		return result;
	}
}
