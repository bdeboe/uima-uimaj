/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.uima.cas.test;

import static java.util.Arrays.asList;
import static org.apache.uima.UIMAFramework.getResourceSpecifierFactory;
import static org.apache.uima.UIMAFramework.getXMLParser;
import static org.apache.uima.UIMAFramework.newDefaultResourceManager;
import static org.apache.uima.UIMAFramework.produceAnalysisEngine;
import static org.apache.uima.util.CasCreationUtils.createCas;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_component.Annotator_ImplBase;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.impl.PrimitiveAnalysisEngine_impl;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.impl.ResourceManager_impl;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;

public class JCasClassLoaderTest {
  
  /**
   * This test simulates an environment as it could exist when using e.g. PEARs. We use a vanilla
   * JCas and the analysis engines each use local JCas wrappers.
   * 
   * <ul>
   * <li>JCas does not know JCas wrappers for the {@code Token} type.</li>
   * <li>AddATokenAnnotator uses a local version of the {@code Token} type.</li>
   * <li>FetchTheTokenAnnotator uses a local version of the {@code Token} type.</li>
   * </ul>
   * 
   * The expectation here is that at the moment when the JCas is passed to the analysis engines,
   * {@link PrimitiveAnalysisEngine_impl#callAnalysisComponentProcess(CAS) it is reconfigured} using
   * {@link CASImpl#switchClassLoaderLockCasCL(ClassLoader)} to use the classloader defined in the
   * {@link ResourceManager} of the engines to load the JCas wrapper classes. So each of the anlysis
   * engines should use its own version of the JCas wrappers to access the CAS.
   * 
   * <b>NOTE:<b> On UIMAv3, this test currently fails because in {@link FetchTheTokenAnnotator},
   * the we a different version of the {@link Token} JCas wrapper than expected:
   * <pre>{@code 
   * Caused by: java.lang.ClassCastException: org.apache.uima.cas.test.Token cannot be cast to org.apache.uima.cas.test.Token
   *   at java.util.Iterator.forEachRemaining(Iterator.java:116)
   *   at org.apache.uima.cas.test.JCasClassLoaderTest$FetchTheTokenAnnotator.process(JCasClassLoaderTest.java:265)
   *   at org.apache.uima.analysis_component.JCasAnnotator_ImplBase.process(JCasAnnotator_ImplBase.java:48)
   *   at org.apache.uima.analysis_engine.impl.AnalysisEngineImplBase.lambda$3(AnalysisEngineImplBase.java:626)
   *   at org.apache.uima.analysis_engine.impl.AnalysisEngineImplBase.withContexts(AnalysisEngineImplBase.java:643)
   *   at org.apache.uima.analysis_engine.impl.AnalysisEngineImplBase.callProcessMethod(AnalysisEngineImplBase.java:623)
   *   at org.apache.uima.analysis_engine.impl.PrimitiveAnalysisEngine_impl.callAnalysisComponentProcess(PrimitiveAnalysisEngine_impl.java:381)
   *   ... 27 more
   * }</pre>
   * However, on UIMAv2, we do not get an exception.
   */
  @Test
  public void thatCASCanBeDefinedWithoutJCasWrappersAndTheyComeInWithAnnotators() throws Exception {
    ClassLoader rootCl = getClass().getClassLoader();

    // We do not want the CAS to know the Token JCas wrapper when it gets initialized
    ClassLoader clForCas = new IsolatingClassloader("CAS", rootCl)
            .hiding("org\\.apache\\.uima\\.cas\\.test\\.Token(_Type)?.*");

    ClassLoader clForAddATokenAnnotator = new IsolatingClassloader("AddATokenAnnotator", rootCl)
            .redefining("^.*AddATokenAnnotator$")
            .redefining("org\\.apache\\.uima\\.cas\\.test\\.Token(_Type)?.*");

    ClassLoader clForFetchTheTokenAnnotator = new IsolatingClassloader("FetchTheTokenAnnotator",
            rootCl)
            .redefining("^.*FetchTheTokenAnnotator$")
            .redefining("org\\.apache\\.uima\\.cas\\.test\\.Token(_Type)?.*");

    JCas jcas = makeJCas(clForCas);
    AnalysisEngine addATokenAnnotator = makeAnalysisEngine(AddATokenAnnotator.class,
            clForAddATokenAnnotator);
    AnalysisEngine fetchTheTokenAnnotator = makeAnalysisEngine(FetchTheTokenAnnotator.class,
            clForFetchTheTokenAnnotator);
    
    jcas.setDocumentText("test");
    
    addATokenAnnotator.process(jcas);
    fetchTheTokenAnnotator.process(jcas);
  }
  
