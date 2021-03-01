import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
@WebServlet("/QCShuttleServlet")
@MultipartConfig(fileSizeThreshold= 0, 	// 0 Bytes 
maxFileSize= 30000)     	// 30000 Bytes
public class QCShuttleServlet extends HttpServlet 
{

	private String alertUrl = "https://www.qc.cuny.edu/about/directions/Pages/Shuttle.aspx";
	private String ticketUrl = "https://shuttlebus.simpletix.com/e/24943";
	private String calendarUrl = "https://www.calendarwiz.com/calendars/calendar.php?crd=queenscollege&amp;cid[]=255008";
	private String mapUrl = "https://queenscollegeshuttle.com/map?showHeader=0&amp;route=3235&amp;silent_disable_timeout=1";
	private String campusImageName = "campusImage.jpeg";
	private String jamaicaPDFName = "jamaicaPDF.pdf";
	private String flushingPDFName = "flushingPDF.pdf";

  private static final long serialVersionUID = 205242440643911308L;
  /**
   * Directory where uploaded files will be saved, its relative to
   * the web application directory.
   */
    
  
@Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
  {
  } 

@Override
protected void doPost(HttpServletRequest request,
          HttpServletResponse response) throws ServletException, IOException 
  {
    Part a = request.getPart("filename");
    InputStream stream = a.getInputStream();
    Scanner s = new Scanner(stream).useDelimiter("\\A");
    String result = s.hasNext() ? s.next() : "";
    System.out.println(result);
    if(result.equals(jamaicaPDFName))
    	getPDF(request, response, jamaicaPDFName);
    else if(result.equals(flushingPDFName))
	  getPDF(request, response, flushingPDFName);
    else if(result.equals(campusImageName))
      getImage(request, response, campusImageName);
    else if(result.equals("alertContent"))
    {
		String alertContent = getAlertContent(alertUrl);
		sendText(request, response, alertContent);
    } 
	
  }
  private void sendText(HttpServletRequest request, HttpServletResponse response, String content) throws IOException
  {
	  String contextPath = getServletContext().getRealPath(File.separator);
	  File alertFile = new File(contextPath + "alertContent");
	  FileWriter f = new FileWriter(alertFile);
	  f.write(content);
	  f.close();
	  response.setContentType("text/plain");
	  response.addHeader("Content-Disposition", "attachment; filename=" + "alertContent");
	  response.setContentLength((int) alertFile.length());
	  FileInputStream fileInputStream = new FileInputStream(alertFile);
	  OutputStream responseOutputStream = response.getOutputStream();
	  int bytes;
	  while ((bytes = fileInputStream.read()) != -1) 
	  {
		responseOutputStream.write(bytes);
	  }
	  fileInputStream.close();
	  responseOutputStream.close();
  }
  private void getPDF(HttpServletRequest request, HttpServletResponse response, String pdfName) throws IOException
  {
	  String contextPath = getServletContext().getRealPath(File.separator);
	  File pdfFile = new File(contextPath + pdfName);
	  response.setContentType("application/pdf");
	  response.addHeader("Content-Disposition", "attachment; filename=" + pdfName);
	  response.setContentLength((int) pdfFile.length());
	  FileInputStream fileInputStream = new FileInputStream(pdfFile);
	  OutputStream responseOutputStream = response.getOutputStream();
	  int bytes;
	  while ((bytes = fileInputStream.read()) != -1) 
	  {
		responseOutputStream.write(bytes);
	  }
	  fileInputStream.close();
  }
  private void getImage(HttpServletRequest request, HttpServletResponse response, String imageName) throws IOException
  {
	  String contextPath = getServletContext().getRealPath(File.separator);
	  File imageFile = new File(contextPath + imageName);
	  response.setContentType("image/jpeg");
	  response.addHeader("Content-Disposition", "attachment; filename=" + imageName);
	  response.setContentLength((int) imageFile.length());
	  FileInputStream fileInputStream = new FileInputStream(imageFile);
	  OutputStream responseOutputStream = response.getOutputStream();
	  int bytes;
	  while ((bytes = fileInputStream.read()) != -1) 
	  {
		responseOutputStream.write(bytes);
	  }
	  fileInputStream.close();
  }
  private String getAlertContent(String url) throws IOException
  {
	  Document document;
	  String content = "";
	  document = Jsoup.connect(url).get();
	  Elements pargs = document.select("p.MsoNormal");
	  for(int i=0; i<pargs.size(); i++)
	  {
		  content = content.concat(pargs.get(i).text());
		  content = content.concat("---");
	  }
	  return content;
  }
  private String getFileName(Part part) 
  {
      String contentDisp = part.getHeader("filename");
      System.out.println("filename= "+contentDisp);
      return "";
  }
}