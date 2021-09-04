package com.github.melodyfx.dbchecksync;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @Author: huangzhimao
 * @Date: 2021-03-11
 */
public class SendMail {
  private Properties properties;
  private String subject;
  private String content;

  public SendMail() {
  }

  public SendMail(Properties properties,String subject,String content) {
    this.properties = properties;
    this.subject=subject;
    this.content=content;
  }


  public void sendMail() throws Exception{
    Properties prop = new Properties();
    String host=properties.getProperty("mail.host");
    String username=properties.getProperty("mail.username");
    String password=properties.getProperty("mail.password");
    prop.setProperty("mail.host", host);
    prop.setProperty("mail.transport.protocol", "smtp");
    prop.setProperty("mail.smtp.auth", "true");
    //使用JavaMail发送邮件的5个步骤
    //1、创建session
    Session session = Session.getInstance(prop);
    //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
    session.setDebug(false);
    //2、通过session得到transport对象
    Transport ts = session.getTransport();
    //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
    //ts.connect("smtp.qq.com", "1321497435@qq.com", "gqbikupqrizrgbfc");
    ts.connect(host, username, password);
    //4、创建邮件
    Message message = createSimpleMail(session,this.subject,this.content);
    //5、发送邮件
    ts.sendMessage(message, message.getAllRecipients());
    ts.close();
  }

  private MimeMessage createSimpleMail(Session session,String subject,String content)
      throws Exception {
    String username=properties.getProperty("mail.username");
    String rec=properties.getProperty("mail.recipients");
    String[] to=rec.split(",");
    //创建邮件对象
    MimeMessage message = new MimeMessage(session);
    //指明邮件的发件人
    message.setFrom(new InternetAddress(username));
    //指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
    //message.setRecipient(Message.RecipientType.TO, new InternetAddress(rec));
    InternetAddress[] sentTo=new InternetAddress[to.length];
    for (int i = 0; i  < to.length; i++) {
      sentTo[i]=new InternetAddress(to[i]);
    }
    message.setRecipients(Message.RecipientType.TO,sentTo);
    //邮件的标题
    message.setSubject(subject);
    //邮件的文本内容
    message.setContent(content, "text/html;charset=UTF-8");
    //返回创建好的邮件对象
    return message;
  }
}
