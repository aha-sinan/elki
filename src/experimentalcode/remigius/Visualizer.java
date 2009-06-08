package experimentalcode.remigius;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.visualization.colors.ColorLibrary;
import de.lmu.ifi.dbs.elki.visualization.colors.PublicationColorLibrary;
import de.lmu.ifi.dbs.elki.visualization.scales.LinearScale;
import de.lmu.ifi.dbs.elki.visualization.scales.Scales;

public abstract class Visualizer<O extends DoubleVector> {
	
	protected Database<O> database;
	protected LinearScale[] scales;
	
	protected VisualizationManager<O> visManager;
	
	protected static final CommonSVGShapes SHAPEGEN = new CommonSVGShapes();
	protected static final ColorLibrary COLORS = new PublicationColorLibrary();
	
	public Visualizer(Database<O> db, VisualizationManager<O> v){
		this.database = db;
		this.scales = Scales.calcScales(db);
		this.visManager = v;
	}
	
	public Double getPositioned(O o, int dimx){
			return scales[dimx].getScaled(o.getValue(dimx));
	}
	
	public Visualization<O> visualize(Document doc, int dimx, int dimy){
		Element layer = SHAPEGEN.createSVG(doc);
		return visualizeElements(doc, layer, dimx, dimy);
		
	}
	protected abstract Visualization<O> visualizeElements(Document doc, Element layer, int dimx, int dimy);
}
