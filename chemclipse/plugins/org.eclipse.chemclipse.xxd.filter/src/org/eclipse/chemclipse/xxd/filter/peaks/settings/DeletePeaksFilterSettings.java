/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks.settings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.settings.AbstractSettingsMigrator;
import org.eclipse.chemclipse.support.settings.ISettingsMigrationHandler;
import org.eclipse.chemclipse.xxd.filter.internal.peaks.settings.DeletePeaksFilterSettings_v1;
import org.eclipse.chemclipse.xxd.filter.internal.peaks.settings.DeletePeaksFilterSettings_v2;
import org.eclipse.chemclipse.xxd.filter.support.DeletePeakOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class DeletePeaksFilterSettings extends AbstractSettingsMigrator<DeletePeaksFilterSettings> {

	@JsonProperty(value = "Delete Peak Option", defaultValue = "NONE")
	@JsonPropertyDescription(value = "Select the peaks to be deleted.")
	private DeletePeakOption deletePeakOption = DeletePeakOption.NONE;

	public DeletePeakOption getDeletePeakOption() {

		return deletePeakOption;
	}

	public void setDeletePeakOption(DeletePeakOption deletePeakOption) {

		this.deletePeakOption = deletePeakOption;
	}

	@Override
	public List<ISettingsMigrationHandler<DeletePeaksFilterSettings>> getSettingsMigrationHandler() {

		List<ISettingsMigrationHandler<DeletePeaksFilterSettings>> migrationHandler = new ArrayList<>();
		migrationHandler.add(new DeletePeaksFilterSettings_v1());
		migrationHandler.add(new DeletePeaksFilterSettings_v2());
		//
		return migrationHandler;
	}

	@Override
	public void transferToLatestVersion(String content) {

		super.transferToLatestVersion(this, content);
	}
}