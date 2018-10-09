package rpc;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
	
	private static final String TYPE = "application/json; charset=UTF-8";
	private static final String ALLOWED = "Access-Control-Allow-Origin";
	
	// write a JSONArray to HTTP response encoding in UTF-8 
	public static void writeJSONArray( HttpServletResponse response, JSONArray array ) throws IOException {
		//PrintWriter out = response.getWriter();
		response.setContentType( TYPE );
		response.addHeader( ALLOWED, "*" );
		PrintWriter out = new PrintWriter( new OutputStreamWriter( response.getOutputStream(), "UTF8" ), true );
		out.print( array );
		out.close();
	}
	
	// write a JSONObject to HTTP response encoding in UTF-8
	public static void writeJSONObject( HttpServletResponse response, JSONObject obj ) throws IOException {
		//PrintWriter out = response.getWriter();
		response.setContentType( TYPE );
		response.addHeader( ALLOWED, "*" );
		PrintWriter out = new PrintWriter( new OutputStreamWriter( response.getOutputStream(), "UTF8" ), true );
		out.print( obj );
		out.close();
	}
}
