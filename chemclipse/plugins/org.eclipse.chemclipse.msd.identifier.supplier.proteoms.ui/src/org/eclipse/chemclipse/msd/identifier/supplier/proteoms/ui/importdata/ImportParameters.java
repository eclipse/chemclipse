/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata;

import org.eclipse.core.resources.IProject;

public class ImportParameters {

	private FILE_FORMAT fileFormat = FILE_FORMAT.MFG_APPLIED_BIOSYSTEM;
	private ENZYME enzyme = ENZYME.TRYPSIN;
	private DERIVATIZATION_REAGENTS derivatiozationReagents = DERIVATIZATION_REAGENTS.NONE;
	private String mgfDirectoryPath;
	private IProject project;

	public interface ParameterInfo {
	}

	public FILE_FORMAT getFileFormat() {

		return fileFormat;
	}

	public void setFileFormat(FILE_FORMAT fileFormat) {

		this.fileFormat = fileFormat;
	}

	public ENZYME getEnzyme() {

		return enzyme;
	}

	public void setEnzyme(ENZYME enzyme) {

		this.enzyme = enzyme;
	}

	public DERIVATIZATION_REAGENTS getDerivatiozationReagents() {

		return derivatiozationReagents;
	}

	public void setDerivatiozationReagents(DERIVATIZATION_REAGENTS derivatiozationReagents) {

		this.derivatiozationReagents = derivatiozationReagents;
	}

	public String getMgfDirectoryPath() {

		return mgfDirectoryPath;
	}

	public void setMgfDirectoryPath(String mgfDirectoryPath) {

		this.mgfDirectoryPath = mgfDirectoryPath;
	}

	public IProject getProject() {

		return project;
	}

	public void setProject(IProject project) {

		this.project = project;
	}

	public enum FILE_FORMAT implements ParameterInfo {
		MFG_APPLIED_BIOSYSTEM("MGF", "MALDI TOF/TOF (Applied Biosystems");

		private String description;
		private String formatName;

		private FILE_FORMAT(String formatName, String description) {
			this.formatName = formatName;
			this.description = description;
		}

		public String getFormatName() {

			return formatName;
		}

		public String getDescription() {

			return description;
		}
	}

	public enum ENZYME {
		TRYPSIN("Trypsin", "Trypsin enzyme");

		private String nameEnzyme;
		private String description;

		private ENZYME(String name, String description) {
			this.nameEnzyme = name;
			this.description = description;
		}

		public String getNameEnzyme() {

			return nameEnzyme;
		}

		public String getDescription() {

			return description;
		}
	}

	public enum DERIVATIZATION_REAGENTS {
		CAF_CAF("CAF/CAF", "CAF/CAF derivatization reagents"), NONE("None", "None");

		private String description;
		private String nameDerivatizationReagents;

		private DERIVATIZATION_REAGENTS(String name, String description) {
			this.nameDerivatizationReagents = name;
			this.description = description;
		}

		public String getNameDerivatizationReagents() {

			return nameDerivatizationReagents;
		}

		public String getDescription() {

			return description;
		}
	}
}
