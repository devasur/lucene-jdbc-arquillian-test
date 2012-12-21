package org.boni.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.sql.DataSource;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.apache.lucene.util.Version;
import org.boni.domain.Person;
import org.boni.search.SearchEngine;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TestSearchPerson {
	
	@EJB
	PersonDao personDao = null;
	
    static final
    JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "testService.jar")
    		  .addPackage("org.boni.service")
    		  .addPackage("org.boni.service.impl")
    		  .addPackage("org.boni.search")
		      .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
		      .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    
    @Deployment
	public static JavaArchive createDeployment() {
    	return archive;
	}
	
    @EJB
    SearchEngine searcher;
    
	@Test
	public void doSearch() throws Exception{
		searcher.doIndex(personDao.save(new Person("Boni","IN",new String[]{"reading","coding","cycling"})));
		searcher.doIndex(personDao.save(new Person("nikhil","IN",new String[]{"painting","cycling","dancing"})));
		searcher.doIndex(personDao.save(new Person("urmi","IN",new String[]{"singing","cooking","sleeping"})));
		searcher.doIndex(personDao.save(new Person("baba","IN",new String[]{"reading","cooking"})));
		searcher.doIndex(personDao.save(new Person("piku","IN",new String[]{"laughing","football"})));
		List<Person> persons = personDao.finadAll();
		assertEquals(5,persons.size());
		searchAndPrintResults("cooking");
		searchAndPrintResults("laughing");
		searchAndPrintResults("reading");
		searchAndPrintResults("foot");
	}
	
	private void searchAndPrintResults(String searchString) throws Exception{
		System.out.println("Search Query:" + searchString);
		List<Person> persons = searcher.doSearch(Person.class, searchString);
		for (Person aPerson : persons){
			System.out.println("Name:" + aPerson.getName());
		}
		
	}
	
	@Resource(mappedName="jdbc/testDs") 
	DataSource dataSource;
	
	//@Test
	public void doSimpleSearchTestWithJdbcDirectory() throws Exception{
        Directory directory = 	new JdbcDirectory(dataSource, "SEARCH_INDEX"){
			@Override
			public String[] listAll() throws IOException {
				return list();
			}
		};

        IndexWriter writer = 
          new IndexWriter(directory, new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
            
        Document doc = new Document(); 
        doc.add(new Field("partnum", "Q36", Field.Store.YES, Field.Index.NOT_ANALYZED));   
        doc.add(new Field("description", "Illidium Space Modulator", Field.Store.YES, Field.Index.ANALYZED)); 
        writer.addDocument(doc); 
        writer.close();

        IndexSearcher searcher = new IndexSearcher(directory);
        
        QueryParser qp = new QueryParser(Version.LUCENE_30,"description",new SimpleAnalyzer());
        Query query = qp.parse("Illidium");;   
        TopDocs rs = searcher.search(query, null, 10);
        System.out.println(rs.totalHits);

        Document firstHit = searcher.doc(rs.scoreDocs[0].doc);
        System.out.println(firstHit.getField("partnum").name());		
	}
}
