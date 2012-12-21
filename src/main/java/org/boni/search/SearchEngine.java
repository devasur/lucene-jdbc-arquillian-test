package org.boni.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.apache.lucene.util.Version;
import org.boni.domain.Person;
import org.boni.service.GenericDao;
import org.boni.service.PersonDao;
import org.boni.service.impl.GenericJpaDaoImpl;


@Singleton
public class SearchEngine {
	@Resource(mappedName="jdbc/testDs") 
	DataSource dataSource;
	
	@PersistenceContext(unitName = "arquilianPU")
	protected EntityManager entityManager;
	
	private static IndexWriter iw = null;
	private static QueryParser parser = null;
	private static Analyzer analyzer = null;
	private static Directory d = null;
	
	@PostConstruct
	public void postConstruct() throws Exception{
		d = 
	    new RAMDirectory(); 
		new JdbcDirectory(dataSource, "SEARCH_INDEX"){
			@Override
			public String[] listAll() throws IOException {
				return list();
			}
		};
		
	    analyzer = new StandardAnalyzer(Version.LUCENE_30);
	    iw = new IndexWriter(d, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
	    parser = new QueryParser(Version.LUCENE_30, "searchfield",analyzer);
	}
	
	public <T> void doIndex(T entity) throws Exception{
		iw.addDocument(getDocument(entity));
		iw.commit();
	}
	
	public <T> List<T> doSearch(Class<T> classToSearch, String query) throws Exception{
		IndexSearcher searcher = new IndexSearcher(d);
		Query q = parser.parse(query+"*");
		System.out.println(q);
		TopDocs rs = searcher.search(q, null, 10);
		System.out.println("Total Hits:" + rs.totalHits);
		ScoreDoc[] scoreDocs =  rs.scoreDocs;
		if (null == scoreDocs) return new ArrayList<T>();
		List<T> matching = new ArrayList<T>();
		GenericDao<T, Long> dao = new GenericJpaDaoImpl<T, Long>(classToSearch){};
		dao.setEntityManager(entityManager);
		for (ScoreDoc aDoc : scoreDocs){
			Document document = searcher.doc(aDoc.doc);
			Long id = Long.valueOf(document.get("id")) ;
			matching.add(dao.findById(id));
		}
		return matching;
	}
	
	private<T> Document getDocument(T entity){
		if (entity instanceof Person){
			Person p = (Person)(entity);
			Document d = new Document();
			d.add(new Field("id", p.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			d.add(new Field("class", Person.class.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			d.add(new Field("name", p.getName(),Field.Store.YES, Field.Index.ANALYZED));
			d.add(new Field("country", p.getCountry(),Field.Store.YES, Field.Index.ANALYZED));
			ArrayList<String> hobbies = p.getHobbies();
			String hobbiesString = null;
			if (null != hobbies){
				hobbiesString = "";
				for (String aHobby : hobbies){
					hobbiesString = hobbiesString + aHobby + " ";
				}
				d.add(new Field("hobbies", hobbiesString ,Field.Store.NO, Field.Index.ANALYZED));
			}
			d.add(new Field("searchfield", (p.getName() + " " + p.getCountry())  + (hobbiesString == null ? "" : " " + hobbiesString),Field.Store.NO, Field.Index.ANALYZED));
			System.out.println(d);
			return d;
		}
		return null;
	}
}
