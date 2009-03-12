package experimentalcode.lisa;

import java.util.Iterator;

import de.lmu.ifi.dbs.elki.utilities.QueryResult;
import de.lmu.ifi.dbs.elki.algorithm.DistanceBasedAlgorithm;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.AssociationID;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.IndexDatabase;
import de.lmu.ifi.dbs.elki.distance.Distance;
import de.lmu.ifi.dbs.elki.result.AnnotationsFromDatabase;
import de.lmu.ifi.dbs.elki.result.MultiResult;
import de.lmu.ifi.dbs.elki.result.OrderingFromAssociation;
import de.lmu.ifi.dbs.elki.utilities.Description;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.DoubleParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.IntParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.PatternParameter;



public class SimpleIndexBasedOutlierDetection <O extends DatabaseObject, D extends Distance<D>> extends DistanceBasedAlgorithm<O , D , MultiResult> {
	
	public static final OptionID D_ID = OptionID.getOrCreateOptionID(
		      "sibo.d",
		      "size of the D-neighborhood"
		  );;

	public static final OptionID P_ID = OptionID.getOrCreateOptionID(
				      "sibo.p",
				      "minimum fraction of objects that must be outside the D-neigborhood of an outlier"
				  );;
		
		public static final AssociationID<Boolean> SIBO_OFLAG= AssociationID.getOrCreateAssociationID("sibo.oflag", Boolean.class);
		/**
		   * Parameter to specify the size of the D-neighborhood,
		   * 
		   * <p>Key: {@code -sibo.d} </p>
		   */
		  private final PatternParameter D_PARAM = new PatternParameter(D_ID);
		  
		  /**
		   * Parameter to specify the minimum fraction of objects that must be outside the D-neigborhood of an outlier,
		   * 
		   * <p>Key: {@code -sibo.p} </p>
		   */
		  private final DoubleParameter P_PARAM = new DoubleParameter(P_ID);
		  /**
		   * Holds the value of {@link #D_PARAM}.
		   */
		  private String d;
		  /**
		   * Holds the value of {@link #P_PARAM}.
		   */
		  private double p;
		  /**
		   * Provides the result of the algorithm.
		   */
		  MultiResult result;

		  /**
		   * Constructor, adding options to option handler.
		   */
		  public SimpleIndexBasedOutlierDetection() {
		    super();
		    // neighborhood size
		    addOption(D_PARAM);
		    // maximum fraction of objects outside the neighborhood of an outlier
		    addOption(P_PARAM);
		    }
		  
		  /**
		   * Calls the super method
		   * and sets additionally the values of the parameter
		   * {@link #D_PARAM}, {@link #P_PARAM} 
		   */
		  @Override
		  public String[] setParameters(String[] args) throws ParameterException {
		      String[] remainingParameters = super.setParameters(args);

		   // neighborhood size
		      d = D_PARAM.getValue();
			    // maximum fraction of objects outside the neighborhood of an outlier
			    p = P_PARAM.getValue();
		      
		      return remainingParameters;
		  }

		  /**
		   * Runs the algorithm in the timed evaluation part.
		   */
		  @Override
		  protected MultiResult runInTime(Database<O> database) throws IllegalStateException {
			getDistanceFunction().setDatabase(database, isVerbose(), isTime());
			D distance = getDistanceFunction().valueOf(d);
			//maximum number of objects in the D-neighborhood of an outlier 
		    int m = (int) ((database.size())*(1-p));
		    //if index exists, kNN query. if the distance to the mth nearest neighbor is more than d -> object is outlier  
		    if (database instanceof IndexDatabase){
		    	for(Integer id : database.getIDs()){
		    		if(database.kNNQueryForID(id, m , getDistanceFunction()).get(m-1).getDistance().compareTo(distance)  <= 0){
		    			//flag as outlier
		    			database.associate(SIBO_OFLAG, id, true);
		    		}
		    		else {
		    			//flag as no outlier
			    		database.associate(SIBO_OFLAG, id, false);
		    		}
		    	}
		    }
		    else {
		    	//range query for each object. stop if m objects are found
		    	for (Integer id : database.getIDs()){
			    	Iterator<Integer> iterator = database.iterator();
			        int count = 0;
			    	while (iterator.hasNext()&& count < m) {
			            Integer currentID = iterator.next();
			            D currentDistance = getDistanceFunction().distance(id, currentID);
			            if (currentDistance.compareTo(distance) <= 0) {
			            	count ++;
			            }
			    	}
			    	if(count<= m){
			    		//flag as outlier
			    		database.associate(SIBO_OFLAG, id, true);
			    	}
			    	else {
			    		//flag as no outlier
			    		database.associate(SIBO_OFLAG, id, false);
			    	}
		    	}
		    }
		    AnnotationsFromDatabase<O, Boolean> res1 = new AnnotationsFromDatabase<O, Boolean>(database);
	        res1.addAssociation("OFLAG", SIBO_OFLAG);
	        // Ordering
	        OrderingFromAssociation<Boolean, O> res2 = new OrderingFromAssociation<Boolean, O>(database, SIBO_OFLAG, true); 
	        // combine results.
	        result = new MultiResult();
	        result.addResult(res1);
	        result.addResult(res2);
			return result;
			
		  }
		  
		@Override
		public Description getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
 
		@Override
		public MultiResult getResult() {
			return result;
		}

}