  /**
   * This test simulates an environment as it could exist when using e.g. PEARs. The CAS has access
   * to JCas wrappers, but the analysis engines bring in their own.
   * 
   * <ul>
   * <li>JCas knows the global JCas wrappers for the {@code Token} type.</li>
   * <li>AddATokenAnnotator uses a local version of the {@code Token} type.</li>
   * <li>FetchTheTokenAnnotator uses a local version of the {@code Token} type.</li>
   * </ul>
   * 
   * The expectation here is that at the moment when the JCas is passed to the analysis engines,
   * {@link PrimitiveAnalysisEngine_impl#callAnalysisComponentProcess(CAS) it is reconfigured} using
   * {@link CASImpl#switchClassLoaderLockCasCL(ClassLoader)} to use the classloader defined in the
   * {@link ResourceManager} of the engines to load the JCas wrapper classes. So each of the anlysis
   * engines should use its own version of the JCas wrappers to access the CAS. In particular, they
   * should not use the global JCas wrappers which were known to the JCas when it was first
   * initialized.
   * 
   * <b>NOTE:<b> On UIMAv3, this test currently fails because in {@link FetchTheTokenAnnotator},
   * the we a different version of the {@link Token} JCas wrapper than expected:
   * <pre>{@code 
   * Caused by: java.lang.ClassCastException: org.apache.uima.cas.test.Token cannot be cast to org.apache.uima.cas.test.Token
   *   at java.util.Iterator.forEachRemaining(Iterator.java:116)
   *   at org.apache.uima.cas.test.JCasClassLoaderTest$FetchTheTokenAnnotator.process(JCasClassLoaderTest.java:265)
   *   at org.apache.uima.analysis_component.JCasAnnotator_ImplBase.process(JCasAnnotator_ImplBase.java:48)
   *   at org.apache.uima.analysis_engine.impl.AnalysisEngineImplBase.lambda$3(AnalysisEngineImplBase.java:626)
   *   at org.apache.uima.analysis_engine.impl.AnalysisEngineImplBase.withContexts(AnalysisEngineImplBase.java:643)
   *   at org.apache.uima.analysis_engine.impl.AnalysisEngineImplBase.callProcessMethod(AnalysisEngineImplBase.java:623)
   *   at org.apache.uima.analysis_engine.impl.PrimitiveAnalysisEngine_impl.callAnalysisComponentProcess(PrimitiveAnalysisEngine_impl.java:381)
   *   ... 27 more
   * }</pre>
   * However, on UIMAv2, we do not get an exception.
   */
  @Test
  public void thatAnnotatorsCanLocallyUseDifferentJCasWrappers() throws Exception {
    ClassLoader rootCl = getClass().getClassLoader();

    ClassLoader clForCas = new IsolatingClassloader("CAS", rootCl);

    ClassLoader clForAddATokenAnnotator = new IsolatingClassloader("AddATokenAnnotator", rootCl)
            .redefining("^.*AddATokenAnnotator$")
            .redefining("org\\.apache\\.uima\\.cas\\.test\\.Token(_Type)?.*");

    ClassLoader clForFetchTheTokenAnnotator = new IsolatingClassloader("FetchTheTokenAnnotator",
            rootCl)
            .redefining("^.*FetchTheTokenAnnotator$")
            .redefining("org\\.apache\\.uima\\.cas\\.test\\.Token(_Type)?.*");

    JCas jcas = makeJCas(clForCas);
    AnalysisEngine addATokenAnnotator = makeAnalysisEngine(AddATokenAnnotator.class,
            clForAddATokenAnnotator);
    AnalysisEngine fetchTheTokenAnnotator = makeAnalysisEngine(FetchTheTokenAnnotator.class,
            clForFetchTheTokenAnnotator);
    
    jcas.setDocumentText("test");
    
    addATokenAnnotator.process(jcas);
    fetchTheTokenAnnotator.process(jcas);
  }

