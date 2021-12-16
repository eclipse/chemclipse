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
 * Matthias Mailänder - add default icons
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;

public class Processor {

	private IProcessSupplier<?> processSupplier;
	private boolean active = false;
	private String imageFileName = IApplicationImage.IMAGE_EXECUTE_EXTENSION;

	public Processor(IProcessSupplier<?> processSupplier) {

		this.processSupplier = processSupplier;
		// Quick-Access Toolbar default icons:
		if(processSupplier.getCategory().equals("Baseline Detector")) {
			imageFileName = IApplicationImage.IMAGE_BASELINE;
			if(processSupplier.getName().contains("SNIP")) {
				imageFileName = IApplicationImage.IMAGE_BASELINE_SNIP;
			} else if(processSupplier.getName().contains("Delete")) {
				imageFileName = IApplicationImage.IMAGE_BASELINE_DELETE;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Calculator")) {
			imageFileName = IApplicationImage.IMAGE_CALCULATE;
			if(processSupplier.getName().contains("Retention Index Calculator")) {
				imageFileName = IApplicationImage.IMAGE_RETENION_INDEX;
			} else if(processSupplier.getName().contains("Reset")) {
				imageFileName = IApplicationImage.IMAGE_RESET;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Classifier")) {
			imageFileName = IApplicationImage.IMAGE_CLASSIFIER;
			if(processSupplier.getName().contains("WNC")) {
				imageFileName = IApplicationImage.IMAGE_CLASSIFIER_WNC;
			} else if(processSupplier.getName().contains("Durbin-Watson")) {
				imageFileName = IApplicationImage.IMAGE_CLASSIFIER_DW;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Export")) {
			imageFileName = IApplicationImage.IMAGE_SAVE;
			if(processSupplier.getName().contains("*.CAL")) {
				imageFileName = IApplicationImage.IMAGE_SAMPLE_CALIBRATION;
			} else if(processSupplier.getName().contains("Excel")) {
				imageFileName = IApplicationImage.IMAGE_EXCEL_DOCUMENT;
			} else if(processSupplier.getName().contains("ZIP")) {
				imageFileName = IApplicationImage.IMAGE_ZIP_FILE;
			} else if(processSupplier.getName().contains("mzML") || processSupplier.getName().contains("mzXML") || processSupplier.getName().contains("mzData") || processSupplier.getName().contains("AnIML") || processSupplier.getName().contains("GAML")) {
				imageFileName = IApplicationImage.IMAGE_XML_FILE;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Filter")) {
			imageFileName = IApplicationImage.IMAGE_CHROMATOGRAM;
			if(processSupplier.getName().contains("(1:1)")) {
				imageFileName = IApplicationImage.IMAGE_RESET_EQUAL;
			} else if(processSupplier.getName().contains("CODA")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_CODA;
			} else if(processSupplier.getName().contains("Backfolding")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_BACKFOLDING;
			} else if(processSupplier.getName().contains("Denoising")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_DENOISING;
			} else if(processSupplier.getName().contains("Mean Normalizer")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_MEAN_NORMALIZER;
			} else if(processSupplier.getName().contains("Median Normalizer")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_MEDIAN_NORMALIZER;
			} else if(processSupplier.getName().equals("Normalizer")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_NORMALIZER;
			} else if(processSupplier.getName().contains("Savitzky-Golay")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SAVITZKY_GOLAY;
			} else if(processSupplier.getName().contains("Scan Remover")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SCANREMOVER;
			} else if(processSupplier.getName().contains("Subtract")) {
				imageFileName = IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Integrator")) {
			imageFileName = IApplicationImage.IMAGE_CHROMATOGRAM_INTEGRATOR;
			if(processSupplier.getName().contains("Sumarea")) {
				imageFileName = IApplicationImage.IMAGE_INTEGRATOR_SUMAREA;
			}
		} else if(processSupplier.getCategory().equals("Chromatogram Reports")) {
			imageFileName = IApplicationImage.IMAGE_CHROMATOGRAM_REPORT;
		} else if(processSupplier.getCategory().equals("External Programs")) {
			if(processSupplier.getName().contains("NIST")) {
				imageFileName = IApplicationImage.IMAGE_NIST;
			}
		} else if(processSupplier.getCategory().equals("Combined Chromatogram and Peak Integrator")) {
			imageFileName = IApplicationImage.IMAGE_COMBINED_INTEGRATOR;
		} else if(processSupplier.getCategory().equals("Peak Detector")) {
			imageFileName = IApplicationImage.IMAGE_PEAK_DETECTOR;
			if(processSupplier.getName().contains("AMDIS") || processSupplier.getName().contains("MCR-AR")) {
				imageFileName = IApplicationImage.IMAGE_DECONVOLUTION;
			}
		} else if(processSupplier.getCategory().equals("Peak Export")) {
			imageFileName = IApplicationImage.IMAGE_EXPORT;
		} else if(processSupplier.getCategory().equals("Peak Filter")) {
			imageFileName = IApplicationImage.IMAGE_PEAKS;
			if(processSupplier.getName().contains("Add")) {
				imageFileName = IApplicationImage.IMAGE_ADD;
			} else if(processSupplier.getName().contains("Remove")) {
				imageFileName = IApplicationImage.IMAGE_REMOVE;
			} else if(processSupplier.getName().contains("Delete")) {
				imageFileName = IApplicationImage.IMAGE_DELETE;
			}
			if(processSupplier.getName().contains("Delete Peak")) {
				imageFileName = IApplicationImage.IMAGE_DELETE_PEAKS;
			} else if(processSupplier.getName().contains("Delete Integration")) {
				imageFileName = IApplicationImage.IMAGE_DELETE_PEAK_INTEGRATIONS;
			} else if(processSupplier.getName().contains("Delete Target")) {
				imageFileName = IApplicationImage.IMAGE_DELETE_PEAK_IDENTIFICATIONS;
			} else if(processSupplier.getName().contains("SNIP")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SNIP_ALL_PEAKS;
			}
		} else if(processSupplier.getCategory().equals("Peak Identifier")) {
			imageFileName = IApplicationImage.IMAGE_IDENTIFY_PEAKS;
			if(processSupplier.getName().equals("Library File (MS)")) {
				imageFileName = IApplicationImage.IMAGE_MASS_SPECTRUM_LIBRARY;
			} else if(processSupplier.getName().equals("Unknown Marker")) {
				imageFileName = IApplicationImage.IMAGE_UNKNOWN;
			} else if(processSupplier.getName().contains("NIST")) {
				imageFileName = IApplicationImage.IMAGE_NIST_PEAKS;
			}
		} else if(processSupplier.getCategory().equals("Peak Integrator")) {
			imageFileName = IApplicationImage.IMAGE_PEAK_INTEGRATOR;
			if(processSupplier.getName().contains("Peak Max")) {
				imageFileName = IApplicationImage.IMAGE_PEAK_INTEGRATOR_MAX;
			}
		} else if(processSupplier.getCategory().equals("Peak Quantifier")) {
			imageFileName = IApplicationImage.IMAGE_QUANTIFY_ALL_PEAKS;
			if(processSupplier.getName().contains("ISTD")) {
				imageFileName = IApplicationImage.IMAGE_INTERNAL_STANDARDS_DEFAULT;
			} else if(processSupplier.getName().contains("ESTD")) {
				imageFileName = IApplicationImage.IMAGE_EXTERNAL_STANDARDS_DEFAULT;
			}
			if(processSupplier.getName().contains("Add Peaks to DB")) {
				imageFileName = IApplicationImage.IMAGE_ADD_PEAKS_TO_QUANTITATION_TABLE;
			}
		} else if(processSupplier.getCategory().equals("Peak Massspectrum Filter") || processSupplier.getCategory().equals("Scan Massspectrum Filter")) {
			imageFileName = IApplicationImage.IMAGE_MASS_SPECTRUM;
			if(processSupplier.getName().contains("Savitzky-Golay")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SAVITZKY_GOLAY;
			} else if(processSupplier.getName().contains("SNIP")) {
				imageFileName = IApplicationImage.IMAGE_FILTER_SNIP_ALL_PEAKS;
			} else if(processSupplier.getName().contains("Subtract")) {
				imageFileName = IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT;
			}
		} else if(processSupplier.getCategory().equals("Scan Identifier")) {
			imageFileName = IApplicationImage.IMAGE_IDENTIFY_MASS_SPECTRUM;
			if(processSupplier.getName().equals("Library File (MS)")) {
				imageFileName = IApplicationImage.IMAGE_MASS_SPECTRUM_LIBRARY;
			} else if(processSupplier.getName().equals("Unknown Marker")) {
				imageFileName = IApplicationImage.IMAGE_UNKNOWN;
			} else if(processSupplier.getName().contains("NIST")) {
				imageFileName = IApplicationImage.IMAGE_NIST_MASS_SPECTRUM;
			}
		} else if(processSupplier.getCategory().equals("System")) {
			imageFileName = IApplicationImage.IMAGE_PREFERENCES;
		}
	}

	public boolean isActive() {

		return active;
	}

	public void setActive(boolean active) {

		this.active = active;
	}

	public String getImageFileName() {

		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {

		if(imageFileName != null && !imageFileName.isEmpty()) {
			this.imageFileName = imageFileName;
		}
	}

	public IProcessSupplier<?> getProcessSupplier() {

		return processSupplier;
	}
}