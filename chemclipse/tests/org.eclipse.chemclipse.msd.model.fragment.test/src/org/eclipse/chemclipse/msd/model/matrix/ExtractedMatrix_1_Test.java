package org.eclipse.chemclipse.msd.model.matrix;


import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanIon;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

import junit.framework.TestCase;

public class ExtractedMatrix_1_Test extends TestCase {
	
	private IVendorMassSpectrum supplierMassSpectrum;
	private IScanIon defaultIon;
	private IChromatogramMSD chromatogram;
	
	@Override
	protected void setUp() throws Exception {

		int scans = 120;
		int ionStart = 25;
		int ionStop = 30;
		/*
		 * No empty scans.
		 */
		chromatogram = new ChromatogramMSD();
		for(int scan = 1; scan <= scans; scan++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			supplierMassSpectrum.setRetentionTime(scan);
			supplierMassSpectrum.setRetentionIndex(scan / 60.0f);
			for(int ion = ionStart; ion <= ionStop; ion++) {
				defaultIon = new ScanIon(ion, ion * scan);
				supplierMassSpectrum.addIon(defaultIon);
			}
			chromatogram.addScan(supplierMassSpectrum);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;

		super.tearDown();
	}
	
	public void testConstructor_1() {

		try {
			ChromatogramSelectionMSD selection = new ChromatogramSelectionMSD(chromatogram);
			ExtractedMatrix extracted = new ExtractedMatrix(selection);
			assertNotNull(extracted);	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
