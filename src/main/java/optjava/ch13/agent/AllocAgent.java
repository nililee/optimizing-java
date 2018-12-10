package optjava.ch13.agent;

import java.lang.instrument.Instrumentation;

/**
 *
 * @author kittylyst
 */
public class AllocAgent {
	public static void premain(String args, Instrumentation instrumentation) {
		AllocRewriter transformer = new AllocRewriter();
		instrumentation.addTransformer(transformer);
	}

}