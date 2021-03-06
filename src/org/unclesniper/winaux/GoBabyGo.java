package org.unclesniper.winaux;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import org.unclesniper.ogdl.Injection;
import org.unclesniper.ogdl.ClassRegistry;
import org.unclesniper.ogdl.FileURLResolver;
import org.unclesniper.winaux.boot.BootConfig;
import org.unclesniper.ogdl.DirectiveTokenSink;
import org.unclesniper.ogdl.ObjectGraphDescriptor;
import org.unclesniper.ogdl.ObjectDescriptionException;

public class GoBabyGo {

	private static ObjectGraphDescriptor readOGDL(File file, File confdir, ClassLoader bootcl)
			throws IOException, ObjectDescriptionException {
		ClassRegistry creg = new ClassRegistry();
		Injection inj = new Injection(creg);
		inj.setConstructionClassLoader(bootcl);
		inj.registerBuiltinStringClassMappers();
		DirectiveTokenSink.Wrapper wrapper = new DirectiveTokenSink.Wrapper();
		wrapper.setIncludeURLResolver("conf", new FileURLResolver(confdir));
		inj.addTokenSinkWrapper(wrapper);
		return inj.readDescription(file);
	}

	private static ClassLoader getBootClassLoader(File bootfile, File confdir)
			throws IOException, ObjectDescriptionException {
		ClassLoader parent = GoBabyGo.class.getClassLoader();
		if(!bootfile.isFile())
			return parent;
		ObjectGraphDescriptor odesc = GoBabyGo.readOGDL(bootfile, confdir, null);
		Object confroot = odesc.getRootObject();
		if(!(confroot instanceof BootConfig))
			throw new IllegalArgumentException("Bootstrap wiring file must yield a " + BootConfig.class.getName()
					+ ", not a " + confroot.getClass().getName());
		BootConfig conf = (BootConfig)confroot;
		return new URLClassLoader(conf.getClasspath(), parent);
	}

	public static void main(String[] args) {
		File home = new File(System.getProperty("user.home"));
		File confdir = new File(home, ".winaux");
		ClassLoader bootcl;
		try {
			bootcl = GoBabyGo.getBootClassLoader(new File(confdir, "boot.ogdl"), confdir);
		}
		catch(IOException | ObjectDescriptionException | RuntimeException e) {
			ExceptionWindow.showException(e, win -> System.exit(1));
			return;
		}
		File wirefile = new File(confdir, "wire.ogdl");
		Configuration conf;
		try {
			ObjectGraphDescriptor odesc = GoBabyGo.readOGDL(wirefile, confdir, bootcl);
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
