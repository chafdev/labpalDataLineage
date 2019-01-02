package ca.uqac.lif.datalineage.annotation;

import java.lang.annotation.Annotation;

public class DynamicAnnotation implements MetadataLineage {
    private String value;
 
    public DynamicAnnotation(String value) {
        this.value = value;
    }

 
    @Override
    public Class<? extends Annotation> annotationType() {
        return DynamicAnnotation.class;
    }

	@Override
	public String value() {
		// TODO Auto-generated method stub
		return value;
	}
}