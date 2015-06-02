package esp.apisearch.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import ar.com.asanteit.esp.spi.index.IndexException;
import ar.com.asanteit.esp.spi.index.IndexService;
import ar.com.asanteit.esp.spi.index.WriteTransaction;

public class IndexManagerService {
	private final Logger log = Logger.getLogger(IndexManagerService.class);

	private Map<String, WriteTransaction> txMap = new ConcurrentHashMap<String, WriteTransaction>();
	private IndexService indexService;

	public IndexManagerService(IndexService theIndexService) {
		this.indexService = theIndexService;
	}

	public void commitAllIndexes() throws IndexException {
		log.info("Commiting Indexes.");
		if (!txMap.isEmpty()) {
			for (String index : txMap.keySet()) {
				log.info("Commiting Index: " + index);
				WriteTransaction writeTransaction = txMap.remove(index);
				writeTransaction.success();
				writeTransaction.close();
			}
		}
	}

	public WriteTransaction getWriteTransaction(String index)
	    throws IndexException {
		if (txMap.get(index) != null) {
			return txMap.get(index);
		} else {
			log.info("Creating write Transaction for: " + index);
			txMap.put(index, indexService.writeTransaction(index));
			return txMap.get(index);
		}
	}

	public boolean isIndexCreated(String index) {
		try {
			indexService.readTransaction(index);
			return true;
		} catch (IndexException e) {
			return false;
		}
	}
}