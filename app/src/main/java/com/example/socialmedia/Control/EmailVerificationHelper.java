package com.example.socialmedia.Control;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailVerificationHelper {

    private static final String FROM_EMAIL = "socialmediaappmobile@gmail.com"; // Email used to send code verification
    private static final String APP_PASSWORD = "hhng yufj pfvm dsiz";  //  App Password
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  //100000<=code<=999999 code length is 6
        return String.valueOf(code);
    }

    //sent code verification to the user
    public void sendVerificationEmail(String userEmail, String verificationCode, EmailCallback callback) {
        // تنفيذ الكود في Thread منفصل باستخدام ExecutorService لتجنب التأثير على واجهة المستخدم
        executorService.execute(() -> {
            try {
                // إعدادات SMTP للبريد الإلكتروني (الخوادم التي يتم الاتصال بها لإرسال الرسائل)
                Properties properties = new Properties();
                properties.put("mail.smtp.host", "smtp.gmail.com");  // تحديد خادم SMTP (هنا نستخدم Gmail)
                properties.put("mail.smtp.port", "587");  // تحديد المنفذ (587 هو المنفذ الأكثر شيوعاً للبريد باستخدام TLS)
                properties.put("mail.smtp.auth", "true");  // تمكين المصادقة
                properties.put("mail.smtp.starttls.enable", "true");  // تمكين تشفير TLS للبريد
                properties.put("mail.smtp.connectiontimeout", "5000"); // مهلة الاتصال (5 ثوانٍ)
                properties.put("mail.smtp.timeout", "5000");           // مهلة القراءة (5 ثوانٍ)
                properties.put("mail.smtp.writetimeout", "5000");      // مهلة الكتابة (5 ثوانٍ)

                // تهيئة الجلسة الخاصة بالبريد الإلكتروني باستخدام البيانات السابقة
                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD); // بيانات حساب البريد
                    }
                });

                // إعداد الرسالة التي سيتم إرسالها
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(FROM_EMAIL));  // تعيين عنوان المرسل
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail)); // تعيين عنوان المستقبل
                message.setSubject("The verification code");  // تعيين موضوع الرسالة
                message.setText("Your code is: " + verificationCode);  // تعيين نص الرسالة الذي يتضمن الكود للتحقق

                // إرسال البريد الإلكتروني باستخدام Transport.send()
                try {
                    Transport.send(message);  // إرسال الرسالة
                    callback.onSuccess();  // إذا تم إرسال البريد بنجاح، استدعاء callback للإشارة إلى النجاح
                } catch (MessagingException e) {
                    // في حالة حدوث خطأ أثناء إرسال الرسالة
                    callback.onFailure(e);  // استدعاء callback للإشارة إلى الفشل مع التفاصيل
                }
            } catch (MessagingException e) {
                // إذا حدث خطأ أثناء إعداد الجلسة أو أي جزء آخر من العملية
                callback.onFailure(e);  // استدعاء callback للإشارة إلى الفشل مع التفاصيل
            }
        });
    }


    // 🔹 دمج الوظائف (توليد الرمز وإرساله)
    public String initiateEmailVerification(String userEmail, EmailCallback callback) {
        String verificationCode = generateVerificationCode();
        sendVerificationEmail(userEmail, verificationCode, callback);
        return verificationCode;
    }

    // 🔹 واجهة لمعالجة ردود الفعل عند الإرسال
    public interface EmailCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
