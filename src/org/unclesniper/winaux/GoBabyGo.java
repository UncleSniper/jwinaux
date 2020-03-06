package org.unclesniper.winaux;

import java.io.File;
import java.io.IOException;
import org.unclesniper.ogdl.Injection;
import org.unclesniper.ogdl.ClassRegistry;
import org.unclesniper.ogdl.ObjectGraphDescriptor;
import org.unclesniper.ogdl.ObjectDescriptionException;

public class GoBabyGo {

	public static void main(String[] args) {
		File home = new File(System.getProperty("user.home"));
		File confdir = new File(home, ".winaux");
		File wirefile = new File(confdir, "wire.ogdl");
		ClassRegistry creg = new ClassRegistry();
		Injection inj = new Injection(creg);
		inj.registerBuiltinStringClassMappers();
		Configuration conf;
		try {
			ObjectGraphDescriptor odesc = inj.readDescription(wirefile);
			Object confroot = odesc.getRootObject();
			if(!(confroot instanceof Configuration))
				throw new IllegalArgumentException("Wiring file must yield a " + Configuration.class.getName()
						+ ", not a " + confroot.getClass().getName());
			conf = (Configuration)confroot;
		}
		catch(IOException | ObjectDescriptionException | RuntimeException e) {
			ExceptionWindow.showException(e, win -> System.exit(1));
			return;
		}
		AuxEngine engine = new AuxEngine();
		engine.doYaThang(conf, () -> System.exit(1));
	}

}
