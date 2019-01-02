package aspects;

import org.aspectj.lang.Signature;

import ca.uqac.lif.datalineage.DataInfo;
import ca.uqac.lif.datalineage.ManageDataInfo;
import ca.uqac.lif.datalineage.annotation.AnnotationHelper;
import ca.uqac.lif.datalineage.annotation.DynamicAnnotation;
import ca.uqac.lif.datalineage.annotation.MetadataLineage;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableCellNode;
import ca.uqac.lif.mtnp.table.TableTransformation;
import ca.uqac.lif.mtnp.table.TransformedTable;

public aspect AspectData {

	pointcut constructor(): execution(public ExperimentTable+.new(..)) || execution(public Experiment+.new(..)) ||  execution(public TransformedTable+.new(..));

	after(): constructor(){

		Signature sig = thisJoinPointStaticPart.getSignature();
		String line = "" + thisJoinPointStaticPart.getSourceLocation().getLine();
		String sourceName = thisJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName();

		String destination = sig.getDeclaringTypeName() + "." + sig.getName();
		Class<?> classType = null;
		MetadataLineage myAnnotation = null;
		try {
			classType = Class.forName(sig.getDeclaringType().getName());
			myAnnotation = classType.getAnnotation(MetadataLineage.class);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		if (myAnnotation != null && myAnnotation.value() != "")
			AnnotationHelper.alterAnnotationOn(classType, MetadataLineage.class, new DynamicAnnotation("test"));

		Object o = thisJoinPoint.getTarget();
		Object[] values = thisJoinPoint.getArgs();

		DataInfo d = new DataInfo(o, sourceName, destination, line, true);
		if (myAnnotation != null) {
			myAnnotation = classType.getAnnotation(MetadataLineage.class);
			d.setIdJoint(myAnnotation.value());
		}
		ManageDataInfo.getInstance().addDataInfo(d);
	}

	pointcut traceMethods() :  !cflow(within(ManageDataInfo)) && !cflow(within(AspectData)) &&  (target(TableTransformation) || target(TableCellNode)|| target(Experiment) || target(TransformedTable) || target(ExperimentTable)) && (call(* *(..)) || execution(* *(..)))  && !call(String toString()) && !execution(String toString());

	before(): traceMethods(){

		Signature sig = thisJoinPointStaticPart.getSignature();
		String line = "" + thisJoinPointStaticPart.getSourceLocation().getLine();
		String sourceName = thisJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName();
		String destination = sig.getDeclaringTypeName() + "." + sig.getName();
		if (!sig.getName().contains("get")) {
			Object o = thisJoinPoint.getTarget();
			Object[] values = thisJoinPoint.getArgs();
			DataInfo d = new DataInfo(o, sourceName, destination, line, false);
			String t = Integer.toString(thisJoinPointStaticPart.getId());
			d.setIdJoint(t);
			ManageDataInfo.getInstance().addDataInfo(d);
		}
	}

	after(): traceMethods(){

		Signature sig = thisJoinPointStaticPart.getSignature();
		String line = "" + thisJoinPointStaticPart.getSourceLocation().getLine();
		String sourceName = thisJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName();
		String destination = sig.getDeclaringTypeName() + "." + sig.getName();
		if (!sig.getName().contains("get")) {
			Object o = thisJoinPoint.getTarget();
			Object[] values = thisJoinPoint.getArgs();

			DataInfo d = new DataInfo(o, sourceName, destination, line, false);
			String t = Integer.toString(thisJoinPointStaticPart.getId());
			d.setIdJoint(t);
			ManageDataInfo.getInstance().addDataInfo(d);
		}
	}
}
