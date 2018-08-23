/**
 * 
 */
package simulation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import translation.DirectoryHandler;
import translation.Translation;

/**
 * @author Timo Freitag
 *
 */
public class XmiReader {
	URLClassLoader classLoader;
	ResourceSet resourceSet;
	Resource resource;
	DirectoryHandler directoryHandler = Translation.getTranslation().getDirectoryHandler();
	
	public XmiReader(URLClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public void initialize(String projectName) {
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(ResourceFactoryRegistryImpl.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
		
		try {
			Class<?> pack = classLoader.loadClass(directoryHandler.getFullClassName(projectName + "Package"));
			Field field = pack.getField("eINSTANCE");
			field.get(null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | ClassNotFoundException e) {
			throw new SimulationException("Failed to initialize the XmiReader");
		}
	}
	
	public Object load(File file) {
		resource = resourceSet.createResource(URI.createFileURI(file.getAbsolutePath()));
		try {
			resource.load(null);
			return resource.getContents().get(0);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(Object object) {
		try {
			Resource saveResource = resourceSet.createResource(URI.createURI(resource.getURI().toString().replace(".xmi", "_transformed.xmi")));
			saveResource.getContents().add((EObject) object);
			saveResource.save(null);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}