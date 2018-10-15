package com.waverider.soldout;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;
import com.waverider.soldout.messages.SoldOutEntityUpdate;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ChronicleWriter {

	Schema<SoldOutEntityUpdate> soldOutEntitySchema = RuntimeSchema.getSchema(SoldOutEntityUpdate.class);
	final private String chronicleName;
	private Boolean fileExists = false;
	static public String basePathForChronicle = System.getProperty("ipc.chronicleDir","./dat");
	private static final Logger logger = LoggerFactory.getLogger(ChronicleWriter.class);


	 // Re-use (manage) this buffer to avoid allocating on every serialization
    LinkedBuffer buffer = LinkedBuffer.allocate(1024);
	private IndexedChronicle chronicle;
	private Excerpt excerpt;
	
	public ChronicleWriter(String chronicleName){
		this.chronicleName = chronicleName;
		try {
			File f = new File(chronicleName);
			if(f.exists()) {
				fileExists = true;
			}
			
			chronicle = new IndexedChronicle(chronicleName);
			
		} catch(IOException ioe) {
			logger.error("Chronicle error - ioe: " + ioe.getMessage());
			System.exit(-1);
		}
		excerpt = chronicle.createExcerpt();
	}
	
	public void writeEntity(SoldOutEntityUpdate entity){
	    // ser
	    final byte[] protostuff;
	    try
	    {
	        protostuff = ProtostuffIOUtil.toByteArray(entity, soldOutEntitySchema, buffer);
	        excerpt.startExcerpt(protostuff.length);
			excerpt.write(protostuff,0,protostuff.length);
			excerpt.finish();
	    }
	    finally
	    {
	        buffer.clear();
	    }
	}
	
	public void runReadLoopToEnd(ChronicleSubscriber s) throws IOException {
		byte[] bytes = new byte[1024];
		int count=0;
		
		while (excerpt.nextIndex()){
			count++;
			excerpt.read(bytes);
			SoldOutEntityUpdate soe =  soldOutEntitySchema.newMessage();
		    ProtostuffIOUtil.mergeFrom(bytes, soe, soldOutEntitySchema);
		    s.onMessage(soe);
		}
	}
}
