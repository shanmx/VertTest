package com.shanmx;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	public JDBCClient jdbc;
	//private HttpServerFileUpload upload;
/*	private void uploadfile(RoutingContext routingContext) 
	{
		req=  routingContext.request(); 
		req.setExpectMultipart(true);
	    req.uploadHandler(HttpServerFileUpload upload,res->{
	    //upload.exceptionHandler((Throwable err) => req.response().end("Upload failed"));
	    upload.endHandler(() -> req.response().end("Successfully uploaded to "+upload.filename()));
	    upload.streamToFileSystem(upload.filename());
	    });
	}
		*/

	private void getAll(RoutingContext routingContext) {
		jdbc.getConnection(res -> {
			  if (res.succeeded()) {
			    SQLConnection connection = res.result();
			    connection.query("SELECT id,role_name as roleName,note FROM t_role", res2 -> {
			      if (res2.succeeded()) {
			        ResultSet rs = res2.result();
			        // Do something with results
			        System.out.println(rs.getRows().toString());
				    Map<String, Object> m = new HashMap<String, Object>();  
				    m.put("total", rs.getNumRows());  
				    m.put("rows",rs.getRows()); 
					routingContext.response()	  
				       .putHeader("content-type", "text/html")
				       .end(Json.encodePrettily(m));			        
			      }else{
			    	  logger.error(res2.cause());
			    	  System.out.println("没有取得角色列表！");
			    	  connection.close();
			      }
			    });
			  } else {
			    System.out.println("获得连接失败");
			  }
			});
		}	
	private void updateRole(RoutingContext routingContext) {
			jdbc.getConnection(res -> {
				  if (res.succeeded()) {
				    SQLConnection connection = res.result();
				    System.out.println(routingContext.data().toString());
				    
				    //System.out.println(routingContext.getBodyAsJson());
				    //Map<String,Object> role=(Map<String,Object>)routingContext.request().params();
				    JsonArray params=new JsonArray().add(routingContext.request().params().get("roleName")).add(routingContext.request().params().get("note")).add(routingContext.request().params().get("id"));
				   // System.out.println(routingContext.request().params()..getParam("roleName"));
					//JsonArray params=new JsonArray().add("大海").add("大海呀，全都是水").add(1); 
				    connection.updateWithParams("update t_role set role_name=?,note=? where id=?",params
					  ,res2 -> {
						JsonObject result=new JsonObject();  
				      if (res2.succeeded()) {
				    	 result.put("success", "更新成功"); 
				        System.out.println(res2.result().getUpdated());
	       		      }else{
				    	  logger.error(res2.cause());
				    	  result.put("errorMsg", "更新角色失败，原因是："+res2.cause());
				    	  System.out.println("没有更新成功！");
				    	  connection.close();
				      }
				      routingContext.response().end(result.toString());
				    });
				  } else {
				    System.out.println("获得连接失败");
				  }
				});
			}
    @Override
	public void start(Future<Void> startFuture) throws Exception {
	Router router = Router.router(vertx);
	Buffer dsbuffer = vertx.fileSystem().readFileBlocking("config.properties");
	JsonObject config = new JsonObject(dsbuffer);
	jdbc = JDBCClient.createShared(vertx, config, "mainDataSource");
	 // 将访问“/assets/*”的请求route到“assets”目录下的资源
	//router.route("/").handler(ctx->ctx.reroute(HttpMethod.GET,"/assets/index.html"));
	router.route("/assets/*").handler(StaticHandler.create("assets"));
	router.route().handler(BodyHandler.create());
	router.get("/role/getRoleList").handler(this::getAll);
	router.post("/role/updateRole").handler(this::updateRole);
	//router.post("/upload").handler(this::uploadfile);
	 vertx
	     .createHttpServer()
	     .requestHandler(router::accept)
	     .listen(config.getInteger("http.port", 8080),result -> {
	           if (result.succeeded()) {
	        	   startFuture.complete();
	           } else {
	        	   startFuture.fail(result.cause());
	           }
	         }
	     );
	}
}
