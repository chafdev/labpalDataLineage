package ca.uqac.lif.datalineage.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ TYPE, FIELD, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MetadataLineage {
	String value();
}
