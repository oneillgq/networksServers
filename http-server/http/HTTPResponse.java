package http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * Class representing a single HTTP response message.
 *
 * @version 1.0
 */
public class HTTPResponse {
    /* TODO
     * 1) Create methods and member variables to represent an HTTP response.
     * 2) Set response fields based on the request message received.
     *      a) If the request was invalid, send a 400 Bad Request response, with errors/400.html.
     *      b) If the request path doesn't exist, send a 404 Not Found, with errors/404.html.
     *      c) Otherwise, send a 200 OK, with the full contents of the file.
     *      d) Every response must have these four headers: Server, Date, Content-Length, and Content-Type.
     *          i) Note that Date must follow the required HTTP date format.
     * 3) Craft the response message and send it out the socket with DataOutputStream.
     *      a) Be sure that you use "\r\n" to separate each line.
     */

    private boolean validReq;
    private String path;
    private String status;
    private String reason;
    private HashMap<String,String> headerVars;

    public HTTPResponse(HTTPRequest req) {
        validReq = req.isValid();
        if (validReq) { path = "content" + req.getPath(); }
        initHeader();
    }

    private void initHeader() {
        headerVars = new HashMap<String,String>();
        headerVars.put("Server", "oneillgq");
        headerVars.put("Content-Length", "unset");
        headerVars.put("Date", getDate());
        headerVars.put("Content-Type", "unset");
    }
    
    public void sendResponse(DataOutputStream o) throws IOException { 
        var content = parseContent();
        writeResponseHeaders(o);
        o.write(content);
    } 

    private byte[] parseContent() throws IOException {
        if (!validReq) {
            path = "errors/400.html";
            status = "400";
            reason = "Bad Request";
        } else if (Files.exists(Paths.get(path))) {
            status = "200";
            reason = "OK";
        } else {
            path = "errors/404.html";
            status = "404";
            reason = "Not Found";
        } 
        if (path.equals("content/")) { path = "content/index.html"; }
        var p = Paths.get(path);
        headerVars.put("Content-Length", Long.toString(Files.size(p)));
        headerVars.put("Content-Type", Files.probeContentType(p));
        return Files.readAllBytes(p);
    }

    public String getStatus() {
        return status;
    }

    private void writeResponseHeaders(DataOutputStream o) throws IOException {
        String resp = ("HTTP/1.1" + " " + status + " " + reason + "\r\n");
        for (String key : headerVars.keySet()) {
            resp += (key + ": " + headerVars.get(key) + "\r\n");
        }
        resp += ("\r\n");
        var byteResp = resp.getBytes();
        o.write(byteResp);
    }

    private static String getDate(){
        var now = ZonedDateTime.now(ZoneOffset.UTC);
        String pattern = "E, d MMM yyyy HH:mm:ss 'GMT'";
        var formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        return now.format(formatter);
    }
        
       
}
