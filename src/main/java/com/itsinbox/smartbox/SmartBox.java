package com.itsinbox.smartbox;

import com.itsinbox.smartbox.gui.LoginFrame;
import com.itsinbox.smartbox.gui.SignXmlFrame;
import com.itsinbox.smartbox.utils.Utils;
import java.awt.Component;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SmartBox {

   public static final String VERSION = "1.0";
   public static final String ITO_BASE_URL = "http://10.1.65.31";
   public static final String ETO_BASE_URL = "http://test.purs.gov.rs";
   public static final String TEST_BASE_URL = "http://test.purs.gov.rs";
   public static final String PRODUCTION_BASE_URL = "http://eporezi.purs.gov.rs";
   public static final String VERSION_CHECK_URL = "http://eporezi.purs.gov.rs/upload/eporezi/version";
   public static final String APP_DOWNLOAD_BASE_URL = "http://eporezi.purs.gov.rs/upload/eporezi/eporezi_setup_v";
   public static final String LOG_FILE_NAME = "eporezi.log";
   public static final String NOTIFICATION_ENVIRNOMENT_NOT_DETECTED = "Грешка приликом читања параметара.";
   public static final String NOTIFICATION_LOGGING_IN = "Приступање порталу еПорези...";
   public static final String NOTIFICATION_LOGGING_IN_TEST = "Приступање Тестном окружењу...";
   public static final String NOTIFICATION_NO_READER = "Читач картица није пронађен.";
   public static final String NOTIFICATION_NO_CARD = "Картица није пронађена. Молим, убаците картицу у читач.";
   public static final String NOTIFICATION_SERVER_ERROR = "Грешка у комуникацији са сервером.";
   public static final String NOTIFICATION_STATUS_READY = "Читач и картица препознати.";
   public static final String NOTIFICATION_CARD_BROKEN = "Дисфункционална картица.";
   public static final String NOTIFICATION_CARD_BLOCKED = "Блокирана картица.";
   public static final String NOTIFICATION_NO_CERT_DATA = "Дошло је до грешке приликом читања сертификата. Молим, покушајте поново.";
   public static final String NOTIFICATION_WRONG_PIN = "Погрешан ПИН!";
   public static final String NOTIFICATION_PIN_VALID = "ПИН исправан! Учитавање...";
   public static final String NOTIFICATION_INVALID_CERT = "<html>Невалидан сертификат. Обратите се вашем<br>сертификационом телу за помоћ.</html>";
   public static final String WINDOW_TITLE = "еПорези ";
   private static SmartBox.Environment environment = SmartBox.Environment.UNKNOWN;
   private static String baseUrl;


   public static void main(String[] args) {
      setLaf();
      if(args != null && args.length > 0) {
         processUrl(args[0]);
      } else {
         showLogin((String)null);
      }

   }

   private static void processUrl(String uri) {
      try {
         uri = uri.substring(0, uri.length() - 1);
         Map ex = splitQuery(uri.replace("eporezi://", ""));
         String env = (String)ex.get("env");
         String loginKey = (String)ex.get("loginKey");
         String xmlUrl = (String)ex.get("xmlUrl");
         if(env == null) {
            environment = SmartBox.Environment.UNKNOWN;
         } else {
            byte servletUrl = -1;
            switch(env.hashCode()) {
            case 100768:
               if(env.equals("eto")) {
                  servletUrl = 1;
               }
               break;
            case 104612:
               if(env.equals("ito")) {
                  servletUrl = 2;
               }
               break;
            case 3449687:
               if(env.equals("prod")) {
                  servletUrl = 0;
               }
            }

            switch(servletUrl) {
            case 0:
               environment = SmartBox.Environment.PRODUCTION;
               baseUrl = "http://eporezi.purs.gov.rs";
               break;
            case 1:
               environment = SmartBox.Environment.ETO;
               baseUrl = "http://test.purs.gov.rs";
               break;
            case 2:
               environment = SmartBox.Environment.ITO;
               baseUrl = "http://10.1.65.31";
               break;
            default:
               environment = SmartBox.Environment.UNKNOWN;
               baseUrl = null;
            }
         }

         if(environment == SmartBox.Environment.UNKNOWN) {
            JOptionPane.showMessageDialog((Component)null, "Грешка приликом читања параметара.", "SmartBox", 0);
            return;
         }

         if(loginKey != null && loginKey.length() > 0) {
            showLogin(loginKey);
         } else if(xmlUrl != null && xmlUrl.length() > 0) {
            String reqKey = (String)ex.get("reqKey");
            String servletUrl1 = decodeString((String)ex.get("servletUrl"));
            String backUrl = decodeString((String)ex.get("backUrl"));
            String jmbgAuth = decodeString((String)ex.get("jmbgAuth"));
            String pibAuth = decodeString((String)ex.get("pibAuth"));
            String taxFormId = decodeString((String)ex.get("id"));
            String itemId = decodeString((String)ex.get("itemId"));
            showSignXml(reqKey, xmlUrl, servletUrl1, backUrl, jmbgAuth, pibAuth, taxFormId, itemId);
         }
      } catch (Exception var12) {
         Utils.logMessage("Error while processing URL: " + var12.getMessage());
         showLogin((String)null);
      }

   }

   private static void setLaf() {
      String os = System.getProperty("os.name");
      os = os.toLowerCase();
      Utils.logMessage("OS info: " + os);

      try {
         if(os.contains("windows")) {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
         } else if(os.contains("linux")) {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
         } else {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
         }
      } catch (UnsupportedLookAndFeelException var2) {
         Utils.logMessage("Error while setting window theme: " + var2.getMessage());
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }

   }

   private static void showLogin(String loginKey) {
      LoginFrame.init(environment, loginKey);
   }

   private static void showSignXml(String reqKey, String xmlUrl, String servletUrl, String backUrl, String jmbgAuth, String pibAuth, String taxFormId, String itemId) {
      SignXmlFrame frame = new SignXmlFrame(environment, baseUrl, reqKey, xmlUrl, servletUrl, backUrl, jmbgAuth, pibAuth, taxFormId, itemId);
      frame.setVisible(true);
      frame.init();
   }

   private static Map splitQuery(String query) throws Exception {
      LinkedHashMap query_pairs = new LinkedHashMap();
      String[] pairs = query.split("&");
      String[] var3 = pairs;
      int var4 = pairs.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String pair = var3[var5];
         int idx = pair.indexOf("=");
         query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
      }

      return query_pairs;
   }

   private static String decodeString(String string) {
      try {
         return URLDecoder.decode(string, "UTF-8");
      } catch (Exception var2) {
         return string;
      }
   }


   public static enum Environment {

      PRODUCTION("PRODUCTION", 0),
      ETO("ETO", 1),
      ITO("ITO", 2),
      UNKNOWN("UNKNOWN", 3);
      // $FF: synthetic field
      private static final SmartBox.Environment[] $VALUES = new SmartBox.Environment[]{PRODUCTION, ETO, ITO, UNKNOWN};


      private Environment(String var1, int var2) {}

   }
}