  /**
   * Here we try to simulate a situation as it could happen e.g. in an OSGI environment where the
   * type system, the JCas and the analysis engines all use different classloaders.
   * 
   * <ul>
   * <li>JCas does not know JCas wrappers for the {@code Token} type.</li>
   * <li>The JCas wrappers are provided through a dedicated class loader</li>
   * <li>AddATokenAnnotator uses the type classloader to acces the JCas wrapper for
   * {@code Token}</li>
   * <li>FetchTheTokenAnnotator uses the type classloader to acces the JCas wrapper for
   * {@code Token}</li>
   * </ul>
   * 
   * The expectation here is that at the moment when the JCas is passed to the analysis engines,
   * {@link PrimitiveAnalysisEngine_impl#callAnalysisComponentProcess(CAS) it is reconfigured} using
   * {@link CASImpl#switchClassLoaderLockCasCL(ClassLoader)} to use the classloader defined in the
   * {@link ResourceManager} of the engines to load the JCas wrapper classes. Since the JCas wrappers
   * are loaded through the same classloader by both engines, it they should have the same type in
   * both annotators.
   * 
   * <b>NOTE:<b> On UIMAv2, this test currently fails because in {@link FetchTheTokenAnnotator},
   * the we get a plain {@link Annotation} from the JCas instead of a {@link Token}:
   * <pre>{@code 
   * Caused by: java.lang.ClassCastException: org.apache.uima.jcas.tcas.Annotation cannot be cast to org.apache.uima.cas.test.Token
   *   at java.util.Iterator.forEachRemaining(Iterator.java:116)
   *   at org.apache.uima.cas.test.JCasClassLoaderTest$FetchTheTokenAnnotator.process(JCasClassLoaderTest.java:233)
   *   at org.apache.uima.analysis_component.JCasAnnotator_ImplBase.process(JCasAnnotator_ImplBase.java:48)
   *   at org.apache.uima.analysis_engine.impl.PrimitiveAnalysisEngine_impl.callAnalysisComponentProcess(PrimitiveAnalysisEngine_impl.java:411)
   *   ... 28 more
   * }</pre>
   * However, on UIMAv3, we do not get an exception.
   */
  @Test
  public void thatTypeSystemCanComeFromItsOwnClassLoader() throws Exception {
    ClassLoader rootCl = getClass().getClassLoader();

    ClassLoader clForTS = new IsolatingClassloader("TS", rootCl)
        .redefining("org\\.apache\\.uima\\.cas\\.test\\.Token(_Type)?.*");

    ClassLoader clForCas = new IsolatingClassloader("CAS", rootCl)
            .hiding("org\\.apache\\.uima\\.cas\\.test\\.Token(_Type)?.*");

    ClassLoader clForAddATokenAnnotator = new IsolatingClassloader("AddATokenAnnotator", rootCl)
            .redefining("^.*AddATokenAnnotator$")
            .delegating("org\\.apache\\.uima\\.cas\\.test\\.Token(_Type)?.*", clForTS);

    ClassLoader clForFetchTheTokenAnnotator = new IsolatingClassloader("FetchTheTokenAnnotator",
            rootCl)
            .redefining("^.*FetchTheTokenAnnotator$")
            .delegating("org\\.apache\\.uima\\.cas\\.test\\.Token(_Type)?.*", clForTS);

    JCas jcas = makeJCas(clForCas);
    AnalysisEngine addATokenAnnotator = makeAnalysisEngine(AddATokenAnnotator.class,
            clForAddATokenAnnotator);
    AnalysisEngine fetchTheTokenAnnotator = makeAnalysisEngine(FetchTheTokenAnnotator.class,
            clForFetchTheTokenAnnotator);
    
    jcas.setDocumentText("test");
    
    addATokenAnnotator.process(jcas);
    fetchTheTokenAnnotator.process(jcas);
  }

  /**
   * Creates a new JCas and sets it up so it uses the given classloader to load its JCas wrappers.
   */
  private JCas makeJCas(ClassLoader cl)
          throws ResourceInitializationException, CASException, InvalidXMLException, IOException {
    TypeSystemDescription tsd = getXMLParser().parseTypeSystemDescription(new XMLInputSource(
            new File("src/test/resources/CASTests/desc/TokensAndSentencesTS.xml")));
    ResourceManager resMgr = newDefaultResourceManager();
    // resMgr.setExtensionClassPath(cl, "", false);
    CASImpl cas = (CASImpl) createCas(tsd, null, null, null, resMgr);
    cas.setJCasClassLoader(cl);
    return cas.getJCas();
  }

