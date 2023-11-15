/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.converter.chromatogram.model;

import java.io.File;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IFileContentMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.tsd.converter.core.IExportConverterTSD;
import org.eclipse.chemclipse.tsd.converter.core.IImportConverterTSD;
import org.eclipse.chemclipse.tsd.converter.service.IConverterServiceTSD;
import org.eclipse.chemclipse.tsd.model.core.TypeTSD;

public class ConverterAdapterServiceTSD implements IConverterServiceTSD {

	private ISupplier supplier;
	private TypeTSD typeTSD;

	public ConverterAdapterServiceTSD(ISupplier supplier, TypeTSD typeTSD) {

		this.supplier = supplier;
		this.typeTSD = typeTSD;
	}

	@Override
	public IProcessSettings getProcessSettings() {

		return null;
	}

	@Override
	public IMagicNumberMatcher getMagicNumberMatcher() {

		return new AbstractMagicNumberMatcher() {

			@Override
			public boolean checkFileFormat(File file) {

				return checkFileExtension(file, supplier.getFileExtension());
			}
		};
	}

	@Override
	public IImportConverterTSD getImportConverter() {

		return new ChromatogramAdapterConverter(typeTSD);
	}

	@Override
	public String getId() {

		return supplier.getId() + ".tsd.adapter";
	}

	@Override
	public String getFilterName() {

		return supplier.getFilterName() + " [TSD Adapter]";
	}

	@Override
	public String getFileName() {

		return supplier.getFileName();
	}

	@Override
	public String getFileExtension() {

		return supplier.getFileExtension();
	}

	@Override
	public IFileContentMatcher getFileContentMatcher() {

		return new IFileContentMatcher() {

			@Override
			public boolean checkFileFormat(File file) {

				return supplier.isMatchContent(file);
			}
		};
	}

	@Override
	public IExportConverterTSD getExportConverter() {

		/*
		 * Export is not supported.
		 */
		return null;
	}

	@Override
	public String getDirectoryExtension() {

		return supplier.getDirectoryExtension();
	}

	@Override
	public String getDescription() {

		return supplier.getDescription();
	}
}