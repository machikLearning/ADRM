package kr.ac.cbnu.computerengineering.common;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import kr.ac.cbnu.computerengineering.board.common.datatype.FileDataType;
import kr.ac.cbnu.computerengineering.common.datatype.UserDataType;
import kr.ac.cbnu.computerengineering.common.util.Utils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class AbsADRMServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<FileDataType> uploadBoardFile(HttpServletRequest request, HttpServletResponse response, String uploadFolder) throws ServletException, IOException, ParseException {		
        try{
		int sizeLimit = 30 * 1024 * 1024;
        List<FileDataType> uploadFiles = new ArrayList<>();
        File file = new File(uploadFolder);
		if(!file.exists()){
			file.mkdirs();
		}
        MultipartRequest multi = new MultipartRequest(request, uploadFolder, sizeLimit,"euc-kr",new DefaultFileRenamePolicy());
        @SuppressWarnings("unchecked")
		Enumeration <String> formNames = multi.getFileNames();
        String formName="";
        String fileName="";
        while (formNames.hasMoreElements()) {    
        	formName = (String)formNames.nextElement();    
        	fileName = multi.getFilesystemName(formName);
        	if(fileName != null) {   
        		File uploadFile = new File(uploadFolder+"/"+fileName);
        		String now = new SimpleDateFormat("yyyyMMddHmss").format(new Date()) + " ";
        		String saveName= now + fileName;
        		uploadFile.renameTo(new File(uploadFolder+"/"+saveName));
        		uploadFile = new File(uploadFolder+"/"+saveName);
        		String nickName = this.makeNickName(saveName);
        		uploadFiles.add(new FileDataType(uploadFile.getCanonicalPath(), saveName,uploadFile.length(),nickName));
        	}
       	}
        return uploadFiles;
        }catch(Exception e){
        	e.printStackTrace();
        }
        return null;
	}
	
	
	
	private String makeNickName(String saveName) throws ParseException {
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String returnName = saveName.substring(0, saveName.indexOf("."))+transFormat.format(Utils.getDate())+saveName.substring(saveName.indexOf("."));
		return returnName;
	}



	protected void download(HttpServletRequest request, HttpServletResponse response, String filename, String uploadFolder) 
			throws IOException {
		InputStream in = null;
		OutputStream os = null;
		
	    try{
            File file = new File(uploadFolder, filename);
            in = new FileInputStream(file);
	        String userAgent = request.getHeader("User-Agent");
	        response.reset() ;
	        response.setContentType("application/octet-stream");
	        response.setHeader("Content-Description", "JSP Generated Data");
        	if (userAgent != null && userAgent.indexOf("MSIE 5.5") > -1)  // MS IE 5.5 ??????
        		response.setHeader("Content-Disposition", "filename=" + URLEncoder.encode(filename, "UTF-8") + ";");
        	else if (userAgent != null && userAgent.indexOf("MSIE") > -1) // MS IE  6.x ??????
        		response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(filename, "UTF-8") + ";");
        	else  // ???????????? ?????????
        		response.setHeader("Content-Disposition", "attachment; filename=" + new String(filename.getBytes("euc-kr"), "latin1") + ";");
            response.setHeader ("Content-Length", ""+file.length() );
            os = response.getOutputStream();
            byte b[] = new byte[(int)file.length()];
            int leng = 0;
             
            while( (leng = in.read(b)) > 0 ){
                os.write(b,0,leng);
            }
	    } catch(FileNotFoundException e) {
	    	response.setContentType("text/html;charset=UTF-8");
	    	throw new FileNotFoundException(filename);
	    } catch (UnsupportedEncodingException e) {
	    	response.setContentType("text/html;charset=UTF-8");
			throw new UnsupportedEncodingException();
		} catch (IOException e) {
	    	response.setContentType("text/html;charset=UTF-8");
			throw new IOException();
		} finally {
			if(in != null) {
		        try {
					in.close();
				} catch (IOException e) {
					throw new IOException();
				}
			}
			if(os != null) {
		        try {
		        	os.close();
				} catch (IOException e) {
					throw new IOException();
				}
			}
	    }
	}
	
	protected void mailSendRequest(UserDataType user, String content) throws AddressException, MessagingException, ServletException, IOException, NullPointerException {
		// ???????????? ?????? smtp.naver.com ??? ???????????????.
		// Google??? ?????? smtp.gmail.com ??? ???????????????.
		String host = "smtp.naver.com";
		
		final String username = "ubigamelab";       //????????? ???????????? ??????????????????. @nave.com??? ???????????? ????????????.
		final String password = "E8-1423";   //????????? ????????? ??????????????? ??????????????????.
		int port=465; //????????????
		 
		// ?????? ??????
		String recipient = user.getEmail();    //?????? ????????? ??????????????? ??????????????????.
		String subject = "???????????????. ????????? ???????????? ?????? ?????????."; //?????? ?????? ??????????????????.
		String body=content;
		
		Properties props = System.getProperties(); // ????????? ?????? ?????? ?????? ??????
		 
		// SMTP ?????? ?????? ??????
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.trust", host);
		   
		//Session ??????
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
		String un=username;
		String pw=password;
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(un, pw);
			}
		});
		session.setDebug(true); //for debug
		   
		Message mimeMessage = new MimeMessage(session); //MimeMessage ??????
		mimeMessage.setFrom(new InternetAddress("ubigamelab@naver.com")); //????????? ?????? , ????????? ????????? ?????????????????? ?????? ??? ???????????????. ????????? ????????? ??? ????????? ??? ??????????????????.
		mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient)); //??????????????? //.TO ?????? .CC(??????) .BCC(????????????) ??? ??????


		mimeMessage.setSubject(subject);  //????????????
		mimeMessage.setText(body);        //????????????
		Transport.send(mimeMessage); //javax.mail.Transport.send() ??????
	}



	public List<File> uploadFile(HttpServletRequest request, HttpServletResponse response,
			String devlopmentUploadPath) {
		List<File> uploadFileList = new ArrayList<File>();
		return uploadFileList;
	}
}
