package geogebra.geogebra3D.web.kernel3D;

import geogebra.common.geogebra3D.kernel3D.Kernel3D;
import geogebra.common.main.App;
import geogebra.html5.kernel.KernelW;

/**
 * for 3D
 * @author mathieu
 *
 */
public class Kernel3DW extends Kernel3D{


	/**
	 * constructor
	 * @param app application
	 */
	public Kernel3DW(App app) {
	    super(app);
		MAX_SPREADSHEET_COLUMNS_VISIBLE = KernelW.MAX_SPREADSHEET_COLUMNS_VISIBLE_WEB;
		MAX_SPREADSHEET_ROWS_VISIBLE = KernelW.MAX_SPREADSHEET_ROWS_VISIBLE_WEB;
		//Window.alert("KernelW3D : I will be threeD :-)");
    }


}
