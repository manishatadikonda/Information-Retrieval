import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class ReadingIndex {
	
	LinkedList<Integer> DocIdTempList = new LinkedList<Integer>();
	static Map<String, LinkedList<Integer>> PostingsListMap = new HashMap<String, LinkedList<Integer>>(); 
	//static ArrayList<Integer> temp2 = new ArrayList<Integer>();
	//ArrayList<Integer> temp1 = new ArrayList<Integer>();
	LeafReader lr;
	
	public static void main(String[] args) throws IOException,NullPointerException{

		ReadingIndex lir = new ReadingIndex();
		BufferedReader filereader = null;
		String currentline;
		filereader = new BufferedReader(new FileReader(args[0]));
		
		
		String path = "Users/Manisha/Desktop/index";
		FileSystem fs = FileSystems.getDefault();
		Path path1 =(fs.getPath(path));
		Document doc = null;
		IndexReader reader1 = DirectoryReader.open(FSDirectory.open(path1));
		//System.out.println(MultiFields.getIndexedFields(reader1));
		int count1 = 0;
		
		for(LeafReaderContext atomicreadercontext :reader1.leaves()) {
			lir.lr = atomicreadercontext.reader();
		Fields fields = MultiFields.getFields(lir.lr);
				
		for (String field:fields)
		{
			if(field.equals("id"))
				continue;
			if(field.equals("_version_"))
				continue;
			Terms terms = fields.terms(field);
			TermsEnum termsEnum = terms.iterator();
			int count = 0;
			
			while (termsEnum.next() != null) {
				
				BytesRef bytesRef = termsEnum.term();
				String bytesr = bytesRef.utf8ToString();
				double x = termsEnum.docFreq();
				count++;
				Term tr = new Term(field, bytesRef);
				lir.getPostingsList(tr,lir.lr);
				}
			count1 = count1 + count;
			//System.out.println(count);
		}
		}
		System.out.println("Total no of terms: "+count1);
		
		while ((currentline = filereader.readLine()) != null) {
			LinkedList<String> queryTerms = new LinkedList<String>();
			ArrayList<Integer> taatAND = null, taatOR = null, taator = null;
			String compterms[] = currentline.split(" ");
			for( String ss: compterms)
			{
				LinkedList ll;
				ll = PostingsListMap.get(ss);	
				System.out.print(ll);
				System.out.println();
			}
			
			//TAATAND
			int flag  = 0;
			int count = 0;
			for (String ss: compterms)
			{
				taatAND = termAtATimeAnd(taatAND, ss, flag, count);
				flag  = 1;
				queryTerms.add(ss);
			}
			System.out.println("TAATAnd:");
			System.out.println(taatAND);
			
			//TAATOR
			flag = 0;
			count = 0;
			for (String s: compterms)
			{
				System.out.println(s);
				taatOR = termAtATimeOr(taatOR, s, flag, count);
				flag=1;
				//taator.addAll(taatOR);
				queryTerms.add(s);
			}
			//taatOR = temp1;
			System.out.println("TAATOr:");
			System.out.println(taatOR); 
		}}
		
		public static ArrayList<Integer> termAtATimeAnd(ArrayList<Integer> taatAND, String s, int flag, int count)
		{
			ArrayList<Integer> temp = new ArrayList<Integer>();
			if(PostingsListMap.get(s) != null) {
				LinkedList<Integer> freqposts;
				freqposts = PostingsListMap.get(s);
				if(flag == 0)
				{
					for(Integer freqpost : freqposts)
					{
						temp.add(freqpost);
						System.out.println("Temp1 AND: "+temp);
					}
				}
				else
				{
					for( int a : taatAND) {
						for(Integer freqpost : freqposts)
						{
							count++;
							if( a == freqpost) {
								temp.add(a);
								System.out.println("Temp AND: "+temp);
								break;
							}	
						}
						
					}
					
				}
			}
			return temp;
		}
		
		public static ArrayList<Integer> termAtATimeOr(ArrayList<Integer> taatOR, String s, int flag, int count) throws IOException
		{
			ArrayList<Integer> temp1 = new ArrayList<Integer>();
			if(PostingsListMap.get(s) != null) {
				LinkedList<Integer> freqposts;
				freqposts = PostingsListMap.get(s);
				//System.out.println("TAATOR1: "+taatOR);
				//System.out.println("Postings: "+freqposts);
				if(flag == 0)
				{
					for(Integer freqpost:freqposts){
						temp1.add(freqpost);
						taatOR= temp1;
					}
					System.out.println("Initial temp" +temp1);
				}//correct code
				else
				{
					temp1= taatOR;
					//System.out.println("Temp1 after 1" + temp1);
					
					for( int freqpost: freqposts) {
						int flag1=0;
						for(Integer a : temp1)
						{
							//System.out.println("FPs" +freqposts);
							//System.out.println("Temp1 after 2" +temp1);
							//System.out.println("FreqPost: "+freqpost+"a: "+a);
							count++;
							//System.out.println("Inside for");
							if( a == freqpost) {
								flag1=1;
							}
						}
						if(flag1 == 0 )
					{
						temp1.add(freqpost);
						//System.out.print("Temp1 after adding" +temp1);
						flag1=0;
					}
					}
					taatOR=temp1;
				}
		
			}
			return temp1;
		}
		
		
		
		
		
	
	public void getPostingsList(Term tr, LeafReader lre) throws IOException {
		PostingsEnum postingList =  lre.postings(tr);
		while(postingList.nextDoc() != postingList.NO_MORE_DOCS) {
			DocIdTempList.add(postingList.docID());
		}
		int frequency = PostingsListMap.size();
		PostingsListMap.put(tr.bytes().utf8ToString(),DocIdTempList);
		//System.out.print(PostingsListMap);
		DocIdTempList = new LinkedList<Integer>();
	}

}