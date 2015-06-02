package esp.apisearch;

import java.io.IOException;

import ar.com.asanteit.esp.apps.commons.request.rest.ThrowableMapper;
import ar.com.asanteit.esp.jaxrs.JaxRsContext;
import ar.com.asanteit.esp.spi.Application;
import ar.com.asanteit.esp.spi.ApplicationException;
import ar.com.asanteit.esp.spi.Platform;
import ar.com.asanteit.esp.spi.Request;
import ar.com.asanteit.esp.spi.Response;
import esp.apisearch.controllers.IndexController;
import esp.apisearch.controllers.SchemaController;
import esp.apisearch.controllers.SearchController;
import esp.apisearch.services.IndexManagerService;

public class App  implements Application {

	private JaxRsContext jaxrs = null;

	@Override
	public void onLoad(Platform platform) throws ApplicationException {
		// Register REST services.
		jaxrs = new JaxRsContext(platform);

		
		jaxrs.setAttribute("platform", platform);
		IndexManagerService indexManagerService = new IndexManagerService(platform.getIndexService());
		jaxrs.setAttribute("indexManagerService", indexManagerService);
		
		jaxrs.registerProvider(ThrowableMapper.class);
		jaxrs.registerProvider(SchemaController.class);
		jaxrs.registerProvider(IndexController.class);
		jaxrs.registerProvider(SearchController.class);
		jaxrs.init();
		
		
		
		Request commitRequest = Request.builder().requestType("/api/index/commit")
		    .requestHost("localhost").requestPort(9090)//
		    .requestMethod("GET")//
		    .requestScheme("HTTP").build();
		platform.getSchedulerService().scheduleRequest("commit_indexes", null,
		    null, null, 180000l, true, commitRequest);
	}


	@Override
	public void onRequest(Platform platform, Request request, Response response)
	    throws ApplicationException, IOException {
		jaxrs.serviceREST(request, response);
	}


	@Override
  public void onUnload(Platform platform) throws ApplicationException {
		jaxrs.destroy();
		platform.getSchedulerService().unschedule("commit_indexes");
  }

}