  /**
   * Creates a new analysis engine from the given class using the given classloader and sets it up
   * so it used the given classloader to load its JCas wrappers.
   */
  private AnalysisEngine makeAnalysisEngine(Class<? extends Annotator_ImplBase> aeClass,
          ClassLoader cl) throws ResourceInitializationException, MalformedURLException {
    ResourceManager resMgr = newDefaultResourceManager();
    resMgr.setExtensionClassPath(cl, "", false);
    System.out.println("setting extensionclass path: " + ((ResourceManager_impl)resMgr).getExtensionClassLoader().getParent().toString());
    AnalysisEngineDescription desc = getResourceSpecifierFactory()
            .createAnalysisEngineDescription();
    desc.setAnnotatorImplementationName(aeClass.getName());
    desc.setPrimitive(true);
    return produceAnalysisEngine(desc, resMgr, null);
  }

  public static class AddATokenAnnotator extends JCasAnnotator_ImplBase {
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
      System.out.printf("%s class loader: %s%n", getClass().getName(), getClass().getClassLoader());
      
      new Token(aJCas, 0, aJCas.getDocumentText().length()).addToIndexes();
    }
  }

  public static class FetchTheTokenAnnotator extends JCasAnnotator_ImplBase {
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
      System.out.printf("%s class loader: %s%n", getClass().getName(), getClass().getClassLoader());
      System.out.printf("Token class loader: %s%n", Token.class.getClassLoader());

      List<Token> tokens = new ArrayList<>();
      aJCas.getAllIndexedFS(Token.class).forEachRemaining(tokens::add);
      
      assertThat(tokens).isNotEmpty();
    }
  }

  /**
   * Special ClassLoader that helps us modeling different class loader topologies.
   */
  public static class IsolatingClassloader extends ClassLoader {

    private final Set<String> hideClassesPatterns = new HashSet<>();
    private final Set<String> redefineClassesPatterns = new HashSet<>();
    private final Map<String, ClassLoader> delegates = new LinkedHashMap<>();
    private final String id;

    private Map<String, Class<?>> loadedClasses = new HashMap<>();

    public IsolatingClassloader(String name, ClassLoader parent) {
      super(parent);

      id = name;
    }
    
    public IsolatingClassloader hiding(String... patterns)
    {
      hideClassesPatterns.addAll(asList(patterns));
      return this;
    }

    public IsolatingClassloader redefining(String... patterns)
    {
      redefineClassesPatterns.addAll(asList(patterns));
      return this;
    }

    public IsolatingClassloader delegating(String pattern, ClassLoader delegate)
    {
      delegates.put(pattern, delegate);
      return this;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      sb.append(id);
      sb.append(", loaded=");
      sb.append(loadedClasses.size());
      for (String s : loadedClasses.keySet()) {
        sb.append("\n ").append(s);
      }
      sb.append("]");
      return sb.toString();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      synchronized (getClassLoadingLock(name)) {
        Optional<ClassLoader> delegate = delegates.entrySet().stream()
                .filter(e -> name.matches(e.getKey())).map(Entry::getValue).findFirst();
        if (delegate.isPresent()) {
          System.out.println("cl: " + id + ", Delegating class: " + name);
          return delegate.get().loadClass(name);
        }
        
        if (hideClassesPatterns.stream().anyMatch(name::matches)) {
          throw new ClassNotFoundException(name);
        }

        if (redefineClassesPatterns.stream().anyMatch(name::matches)) {
          System.out.println("cl: " + id + ", Using redefined class: " + name);
          Class<?> loadedClass = loadedClasses.get(name);
          if (loadedClass != null) {
            return loadedClass;
          }
          
          System.out.printf("[%s] redefining class: %s%n", id, name);

          String internalName = name.replace(".", "/") + ".class";
          InputStream is = getParent().getResourceAsStream(internalName);
          if (is == null) {
            throw new ClassNotFoundException(name);
          }

          try {
            byte[] bytes = IOUtils.toByteArray(is);
            Class<?> cls = defineClass(name, bytes, 0, bytes.length);
            if (cls.getPackage() == null) {
              int packageSeparator = name.lastIndexOf('.');
              if (packageSeparator != -1) {
                String packageName = name.substring(0, packageSeparator);
                definePackage(packageName, null, null, null, null, null, null, null);
              }
            }
            loadedClasses.put(name, cls);
            return cls;
          } catch (IOException ex) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", ex);
          }
        }

        return super.loadClass(name, resolve);
      }
    }
  }
}
